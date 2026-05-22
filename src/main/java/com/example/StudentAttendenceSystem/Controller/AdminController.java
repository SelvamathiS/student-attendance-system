package com.example.StudentAttendenceSystem.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Service.AttendanceService;
import com.example.StudentAttendenceSystem.Service.StudentService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("attendanceList", attendanceService.getAll());
        model.addAttribute("presentCount", attendanceService.getPresentCount());
        model.addAttribute("absentCount", attendanceService.getAbsentCount());
        model.addAttribute("totalStudents", students.size());
        return "admin-dashboard";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("newStudent", new Student());
        return "student-list";
    }

    @GetMapping("/students/add")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveWithEncodedPassword(student);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getById(id);
        if (student == null) return "redirect:/admin/dashboard";
        model.addAttribute("student", student);
        return "edit-student";
    }

    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute Student student) {
        studentService.update(student);
        return "redirect:/admin/dashboard";
    }

    // ✅ FIX: Keep only POST delete — removed all GET deletes (security risk)
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return "redirect:/admin/dashboard";
    }
 // ✅ Reset auto increment — only works when table is empty
    @PostMapping("/students/reset-id")
    public String resetId() {
        long count = studentService.countStudents();
        if (count == 0) {
            studentService.resetId();
        }
        return "redirect:/admin/dashboard";
    }
}