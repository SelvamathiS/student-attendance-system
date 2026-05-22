package com.example.StudentAttendenceSystem.Reposistory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StudentAttendenceSystem.Model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUsername(String username);
}