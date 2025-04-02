package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotMessages;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private final ToDoItemService toDoItemService;
    private final String botToken;
    private final String botName;

    public ToDoItemBotController(ToDoItemService toDoItemService, 
                                 @Value("${telegram.bot.token}") String botToken, 
                                 @Value("${telegram.bot.name}") String botName) {
        super(botToken); // Pass the bot token to the parent class constructor
        this.toDoItemService = toDoItemService;
        this.botToken = botToken;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            logger.info("Received message: " + messageText + " from chatId: " + chatId);

            if (messageText.equals(BotCommands.START_COMMAND.getCommand())) {
                logger.info("Processing /start command");

                SendMessage messageToTelegram = new SendMessage();
                messageToTelegram.setChatId(String.valueOf(chatId));
                messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

                try {
                    execute(messageToTelegram);
                    logger.info("Response sent for /start command");
                } catch (TelegramApiException e) {
                    logger.error("Error sending response for /start command: " + e.getLocalizedMessage(), e);
                }
            }

			if(messageText.equals(BotCommands.FUNNY_MESSAGE.getCommand())) {
				logger.info("Processing /funny command");
				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(String.valueOf(chatId));
				messageToTelegram.setText(BotMessages.FUN.getMessage());
				try{
					execute(messageToTelegram);
					logger.info("Response sent for /funny command");
				} catch (TelegramApiException e) {
					logger.error("Error sending response for /funny command: " + e.getLocalizedMessage(), e);
				}
			}
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
