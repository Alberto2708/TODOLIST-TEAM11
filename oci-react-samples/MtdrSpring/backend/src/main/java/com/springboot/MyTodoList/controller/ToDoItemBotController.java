package com.springboot.MyTodoList.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {
    
    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private final DataSource dataSource;
    private final String botName;

    @Autowired
    public ToDoItemBotController(DataSource dataSource,
                               @Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.name}") String botName) {
        super(botToken);
        this.dataSource = dataSource;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            String messageText = update.getMessage().getText();

            try (Connection conn = dataSource.getConnection()) {
                if (messageText.startsWith("/auth ")) {
                    handleAuthCommand(conn, chatId, user.getId(), messageText.substring(6).trim());
                } else if (!isUserAuthenticated(conn, user.getId())) {
                    sendMessage(chatId, "🔒 Please authenticate with:\n/auth your_email@example.com");
                } else {
                    handleTaskCommands(conn, chatId, user, messageText);
                }
            } catch (SQLException e) {
                sendMessage(chatId, "⚠️ Database error. Try again later.");
                logger.error("DB Error", e);
            }
        }
    }

    private void handleTaskCommands(Connection conn, long chatId, User user, String command) 
        throws SQLException {
        String[] args = command.split(" ");
        
        switch (args[0].toLowerCase()) {
            case "/newtask":
                handleNewTask(conn, chatId, user.getId(), command);
                break;
            case "/assigntask":
                if (args.length >= 3) {
                    assignTask(conn, chatId, user.getId(), Long.parseLong(args[1]), args[2]);
                } else {
                    sendMessage(chatId, "❌ Format: /assigntask [taskId] employee@example.com");
                }
                break;
            case "/starttask":
                if (args.length >= 2) {
                    updateTaskStatus(conn, chatId, Long.parseLong(args[1]), "IN_PROGRESS");
                }
                break;
            case "/completetask":
                if (args.length >= 2) {
                    updateTaskStatus(conn, chatId, Long.parseLong(args[1]), "COMPLETED");
                }
                break;
            case "/mytasks":
                listUserTasks(conn, chatId, user.getId());
                break;
            default:
                sendMessage(chatId, "🛠️ Available commands:\n" +
                    "/newtask \"Task name\" -s [sprintId] -h [hours]\n" +
                    "/assigntask [taskId] employee@example.com\n" +
                    "/starttask [taskId]\n" +
                    "/completetask [taskId]\n" +
                    "/mytasks");
        }
    }

    private void handleNewTask(Connection conn, long chatId, long telegramId, String command) 
            throws SQLException {
        try {
            // 1. Get employee ID from Telegram ID
            Integer employeeId = getEmployeeIdByTelegramId(conn, telegramId);
            if (employeeId == null) {
                sendMessage(chatId, "❌ Your account is not properly registered");
                return;
            }

            // 2. Check if user is a manager (MANAGER_ID IS NULL)
            if (!isManager(conn, employeeId)) {
                String debugInfo = getEmployeeDebugInfo(conn, employeeId);
                sendMessage(chatId, "⛔ Only managers can create tasks\n" + debugInfo);
                return;
            }

            // 3. Parse command
            String[] parts = command.split(" -");
            if (parts.length < 3) {
                sendMessage(chatId, "❌ Format: /newtask \"Task name\" -s [sprintId] -h [hours]");
                return;
            }
            
            String name = parts[0].replace("/newtask ", "").replace("\"", "");
            int sprintId = Integer.parseInt(parts[1].substring(1));
            int estHours = Integer.parseInt(parts[2].substring(1));

            // 4. Verify sprint exists
            if (!sprintExists(conn, sprintId)) {
                sendMessage(chatId, "❌ Invalid sprint ID");
                return;
            }

            // 5. Create task
            String sql = "INSERT INTO TODOUSER.TODOITEM " +
                "(NAME, STATUS, MANAGER_ID, SPRINT_ID, EST_HOURS, START_DATE) " +
                "VALUES (?, 'PENDING', ?, ?, ?, CURRENT_TIMESTAMP)";
                
            try (PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"TODOITEM_ID"})) {
                stmt.setString(1, name);
                stmt.setLong(2, employeeId);
                stmt.setInt(3, sprintId);
                stmt.setInt(4, estHours);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        sendMessage(chatId, "✅ Task #" + rs.getLong(1) + " created!");
                    }
                }
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Sprint ID and hours must be numbers");
        } catch (SQLException e) {
            sendMessage(chatId, "⚠️ Database error creating task");
            logger.error("Task creation failed", e);
        }
    }

    private void assignTask(Connection conn, long chatId, long assignerTelegramId, long taskId, String email) 
        throws SQLException {
        try {
            // Get assigner's employee ID (must be a manager)
            Integer assignerId = getEmployeeIdByTelegramId(conn, assignerTelegramId);
            if (assignerId == null || !isManager(conn, assignerId)) {
                sendMessage(chatId, "⛔ Only managers can assign tasks");
                return;
            }

            // Find assignee's employee record by email
            Integer assigneeId = findEmployeeByEmail(conn, email);
            if (assigneeId == null) {
                sendMessage(chatId, "❌ Employee with email " + email + " not found");
                return;
            }

            // Verify task exists and belongs to assigner's project
            if (!isTaskValidForAssignment(conn, taskId, assignerId)) {
                sendMessage(chatId, "❌ You can only assign tasks from your own projects");
                return;
            }

            // Check if task is already assigned to this employee
            if (isTaskAlreadyAssigned(conn, taskId, assigneeId)) {
                sendMessage(chatId, "ℹ️ Task is already assigned to this employee");
                return;
            }

            // Assign the task
            String sql = "INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, taskId);
                stmt.setInt(2, assigneeId);
                int updated = stmt.executeUpdate();
                sendMessage(chatId, updated > 0 
                    ? "✅ Task #" + taskId + " assigned to " + email 
                    : "❌ Failed to assign task");
                
                // Notify assignee if they have a Telegram ID
                notifyAssigneeIfAvailable(conn, assigneeId, taskId);
            }
        } catch (SQLException e) {
            handleDatabaseError(chatId, e, "assigning task");
        }
    }

    private void updateTaskStatus(Connection conn, long chatId, long taskId, String status) 
            throws SQLException {
        String sql = "UPDATE TODOUSER.TODOITEM SET STATUS = ? WHERE TODOITEM_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, taskId);
            int updated = stmt.executeUpdate();
            sendMessage(chatId, updated > 0 ? "✅ Task updated!" : "❌ Task not found");
        }
    }

    private void listUserTasks(Connection conn, long chatId, long telegramId) 
            throws SQLException {
        String sql = "SELECT t.TODOITEM_ID, t.NAME, t.STATUS, t.EST_HOURS " +
            "FROM TODOUSER.TODOITEM t " +
            "JOIN TODOUSER.ASSIGNEDDEV a ON t.TODOITEM_ID = a.TODOITEM_ID " +
            "JOIN TODOUSER.EMPLOYEE e ON a.EMPLOYEE_ID = e.EMPLOYEE_ID " +
            "WHERE e.TELEGRAM_ID = ? AND t.STATUS != 'COMPLETED'";
            
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, telegramId);
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder tasks = new StringBuilder("📋 Your Tasks:\n");
            while (rs.next()) {
                tasks.append(String.format(
                    "#%d - %s (%s, %dh)\n",
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getInt(4)
                ));
            }
            sendMessage(chatId, tasks.length() > 10 ? tasks.toString() : "No pending tasks found");
        }
    }

    private void handleAuthCommand(Connection conn, long chatId, long telegramId, String email) 
            throws SQLException {
        String sql = "UPDATE TODOUSER.EMPLOYEE SET TELEGRAM_ID = ? " +
                    "WHERE LOWER(EMAIL) = LOWER(?) AND TELEGRAM_ID IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, telegramId);
            stmt.setString(2, email);
            int updated = stmt.executeUpdate();
            
            if (updated > 0) {
                Integer employeeId = getEmployeeIdByTelegramId(conn, telegramId);
                boolean isManager = isManager(conn, employeeId);
                String role = isManager ? "manager" : "developer";
                sendMessage(chatId, "✅ Authentication successful! (Logged in as " + role + ")");
            } else {
                sendMessage(chatId, "❌ Failed: Email not found/already linked");
            }
        }
    }

    // Helper Methods
    private boolean isUserAuthenticated(Connection conn, long telegramId) throws SQLException {
        String sql = "SELECT 1 FROM TODOUSER.EMPLOYEE WHERE TELEGRAM_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, telegramId);
            return stmt.executeQuery().next();
        }
    }

    private Integer getEmployeeIdByTelegramId(Connection conn, long telegramId) throws SQLException {
        String sql = "SELECT EMPLOYEE_ID FROM TODOUSER.EMPLOYEE WHERE TELEGRAM_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, telegramId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    private boolean isManager(Connection conn, int employeeId) throws SQLException {
        String sql = "SELECT 1 FROM TODOUSER.EMPLOYEE WHERE EMPLOYEE_ID = ? AND MANAGER_ID IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            return stmt.executeQuery().next();
        }
    }

    private boolean sprintExists(Connection conn, int sprintId) throws SQLException {
        String sql = "SELECT 1 FROM TODOUSER.SPRINT WHERE SPRINT_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sprintId);
            return stmt.executeQuery().next();
        }
    }

    private String getEmployeeDebugInfo(Connection conn, int employeeId) throws SQLException {
        String sql = "SELECT NAME, EMAIL, MANAGER_ID FROM TODOUSER.EMPLOYEE WHERE EMPLOYEE_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format(
                    "User Info:\nName: %s\nEmail: %s\nManager ID: %s",
                    rs.getString("NAME"),
                    rs.getString("EMAIL"),
                    rs.getObject("MANAGER_ID")
                );
            }
            return "No employee found with ID: " + employeeId;
        }
    }

    private Integer findEmployeeByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT EMPLOYEE_ID FROM TODOUSER.EMPLOYEE WHERE LOWER(EMAIL) = LOWER(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    private boolean isTaskValidForAssignment(Connection conn, long taskId, Integer managerId) throws SQLException {
        String sql = "SELECT 1 FROM TODOUSER.TODOITEM WHERE TODOITEM_ID = ? AND MANAGER_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setInt(2, managerId);
            return stmt.executeQuery().next();
        }
    }

    private boolean isTaskAlreadyAssigned(Connection conn, long taskId, Integer employeeId) throws SQLException {
        String sql = "SELECT 1 FROM TODOUSER.ASSIGNEDDEV WHERE TODOITEM_ID = ? AND EMPLOYEE_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setInt(2, employeeId);
            return stmt.executeQuery().next();
        }
    }

    private void notifyAssigneeIfAvailable(Connection conn, Integer employeeId, long taskId) throws SQLException {
        String sql = "SELECT TELEGRAM_ID FROM TODOUSER.EMPLOYEE WHERE EMPLOYEE_ID = ? AND TELEGRAM_ID IS NOT NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long telegramId = rs.getLong(1);
                sendMessage(telegramId, "📬 You've been assigned to task #" + taskId);
            }
        }
    }

    private void handleDatabaseError(long chatId, SQLException e, String operation) {
        sendMessage(chatId, "⚠️ Database error while " + operation + ". Please try again.");
        logger.error("DB Error during " + operation, e);
    }

    private void sendMessage(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            logger.error("Message failed", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}