package com.example.StudentAttendenceSystem.Controller;

import com.example.StudentAttendenceSystem.Model.AttendanceStatus;
import com.example.StudentAttendenceSystem.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("attendanceList",
                attendanceService.getByStatus(AttendanceStatus.PENDING));
        return "teacher-dashboard";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long attendanceId,
                               @RequestParam AttendanceStatus status) {
        attendanceService.updateStatus(attendanceId, status);
        return "redirect:/teacher/dashboard";
    }
}