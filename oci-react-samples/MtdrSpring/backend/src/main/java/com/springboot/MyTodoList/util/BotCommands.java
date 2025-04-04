package com.springboot.MyTodoList.util;

public enum BotCommands {
    START_COMMAND("/start"),
    AUTH_COMMAND("/auth"),
    TODO_LIST("/todolist"),
    ADD_ITEM("/additem"),
    HELP("/help");

    private final String command;

    BotCommands(String enumCommand) {
        this.command = enumCommand;
    }

    public String getCommand() {
        return command;
    }
}