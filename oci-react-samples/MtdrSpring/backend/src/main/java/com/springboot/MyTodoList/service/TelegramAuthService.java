// Service class for managing Telegram authentication in the application
package com.springboot.MyTodoList.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.repository.EmployeeRepository;

@Service
public class TelegramAuthService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramAuthService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    // Authenticate a user by their Telegram ID and email
    // This method checks if the email exists in the database and associates it with the Telegram ID
    public boolean authenticate(long telegramId, String email) {
        try {
            Employee employee = employeeRepository.findByEmail(email);
            if (employee != null) {
                employee.setTelegramId(telegramId);
                employeeRepository.save(employee);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Authentication error", e);
            return false;
        }
    }

    // Check if a user is authenticated by their Telegram ID
    public boolean isAuthenticated(long telegramId) {
        return employeeRepository.findByTelegramId(telegramId) != null;
    }

    // Check if a user is a manager by their Telegram ID
    public boolean isManager(long telegramId) {
        Employee employee = employeeRepository.findByTelegramId(telegramId);
        return employee != null && employee.getManagerId() == null;
    }

    // Find employee by Telegram ID
    public Employee getEmployee(long telegramId) {
        return employeeRepository.findByTelegramId(telegramId);
    }
}
