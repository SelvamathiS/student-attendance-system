package com.example.StudentAttendenceSystem.Reposistory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.StudentAttendenceSystem.Model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByName(String name);
    Optional<Student> findByEmail(String email);

    // ✅ FIX: Reset auto increment after all students deleted
    @Modifying
    @Query(value = "ALTER TABLE student AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}