package org.gestionemploye.repository;


import org.gestionemploye.entity.Employee;
import org.gestionemploye.util.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);


    Optional<Employee> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Override
    Page<Employee> findAll(Pageable pageable);

  //  Employee findByUsername(String username);

    Employee findByUsernameAndStatus(String username, EmployeeStatus status);

    // All employee under supervision of given employee
    List<Employee> findAllBySupervisor(Employee employee);

    Page<Employee> findByFirstNameContainingOrMiddleNameContainingOrLastNameContaining(Pageable pageable, String firstName, String middleName, String lastName);
}
