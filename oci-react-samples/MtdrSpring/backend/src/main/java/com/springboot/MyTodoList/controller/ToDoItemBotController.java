package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.service.TelegramAuthService;
import com.springboot.MyTodoList.service.TelegramTaskService;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private Map<Long, TaskCreationState> taskCreationStates = new HashMap<>();

    private class TaskCreationState {
        String name;
        Integer estHours;
        String description;
        OffsetDateTime deadline;
    }
    private static final String HELP_TEXT = 
        "🤖 Bot Commands:\n\n" +
        "/auth email - Authenticate\n" +
        "/menu - Show main menu\n" +
        "/mytasks - List your tasks\n" +
        "/mykpis - Show your KPIs\n" +
        "/newtask \"Name\" -s SprintID -h Hours - Create task\n" +
        "/assigntask TaskID email - Assign task\n" +
        "/starttask TaskID - Start task\n" +
        "/completetask TaskID - Complete task";


    @Autowired
    private TelegramTaskService taskService;
    
    @Autowired
    private TelegramAuthService authService;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.name}")
    private String botName;

    @Override
    public String getBotUsername() {
        return botName;
    }

    
    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
public void onUpdateReceived(Update update) {
    if (!update.hasMessage() || !update.getMessage().hasText()) return;

    long chatId = update.getMessage().getChatId();
    User user = update.getMessage().getFrom();
    String command = update.getMessage().getText();

    try {
        if (taskCreationStates.containsKey(chatId)) {
            handleTaskCreationStep(chatId, user.getId(), command);
            return;
        }
        
        if ("/start".equals(command)) {
            String sprintInfo = taskService.getCurrentSprintInfo();
            sendMessage(chatId, sprintInfo);
            showMainMenu(chatId, user.getId());
        }
        else if (command.startsWith("/auth ")) {
            String email = command.substring(6).trim();
            handleAuthCommand(chatId, user.getId(), email);
        }
        else {
            handleTaskCommand(chatId, user.getId(), command);
        }
    } catch (Exception e) {
        handleError(chatId, e);
    }
}

    private void showMainMenu(long chatId, long telegramId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("📋 Main Menu - Select an option:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        
        // Row 1
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📝 My Tasks"));
        row1.add(new KeyboardButton("📊 My KPIs"));
        keyboard.add(row1);
        
        if (authService.isManager(telegramId)) {
            // Row 2 - Manager only buttons
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("➕ New Task"));
            row2.add(new KeyboardButton("👥 Assign Task"));
            row2.add(new KeyboardButton("📂 Completed Tasks"));
            keyboard.add(row2);
        }
        
        // Row 3
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("✅ Complete Task"));
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send menu", e);
        }
    }

    private void handleNewTaskCommand(long chatId, long telegramId, String Command) {
        if (!authService.isManager(telegramId)) {
            sendMessage(chatId, "⛔ Only managers can create tasks");
            return;
        }
        
        taskCreationStates.put(chatId, new TaskCreationState());
        sendMessage(chatId, "📝 Enter task name:");
    }

    private void handleTaskCommand(long chatId, long telegramId, String command) {
        try {
            // Check authentication for all commands except help
            if (!"/help".equals(command)&& !authService.isAuthenticated(telegramId)) {
                sendLoginPrompt(chatId);
                return;
            }
            if ("📝 My Tasks".equals(command) || "/mytasks".equals(command)) {
                String tasks = taskService.getUserTasks(telegramId);
                sendMessage(chatId, tasks);
            } 
            else if ("📊 My KPIs".equals(command) || "/mykpis".equals(command)) {
                String kpis = taskService.getDeveloperKPIs(telegramId);
                sendMessage(chatId, kpis);
            }
            else if (command.startsWith("➕ New Task") || command.startsWith("/newtask")) {
                if (!authService.isManager(telegramId)) {
                    sendMessage(chatId, "⛔ Only managers can create tasks");
                    return;
                }
                handleNewTaskCommand(chatId, telegramId, command);
            }
            else if (command.startsWith("👥 Assign Task") || command.startsWith("/assigntask")) {
                if (!authService.isManager(telegramId)) {
                    sendMessage(chatId, "⛔ Only managers can assign tasks");
                    return;
                }
                handleAssignTaskCommand(chatId, telegramId, command);
            }
            else if (command.startsWith("✅ Complete Task") || command.startsWith("/completetask")) {
                handleCompleteTaskCommand(chatId, command);
            }
            else if ("📂 Completed Tasks".equals(command)) {
                if (!authService.isManager(telegramId)) {
                    sendMessage(chatId, "⛔ Only managers can view completed tasks");
                    return;
                }
                handleCompletedTasksCommand(chatId, telegramId);
            }
            else {
                sendMessage(chatId, "❌ Unknown command. Type /help for options.");
            }
        } catch (Exception e) {
            handleError(chatId, e);
        }
    }

    private void handleTaskCreationStep(long chatId, long telegramId, String input) {
    TaskCreationState state = taskCreationStates.get(chatId);
    
    if (state.name == null) {
        state.name = input;
        sendMessage(chatId, "⏱️ Enter estimated hours for this task:");
    } 
    else if (state.estHours == null) {
        try {
            state.estHours = Integer.parseInt(input);
            sendMessage(chatId, "📝 Enter task description:");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Please enter a valid number for hours");
        }
    }
    else if (state.description == null) {
        state.description = input;
        sendMessage(chatId, "📅 Enter deadline in format YYYY-MM-DD:");
    }
    else if (state.deadline == null) {
        try {
            state.deadline = LocalDate.parse(input).atStartOfDay().atOffset(ZoneOffset.UTC);
            String result = taskService.createNewTask(
                telegramId, 
                state.name, 
                state.estHours,
                state.description,
                state.deadline
            );
            sendMessage(chatId, result);
            taskCreationStates.remove(chatId);
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "❌ Invalid date format. Please use YYYY-MM-DD");
        } catch (Exception e) {
            sendMessage(chatId, "❌ Failed to create task");
            taskCreationStates.remove(chatId);
            logger.error("Task creation failed", e);
        }
    }
}


    private void handleAssignTaskCommand(long chatId, long telegramId, String command) {
        try {
            if ("👥 Assign Task".equals(command)) {
                sendMessage(chatId, "👥 Format: /assigntask TaskID email@example.com");
                return;
            }
            
            String[] parts = command.split(" ");
            if (parts.length < 3) {
                sendMessage(chatId, "❌ Format: /assigntask TaskID email@example.com");
                return;
            }
            
            long taskId = Long.parseLong(parts[1]);
            String email = parts[2];
            
            String result = taskService.assignTask(telegramId, taskId, email);
            sendMessage(chatId, result);
        } catch (Exception e) {
            sendMessage(chatId, "❌ Invalid format. Use: /assigntask TaskID email@example.com");
        }
    }

    private void handleCompleteTaskCommand(long chatId, String command) {
        try {
            if ("✅ Complete Task".equals(command)) {
                sendMessage(chatId, "✅ Format: /completetask TaskID");
                return;
            }
            
            String[] parts = command.split(" ");
            if (parts.length < 2) {
                sendMessage(chatId, "❌ Format: /completetask TaskID");
                return;
            }
            
            long taskId = Long.parseLong(parts[1]);
            String result = taskService.completeTask(taskId);
            sendMessage(chatId, result);
        } catch (Exception e) {
            sendMessage(chatId, "❌ Invalid task ID");
        }
    }

    private void handleCompletedTasksCommand(long chatId, long telegramId) {
        try {
            String completedTasks = taskService.getAllCompletedTasks();
            sendMessage(chatId, completedTasks);
        } catch (Exception e) {
            sendMessage(chatId, "❌ Failed to retrieve completed tasks");
            logger.error("Error retrieving completed tasks", e);
        }
    }

    private void sendLoginPrompt(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🔒 Please authenticate with:\n/auth your_email@company.com");
        
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/auth email@company.com"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send login prompt", e);
        }
    }

    private void handleAuthCommand(long chatId, long telegramId, String email) {
        try {
            boolean success = authService.authenticate(telegramId, email);
            if (success) {
                String role = authService.isManager(telegramId) ? "manager" : "developer";
                sendMessage(chatId, "✅ Authentication successful! (Logged in as " + role + ")");
                showMainMenu(chatId, telegramId);
            } else {
                sendMessage(chatId, "❌ Authentication failed. Check your email or contact admin");
            }
        } catch (Exception e) {
            sendMessage(chatId, "⚠️ Authentication error. Try again later");
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chat {}: {}", chatId, e.getMessage());
        }
    }

    private void handleError(long chatId, Exception e) {
        logger.error("Error handling update", e);
        sendMessage(chatId, "⚠️ An error occurred. Try again or contact support");
    }
}