package com.springboot.MyTodoList.util;

public enum BotMessages {
	
	HELLO_MYTODO_BOT(
		"Hi! I'm MyTodoList Bot. Use menu to select an option"
		),
	BOT_REGISTERED_STARTED(
		"Bot registered and started succesfully!"
		),
	ITEM_COMPLETED(
		"Completed! Use /todolist or /start to continue."
		),
	ITEM_PENDING(
		"Task pending! Use /todolist or /start to continue."
		), 
	ITEM_CANCELLED(
		"Task cancelled! Use /todolist or /start to continue."
		),
	ITEM_REVIEWING(
		"Task under review! Use /todolist or /start to continue."
		),
	TYPE_NEW_TODO_ITEM(
		"Type your new task and send it."
		),
	NEW_ITEM_ADDED(
		"Task added! Use /todolist or /start to continue."
		),
	BYE(
		"Bye! Use /start to resume!"
		);

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
