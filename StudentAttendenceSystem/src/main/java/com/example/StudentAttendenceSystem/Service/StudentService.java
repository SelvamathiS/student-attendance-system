package com.example.StudentAttendenceSystem.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Reposistory.StudentRepository;
import jakarta.transaction.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveWithEncodedPassword(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        repo.save(student);
    }

    public Student update(Student student) {
        Student existing = repo.findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setDepartment(student.getDepartment());
        if (student.getPassword() != null && !student.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        return repo.save(existing);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public Student getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Student findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
    public long countStudents() {
        return repo.count();
    }

    @Transactional
    public void resetId() {
        repo.resetAutoIncrement();
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);

        // ✅ FIX: Reset auto increment only when table is empty
        long remaining = repo.count();
        if (remaining == 0) {
            repo.resetAutoIncrement();
        }
    }
}