package com.digivision.employee.management.repository;

import com.digivision.employee.management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface  EmployeeRepository extends JpaRepository<Employee, UUID> {
}
