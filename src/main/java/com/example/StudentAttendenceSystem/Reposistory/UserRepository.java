package com.example.StudentAttendenceSystem.Reposistory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StudentAttendenceSystem.Model.Student;

// Students log in using email as username
public interface UserRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}