package com.example.StudentAttendenceSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.StudentAttendenceSystem.Model.AttendanceStatus;
import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Service.AttendanceService;
import com.example.StudentAttendenceSystem.Service.StudentService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentService studentService;

    // FIX: Use @AuthenticationPrincipal to get the currently logged-in student
    //      instead of always loading getAllStudents().get(0)
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        Student student = studentService.findByEmail(userDetails.getUsername());
        if (student == null) {
            return "redirect:/login?error";
        }
        model.addAttribute("student", student);
        // FIX: Show this student's own attendance counts, not global counts
        model.addAttribute("presentCount",
                attendanceService.getPresentCountByStudent(student.getId()));
        model.addAttribute("absentCount",
                attendanceService.getAbsentCountByStudent(student.getId()));
        return "student-dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "student-login";
    }

    @GetMapping("/mark-attendance")
    public String markAttendancePage(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        Student student = studentService.findByEmail(userDetails.getUsername());
        model.addAttribute("student", student);
        return "mark-attendance-student";
    }

    @PostMapping("/save-attendance")
    public String saveAttendance(@RequestParam Long studentId) {
        attendanceService.markAttendance(studentId, AttendanceStatus.PENDING);
        return "redirect:/student/dashboard";
    }
}