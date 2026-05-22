package com.example.StudentAttendenceSystem.Model;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.time.LocalTime;
@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    public Attendance(LocalTime checkIn, LocalTime checkOut) {
		super();
		this.checkIn = checkIn;
		this.checkOut = checkOut;
	}
	private LocalTime checkIn;
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    // FIX: EAGER fetch so att.student.name works in Thymeleaf templates
    // without a LazyInitializationException / SpringEL error
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    public Attendance() {}

    public Attendance(LocalDate date, AttendanceStatus status, Student student) {
        this.date = date;
        this.status = status;
        this.student = student;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime checkIn) { this.checkIn = checkIn; }

    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime checkOut) { this.checkOut = checkOut; }
}