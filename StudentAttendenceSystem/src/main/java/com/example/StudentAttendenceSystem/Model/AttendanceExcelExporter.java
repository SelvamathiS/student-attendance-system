package com.example.StudentAttendenceSystem.Model;

import com.example.StudentAttendenceSystem.Model.Attendance;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class AttendanceExcelExporter {

    private final List<Attendance> attendanceList;

    public AttendanceExcelExporter(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public void export(HttpServletResponse response) throws IOException {

        // Set response headers
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=attendance.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            // ── Header style ──────────────────────────────────────
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // ── Data style ────────────────────────────────────────
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // ── Present style (green) ─────────────────────────────
            CellStyle presentStyle = workbook.createCellStyle();
            presentStyle.cloneStyleFrom(dataStyle);
            Font presentFont = workbook.createFont();
            presentFont.setColor(IndexedColors.GREEN.getIndex());
            presentFont.setBold(true);
            presentStyle.setFont(presentFont);

            // ── Absent style (red) ────────────────────────────────
            CellStyle absentStyle = workbook.createCellStyle();
            absentStyle.cloneStyleFrom(dataStyle);
            Font absentFont = workbook.createFont();
            absentFont.setColor(IndexedColors.RED.getIndex());
            absentFont.setBold(true);
            absentStyle.setFont(absentFont);

            // ── Header Row ────────────────────────────────────────
            String[] headers = {"ID", "Student Name", "Date", "Check In", "Check Out", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Data Rows ─────────────────────────────────────────
            int rowNum = 1;
            for (Attendance a : attendanceList) {
                Row row = sheet.createRow(rowNum++);

                // ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(a.getId());
                idCell.setCellStyle(dataStyle);

                // Student Name
                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(a.getStudent() != null ? a.getStudent().getName() : "Unknown");
                nameCell.setCellStyle(dataStyle);

                // Date
                Cell dateCell = row.createCell(2);
                dateCell.setCellValue(a.getDate() != null ? a.getDate().toString() : "-");
                dateCell.setCellStyle(dataStyle);

             // Check In
                Cell checkInCell = row.createCell(3);
                checkInCell.setCellValue(a.getCheckIn() != null ?
                    a.getCheckIn().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) : "-");
                checkInCell.setCellStyle(dataStyle);

                // Check Out
                Cell checkOutCell = row.createCell(4);
                checkOutCell.setCellValue(a.getCheckOut() != null ?
                    a.getCheckOut().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) : "-");
                checkOutCell.setCellStyle(dataStyle);
                // Status (colored)
                Cell statusCell = row.createCell(5);
                statusCell.setCellValue(a.getStatus() != null ? a.getStatus().toString() : "-");
                String status = a.getStatus() != null ? a.getStatus().toString() : "";
                if (status.equals("PRESENT") || status.equals("APPROVED")) {
                    statusCell.setCellStyle(presentStyle);
                } else if (status.equals("ABSENT") || status.equals("REJECTED")) {
                    statusCell.setCellStyle(absentStyle);
                } else {
                    statusCell.setCellStyle(dataStyle);
                }
            }

            // ── Auto size columns ─────────────────────────────────
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}