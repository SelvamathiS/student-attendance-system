package com.example.StudentAttendenceSystem.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.StudentAttendenceSystem.Model.Attendance;
import com.example.StudentAttendenceSystem.Model.AttendanceExcelExporter;
import com.example.StudentAttendenceSystem.Model.AttendanceStatus;
import com.example.StudentAttendenceSystem.Service.AttendanceService;
import com.example.StudentAttendenceSystem.Service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/mark-attendance")
    public String markPage(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "mark-attendance";
    }

    @PostMapping("/attendance/save")
    public String save(@RequestParam Long studentId,
                       @RequestParam String status,
                       RedirectAttributes redirectAttributes) {
        try {
            AttendanceStatus attendanceStatus =
                    AttendanceStatus.valueOf(status.trim().toUpperCase());
            attendanceService.markAttendance(studentId, attendanceStatus);
            redirectAttributes.addFlashAttribute("success", "Attendance marked!");
        } catch (RuntimeException e) {
            // ✅ Show error if duplicate
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mark-attendance";
    }

    // ✅ New: filter by day + month + year
    @GetMapping("/attendance/filter")
    public String byDayMonthYear(@RequestParam(required = false) Integer day,
                                  @RequestParam(required = false) Integer month,
                                  @RequestParam(required = false) Integer year,
                                  Model model) {
        if (day != null && month != null && year != null) {
            model.addAttribute("attendanceList",
                    attendanceService.getByDayMonthYear(day, month, year));
        } else if (month != null && year != null) {
            model.addAttribute("attendanceList",
                    attendanceService.getByMonth(month, year));
        } else {
            model.addAttribute("attendanceList", attendanceService.getAll());
        }
        return "view-attendance";
    }
    @GetMapping("/view-attendance")
    public String viewAttendance(Model model) {
        model.addAttribute("attendanceList", attendanceService.getAll());
        return "view-attendance";
    }

    // FIX: Use proper repository query instead of full in-memory stream filter
    @GetMapping("/attendance/month")
    public String byMonth(@RequestParam int month,
                          @RequestParam int year,
                          Model model) {
        model.addAttribute("attendanceList",
                attendanceService.getByMonth(month, year));
        return "view-attendance";
    }
    @GetMapping("/attendance/export")
    public void exportToExcel(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) throws IOException {

        List<Attendance> list;

        // Export filtered or all records
        if (day != null && month != null && year != null) {
            list = attendanceService.getByDayMonthYear(day, month, year);
        } else if (month != null && year != null) {
            list = attendanceService.getByMonth(month, year);
        } else {
            list = attendanceService.getAll();
        }

        AttendanceExcelExporter exporter = new AttendanceExcelExporter(list);
        exporter.export(response);
    }
    @PostMapping("/attendance/checkout/{id}")
    public String checkOut(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            attendanceService.markCheckOut(id);
            redirectAttributes.addFlashAttribute("success", "Check out marked!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/view-attendance";
    }
    
}