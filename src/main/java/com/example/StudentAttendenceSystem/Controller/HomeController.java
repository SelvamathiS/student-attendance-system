package com.example.StudentAttendenceSystem.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        // If not authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Check user role and redirect accordingly
        var authorities = authentication.getAuthorities();
        for (var authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_TEACHER")) {
                return "redirect:/teacher/dashboard";
            } else if (role.equals("ROLE_STUDENT")) {
                return "redirect:/student/dashboard";
            }
        }

        return "redirect:/login";
    }
}