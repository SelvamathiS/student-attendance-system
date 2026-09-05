package com.example.StudentAttendenceSystem.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.StudentAttendenceSystem.Model.Student;
import com.example.StudentAttendenceSystem.Model.User;
import com.example.StudentAttendenceSystem.Service.AuthService;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Student student) {

        Student dbUser = authService.login(student);

        if (dbUser != null) {
            switch (dbUser.getRole()) {
                case "STUDENT":
                    return "redirect:/student/dashboard";
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "TEACHER":
                    return "redirect:/teacher/dashboard";
                default:
                    return "redirect:/login";
            }
        }

        return "redirect:/login?error";
    }
}