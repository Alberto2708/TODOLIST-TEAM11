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

    public boolean authenticate(long telegramId, String email) {
        try {
            Employee employee = employeeRepository.findByEmail(email);
            if(employee != null) {
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

    public boolean isAuthenticated(long telegramId) {
        return employeeRepository.findByTelegramId(telegramId) != null;
    }

    public boolean isManager(long telegramId) {
        Employee employee = employeeRepository.findByTelegramId(telegramId);
        return employee != null && employee.getManagerId() == null;
    }

    public Employee getEmployee(long telegramId) {
        return employeeRepository.findByTelegramId(telegramId);
    }
}