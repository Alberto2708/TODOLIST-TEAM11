package com.springboot.MyTodoList.util;

public enum BotMessages {
    HELLO_MYTODO_BOT("Welcome! Authenticate with /auth your_email@example.com"),
    AUTH_SUCCESS("✅ Authentication successful!"),
    AUTH_FAILED("❌ Authentication failed. Check your email"),
    NOT_AUTHENTICATED("🔒 Please authenticate first"),
    TYPE_NEW_TODO_ITEM("Type your new todo item:"),
    ITEM_ADDED("Item added successfully!"),
    DB_ERROR("⚠️ Database error occurred");

    private final String message;

    BotMessages(String enumMessage) {
        this.message = enumMessage;
    }

    public String getMessage() {
        return message;
    }
}