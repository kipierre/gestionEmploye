package org.gestionemploye.security;


import org.gestionemploye.entity.Employee;
import org.gestionemploye.exceptions.ResourceNotFoundException;
import org.gestionemploye.repository.EmployeeRepository;

import org.gestionemploye.util.enums.EmployeeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by admin on 02/08/17.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
     EmployeeRepository employeeRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + username)
                );

        return  UserPrincipal.create(employee);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
       Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Employe", "id", id)
        );

        return UserPrincipal.create(employee);
    }
}
