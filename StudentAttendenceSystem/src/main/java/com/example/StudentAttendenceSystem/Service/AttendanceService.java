package com.example.StudentAttendenceSystem.Service;

import com.example.StudentAttendenceSystem.Model.*;
import com.example.StudentAttendenceSystem.Reposistory.AttendanceRepository;
import com.example.StudentAttendenceSystem.Reposistory.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private StudentRepository studentRepo;

 // Current code — checkIn and checkOut are never set!
    public Attendance markAttendance(Long studentId, AttendanceStatus status) {
        Student student = studentRepo.findById(studentId).orElseThrow();
        LocalDate today = LocalDate.now();

        // Prevent duplicate
        if (attendanceRepo.existsByStudentIdAndDate(studentId, today)) {
            throw new RuntimeException("Attendance already marked for today!");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setDate(today);
        attendance.setStatus(status);
        attendance.setCheckIn(LocalTime.now());  // ✅ Auto set check-in time
        return attendanceRepo.save(attendance);
    }

    public List<Attendance> getAll() {
        return attendanceRepo.findAllWithStudent();
    }

    public long getPresentCount() {
        return attendanceRepo.countByStatus(AttendanceStatus.PRESENT);
    }

    public long getAbsentCount() {
        return attendanceRepo.countByStatus(AttendanceStatus.ABSENT);
    }

    public long getPresentCountByStudent(Long studentId) {
        return attendanceRepo.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
    }

    public long getAbsentCountByStudent(Long studentId) {
        return attendanceRepo.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
    }

    public void updateStatus(Long attendanceId, AttendanceStatus status) {
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found: " + attendanceId));

        if (status == AttendanceStatus.APPROVED) {
            attendance.setStatus(AttendanceStatus.PRESENT);
        } else if (status == AttendanceStatus.REJECTED) {
            attendance.setStatus(AttendanceStatus.ABSENT);
        } else {
            attendance.setStatus(status);
        }

        attendanceRepo.save(attendance);
    }

    public List<Attendance> getByStatus(AttendanceStatus status) {
        return attendanceRepo.findByStatus(status);
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepo.save(attendance);
    }

    public List<Attendance> getByMonth(int month, int year) {
        return attendanceRepo.findByMonthAndYear(month, year);
    }

    public double calculatePercentage(Long studentId, int month, int year) {
        List<Attendance> list = getByMonth(month, year);
        List<Attendance> studentList = list.stream()
                .filter(a -> a.getStudent() != null
                          && a.getStudent().getId().equals(studentId))
                .toList();

        long totalDays = studentList.size();
        long presentDays = studentList.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT
                          || a.getStatus() == AttendanceStatus.APPROVED) // ✅ Fix
                .count();

        if (totalDays == 0) return 0;
        return (presentDays * 100.0) / totalDays;
    }

    public long countStudents() {
        return studentRepo.count();
    }
    public List<Attendance> getByDayMonthYear(int day, int month, int year) {
        return attendanceRepo.findByDayMonthAndYear(day, month, year);
    }
    public void markCheckOut(Long attendanceId) {
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found!"));

        // ✅ Cannot check out if never checked in
        if (attendance.getCheckIn() == null) {
            throw new RuntimeException("Cannot check out — Check in not recorded!");
        }

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("Check out already marked!");
        }

        attendance.setCheckOut(LocalTime.now());
        attendanceRepo.save(attendance);
    }
    
    
    
}