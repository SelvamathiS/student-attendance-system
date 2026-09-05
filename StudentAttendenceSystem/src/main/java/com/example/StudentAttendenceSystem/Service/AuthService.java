package com.example.StudentAttendenceSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Reposistory.StudentRepository;
@Service
public class AuthService {

    @Autowired
    private StudentRepository repo;

    public Student login(Student student) {

        Student dbStudent = repo.findByEmail(student.getEmail()).orElse(null);

        if (dbStudent != null && dbStudent.getPassword().equals(student.getPassword())) {
            return dbStudent;
        }

        return null;
    }
}