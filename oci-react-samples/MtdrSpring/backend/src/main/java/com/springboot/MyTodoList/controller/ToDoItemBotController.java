package com.springboot.MyTodoList.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {
    
    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private final DataSource dataSource;
    private final String botName; // Added missing field declaration

    @Autowired
    public ToDoItemBotController(DataSource dataSource,
                               @Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.name}") String botName) {
        super(botToken);
        this.dataSource = dataSource;
        this.botName = botName; // Now properly storing the value
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        long chatId = update.getMessage().getChatId();
        long telegramId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();

        try (Connection conn = dataSource.getConnection()) {
            if (messageText.startsWith("/auth ")) {
                handleAuthCommand(conn, chatId, telegramId, messageText.substring(6).trim());
            } else if (!isUserAuthenticated(conn, telegramId)) {
                sendMessage(chatId, "🔒 Please authenticate with:\n/auth your_email@example.com");
            } else {
                handleAuthenticatedCommand(chatId, messageText);
            }
        } catch (SQLException e) {
            logger.error("Database error for user {}: {}", telegramId, e.getMessage());
            sendMessage(chatId, "⚠️ Temporary system error. Please try later.");
        }
    }

    private void handleAuthCommand(Connection conn, long chatId, long telegramId, String email) 
            throws SQLException {
        if (email.isEmpty() || !email.contains("@")) {
            sendMessage(chatId, "❌ Invalid email format");
            return;
        }

        String sql = "UPDATE TODOUSER.EMPLOYEE SET TELEGRAM_ID = ? WHERE LOWER(EMAIL) = LOWER(?) AND TELEGRAM_ID IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, telegramId);
            stmt.setString(2, email);
            
            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                logger.info("Linked Telegram ID {} to email {}", telegramId, email);
                sendMessage(chatId, "✅ Account linked successfully!");
            } else {
                sendMessage(chatId, "❌ Failed to link. Please check:\n"
                    + "1. Email is correct\n"
                    + "2. Account isn't already linked");
            }
        }
    }

    private boolean isUserAuthenticated(Connection conn, long telegramId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT 1 FROM TODOUSER.EMPLOYEE WHERE TELEGRAM_ID = ?")) {
            stmt.setLong(1, telegramId);
            return stmt.executeQuery().next();
        }
    }

    private void handleAuthenticatedCommand(long chatId, String command) {
        // Add your task management logic here
        sendMessage(chatId, "🛠️ Authenticated command: " + command);
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