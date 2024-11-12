package com.digivision.employee.management.service;

import com.digivision.employee.management.exception.EmployeeNotFoundException;
import com.digivision.employee.management.exception.InvalidInputException;
import com.digivision.employee.management.exception.ThirdPartyException;
import com.digivision.employee.management.model.Employee;
import com.digivision.employee.management.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailValidationService emailValidationService;

    @Autowired
    private DepartmentValidationService departmentValidationService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Transactional
    public Employee createEmployee(Employee employee) throws ThirdPartyException {
        UUID id = UUID.randomUUID();
        Thread.currentThread().setName("emp-" + id.toString());
        logger.info("Starting employee creation for email: {}", employee.getEmail());
        employee.setId(id);
        if (!emailValidationService.isValidEmail(employee.getEmail())) {
            logger.error("Invalid email: {}", employee.getEmail());
            throw new InvalidInputException("Invalid email address.");
        }
//
//        if (!departmentValidationService.isValidDepartment(employee.getDepartment())) {
//            throw new InvalidInputException("Invalid department.");
//        }

        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Employee saved successfully");
        emailNotificationService.sendEmployeeCreationNotification(employee.getEmail(), employee.getFirstName() + " " + employee.getLastName());
        logger.info("Employee created successfully");
        return savedEmployee;
    }

    public Employee getEmployeeById(UUID id) {
        logger.info("Start getting employee");
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) {
            logger.error("No employee found with id: {}", id);
            throw new EmployeeNotFoundException("Employee not found");
        }
        return employee.get();
    }

    public Employee updateEmployee(UUID id, Employee employeeDetails) {
        logger.info("Start updating employee");
        Employee employee = getEmployeeById(id);
        logger.debug("New employee details: {}", employeeDetails);
        logger.debug("Current employee details: {}", employee);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setSalary(employeeDetails.getSalary());
        employee = employeeRepository.save(employee);
        logger.info("Employee updated successfully");
        return employee;
    }

    public void deleteEmployee(UUID id) {
        logger.info("Start getting employee");
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
        logger.info("Deleting employee ended");

    }

    public List<Employee> getAllEmployees() {
        logger.info("Start getting employees");
        return employeeRepository.findAll();
    }

}