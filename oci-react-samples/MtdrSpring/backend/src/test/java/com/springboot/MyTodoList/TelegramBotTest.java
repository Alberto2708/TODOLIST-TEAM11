package com.springboot.MyTodoList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.service.TelegramAuthService;
import com.springboot.MyTodoList.service.TelegramTaskService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TelegramBotTest {


    @Mock
    private TelegramTaskService taskService;

    @Mock
    private TelegramAuthService authService;

    @Spy
    @InjectMocks
    private ToDoItemBotController bot;


    private Update mockUpdate(long chatId, long userId, String messageText) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        User user = new User();

        chat.setId(chatId);
        user.setId(userId);
        message.setChat(chat);
        message.setFrom(user);
        message.setText(messageText);
        update.setMessage(message);
        return update;
    }



@Test
@Order(1)
void testStartCommand() throws Exception {
    // Arrange
    long chatId = 1001L;
    long userId = 2002L;
    String expectedResponse = "🏃 Current Sprint\n\nID: 122\nName: Sprint 3 (week 5 and 6)\nStart: 2025-04-28\nEnd: 2025-05-19";
    String expectedResponse2 = "📋 Main Menu - Select an option:";

    // 🔧 Este mock es crucial
    when(taskService.getCurrentSprintInfo()).thenReturn(expectedResponse);

    // Para evitar que realmente mande un mensaje al bot
    doReturn(null).when(bot).execute(any(SendMessage.class));

    Update update = mockUpdate(chatId, userId, "/start");

    // Act
    bot.onUpdateReceived(update);

    // Assert
    ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
    verify(bot, times(2)).execute(captor.capture());
    
    List<SendMessage> messages = captor.getAllValues();

    assertEquals(expectedResponse,messages.get(0).getText());
    assertEquals(expectedResponse2, messages.get(1).getText());
}


@Test
@Order(2)
void testMyTasksCommand() throws Exception {
    // Arrange
    long chatId = 1001L;
    long userId = 2002L;
    String email = "uniqueemail@gmail.com";

     // Simula autenticación
    when(authService.authenticate(userId, email)).thenReturn(true);
    when(authService.isAuthenticated(userId)).thenReturn(true);
    when(authService.isManager(userId)).thenReturn(true); // simula que es empleado

    
    // Mock de las respuestas del servicio de tareas
    String authResponse = "✅ Authentication successful! (Logged in as manager)";
    String taskResponse2 = "📋 Main Menu - Select an option:";
    String taskResponse3 = "📭 You have no tasks in current sprint";
    when(taskService.getUserTasks(userId)).thenReturn(taskResponse3);

    // Mock execute
    doReturn(null).when(bot).execute(any(SendMessage.class));

    Update authUpdate = mockUpdate(chatId, userId, "/auth " + email);
    bot.onUpdateReceived(authUpdate);

    // Simula /mytasks después
    Update tasksUpdate = mockUpdate(chatId, userId, "/mytasks");
    bot.onUpdateReceived(tasksUpdate);

    // Captura y verifica los mensajes enviados
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(bot, times(3)).execute(messageCaptor.capture());

    List<SendMessage> messages = messageCaptor.getAllValues();

    for (SendMessage message : messages) {
        System.out.println("Message sent: " + message.getText());
    }

    // Verifica que el último mensaje sea el de las tareas
    assertEquals(authResponse, messages.get(0).getText());
    assertEquals(taskResponse2, messages.get(1).getText());
    assertEquals(taskResponse3, messages.get(2).getText());
}


    @Test
    @Order(3)
    void testCompletedTasksCommand() throws Exception {
        long chatId = 1001L;
        long userId = 2002L;

        // Simulate manager
        when(authService.isAuthenticated(userId)).thenReturn(true);
        when(authService.isManager(userId)).thenReturn(true);

        String expectedResponse = "✅ Completed Tasks:\n#1 - Fix login\n#2 - Refactor service";

        when(taskService.getAllCompletedTasks()).thenReturn(expectedResponse);
        doReturn(null).when(bot).execute(any(SendMessage.class));

        Update update = mockUpdate(chatId, userId, "📂 Completed Tasks");
        bot.onUpdateReceived(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, atLeastOnce()).execute(captor.capture());

        List<SendMessage> messages = captor.getAllValues();
        assertEquals(expectedResponse, messages.get(messages.size() - 1).getText());
    }

    @Test
    @Order(4)
    void testCreateTaskFlow() throws Exception {
        long chatId = 1001L;
        long userId = 2002L;

        // Setup manager user
        Employee manager = new Employee();
        manager.setID(1);
        when(authService.getEmployee(userId)).thenReturn(manager);
        when(authService.isAuthenticated(userId)).thenReturn(true);
        when(authService.isManager(userId)).thenReturn(true);
        doReturn(null).when(bot).execute(any(SendMessage.class));

        // Simulate flow
        bot.onUpdateReceived(mockUpdate(chatId, userId, "/newtask"));
        bot.onUpdateReceived(mockUpdate(chatId, userId, "Task title"));
        bot.onUpdateReceived(mockUpdate(chatId, userId, "5"));
        bot.onUpdateReceived(mockUpdate(chatId, userId, "Fix bug"));

        String expectedFinalResponse = "✅ Task #12 created!\n#12 - Task title\nStatus: PENDING\nHours: 5\nDescription: Fix bug\nDeadline: 2025-12-31";
        lenient().when(taskService.createNewTask(eq(userId), eq("Task title"), eq(5.0), eq("Fix bug"), any()))
            .thenReturn(expectedFinalResponse);

        bot.onUpdateReceived(mockUpdate(chatId, userId, "2025-12-31"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, atLeastOnce()).execute(captor.capture());

        boolean found = captor.getAllValues().stream()
            .anyMatch(msg -> expectedFinalResponse.equals(msg.getText()));
        assertEquals(true, found, "Expected task creation confirmation message");
    }
}