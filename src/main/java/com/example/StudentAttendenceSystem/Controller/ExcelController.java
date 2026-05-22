package com.example.StudentAttendenceSystem.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.StudentAttendenceSystem.Model.Attendance;
import com.example.StudentAttendenceSystem.Model.AttendanceStatus;
import com.example.StudentAttendenceSystem.Service.AttendanceService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private AttendanceService attendanceService;

    // ================= FULL EXPORT =================
    @GetMapping("/attendance")
    public void exportAttendance(HttpServletResponse response) throws Exception {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=attendance.xlsx");

        List<Attendance> attendanceList = attendanceService.getAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Header row
        String[] headers = {"ID", "Name", "Date", "Status", "Check-In", "Check-Out"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ FIX: Added null check before writing rows
        int rowNum = 1;
        for (Attendance att : attendanceList) {
            if (att == null || att.getStudent() == null) continue;

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(att.getStudent().getId());
            row.createCell(1).setCellValue(att.getStudent().getName());
            row.createCell(2).setCellValue(String.valueOf(att.getDate()));
            row.createCell(3).setCellValue(String.valueOf(att.getStatus()));
            row.createCell(4).setCellValue(att.getCheckIn() != null ? String.valueOf(att.getCheckIn()) : "-");
            row.createCell(5).setCellValue(att.getCheckOut() != null ? String.valueOf(att.getCheckOut()) : "-");
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }

    // ================= MONTHLY EXPORT =================
    @GetMapping("/attendance/month")
    public void exportMonthly(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendance_" + month + "_" + year + ".xlsx");

        // ✅ FIX: Uses new MONTH/YEAR query — no longer returns empty list
        List<Attendance> list = attendanceService.getByMonth(month, year);

        Workbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle percentStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        percentStyle.setDataFormat(format.getFormat("0.00%"));

        // ================= SUMMARY SHEET =================
        Sheet summary = workbook.createSheet("Summary");
        String[] headers = {"ID", "Name", "Total Days", "Present", "Absent", "Percentage"};

        Row header = summary.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Map<Long, String> nameMap = new LinkedHashMap<>();
        Map<Long, Integer> totalMap = new LinkedHashMap<>();
        Map<Long, Integer> presentMap = new LinkedHashMap<>();

        for (Attendance att : list) {
            if (att == null || att.getStudent() == null) continue;
            Long id = att.getStudent().getId();
            nameMap.put(id, att.getStudent().getName());
            totalMap.put(id, totalMap.getOrDefault(id, 0) + 1);
            if (att.getStatus() == AttendanceStatus.PRESENT) {
                presentMap.put(id, presentMap.getOrDefault(id, 0) + 1);
            }
        }

        int rowNum = 1;
        for (Long id : totalMap.keySet()) {
            int total = totalMap.getOrDefault(id, 0);
            int present = presentMap.getOrDefault(id, 0);
            int absent = total - present;
            double percent = total == 0 ? 0 : (present / (double) total);

            Row row = summary.createRow(rowNum++);
            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(nameMap.get(id));
            row.createCell(2).setCellValue(total);
            row.createCell(3).setCellValue(present);
            row.createCell(4).setCellValue(absent);

            Cell percentCell = row.createCell(5);
            percentCell.setCellValue(percent);
            percentCell.setCellStyle(percentStyle);
        }

        for (int i = 0; i < headers.length; i++) summary.autoSizeColumn(i);

        // ================= DETAILS SHEET =================
        Sheet detail = workbook.createSheet("Details");
        String[] headers2 = {"ID", "Name", "Date", "Status", "Check-In", "Check-Out"};

        Row header2 = detail.createRow(0);
        for (int i = 0; i < headers2.length; i++) {
            Cell cell = header2.createCell(i);
            cell.setCellValue(headers2[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum2 = 1;
        for (Attendance att : list) {
            if (att == null || att.getStudent() == null) continue;

            Row row = detail.createRow(rowNum2++);
            row.createCell(0).setCellValue(att.getStudent().getId());
            row.createCell(1).setCellValue(att.getStudent().getName());
            row.createCell(2).setCellValue(String.valueOf(att.getDate()));
            row.createCell(3).setCellValue(String.valueOf(att.getStatus()));
            row.createCell(4).setCellValue(att.getCheckIn() != null ? String.valueOf(att.getCheckIn()) : "-");
            row.createCell(5).setCellValue(att.getCheckOut() != null ? String.valueOf(att.getCheckOut()) : "-");
        }

        for (int i = 0; i < headers2.length; i++) detail.autoSizeColumn(i);

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }
}