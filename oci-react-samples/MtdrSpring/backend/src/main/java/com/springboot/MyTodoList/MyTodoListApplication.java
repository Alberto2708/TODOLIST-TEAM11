package com.springboot.MyTodoList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.springboot.MyTodoList.controller.ToDoItemBotController;

@SpringBootApplication
public class MyTodoListApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MyTodoListApplication.class, args);
        
        try {
            // Get the bot controller by TYPE instead of name
            ToDoItemBotController botController = context.getBean(ToDoItemBotController.class);
            
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botController);
            System.out.println("Bot started successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error starting bot: " + e.getMessage());
            System.exit(1);
        }
    }
}