package com.example.StudentAttendenceSystem.Model;

// FIX: Removed unused import (org.springframework.data.jpa.repository.Query)
public enum AttendanceStatus {
	PRESENT,
	ABSENT,
    PENDING,
    APPROVED,
    REJECTED
}