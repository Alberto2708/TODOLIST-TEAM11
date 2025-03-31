package com.springboot.MyTodoList.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;




@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    //Returns all employees from the database
    public List<Employee> findAll(){
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    public List<Employee> findByProjectId(Integer projectId){
        List<Employee> employees = employeeRepository.findByProjectId(projectId);
        return employees;
    }

    public List<Employee> findByManagerId(Integer managerId){
        try{
            List<Employee> employees = employeeRepository.findByManagerId(managerId);
            return employees;
        }catch(Exception e){
            return null;
        } 
    }

    public ResponseEntity<Employee> findEmployeeByEmail(String email){
        try{
            Employee emp = employeeRepository.findByEmail(email);
        return new ResponseEntity<> (emp, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<Employee> findEmployeeById(Integer id){
        Optional<Employee> employeeData = employeeRepository.findById(id);
        if (employeeData.isPresent()){
            return new ResponseEntity<>(employeeData.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Employee> findEmployeeByProjectId(Integer projectId){
        try{
            List<Employee> employees = employeeRepository.findByProjectId(projectId);
            return employees;
        }catch(Exception e){
            return null;
        }
    }

    public Employee addEmployee(Employee employee){
        return employeeRepository.save(employee);
    }



        //It must not delete the Employee, just change the status
    public boolean deleteEmployee(Integer id){
        try{
            employeeRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Employee updateEmployee(Integer id, Employee emp){
        Optional<Employee> employeeData = employeeRepository.findById(id);
        if(employeeData.isPresent()){
            Employee employee = employeeData.get();
            employee.setID(id);

            if (emp.getName() != null){
                employee.setName(emp.getName());
            }
            if (emp.getManagerId() != null){
                employee.setManagerId(emp.getManagerId());
            }
            if (emp.getEmail() != null){
                employee.setEmail(emp.getEmail());
            }
            if (emp.getPassword() != null){
                employee.setPassword(emp.getPassword());
            }
            if (emp.getProjectId() != null){
                employee.setProjectId(emp.getProjectId());
            }
            if (emp.getTelegramId() != null){
                employee.setTelegramId(emp.getTelegramId());
            }
            return employeeRepository.save(employee);
        }else{
            return null;
        }
    }

    public Boolean isEmployeeTelegramIdExists(Integer id){
        try{
            Employee emp = employeeRepository.findById(id).get();
            if (emp.getTelegramId() != null){
                return true;
            } else {
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

}