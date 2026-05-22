package com.example.StudentAttendenceSystem.Security;

import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Reposistory.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Admin hardcoded
        if ("admin@gmail.com".equals(email)) {
            return User.withUsername("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles("ADMIN")
                    .build();
        }

        // Teacher hardcoded
        if ("teacher@gmail.com".equals(email)) {
            return User.withUsername("teacher@gmail.com")
                    .password(passwordEncoder.encode("teacher123"))
                    .roles("TEACHER")
                    .build();
        }

        // Student from database
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return User.withUsername(student.getEmail())
                .password(student.getPassword())
                .roles(student.getRole())
                .build();
    }
}