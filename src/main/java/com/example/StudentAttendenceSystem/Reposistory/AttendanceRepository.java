package com.example.StudentAttendenceSystem.Reposistory;

import com.example.StudentAttendenceSystem.Model.Attendance;
import com.example.StudentAttendenceSystem.Model.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	// ✅ Already works — your existing findByStatus handles PENDING too
	@Query("SELECT a FROM Attendance a JOIN FETCH a.student WHERE a.status = :status")
	List<Attendance> findByStatus(@Param("status") AttendanceStatus status);

    long countByStatus(AttendanceStatus status);
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    // ✅ FIX: Use MONTH/YEAR query instead of date range (more reliable in MySQL)
    @Query("SELECT a FROM Attendance a JOIN FETCH a.student " +
           "WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<Attendance> findByMonthAndYear(@Param("month") int month,
                                        @Param("year") int year);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student " +
           "WHERE a.date BETWEEN :start AND :end")
    List<Attendance> findByDateBetween(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student")
    List<Attendance> findAllWithStudent();
 // Add these two methods:

    boolean existsByStudentIdAndDate(Long studentId, LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student " +
           "WHERE DAY(a.date) = :day AND MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<Attendance> findByDayMonthAndYear(@Param("day") int day,
                                            @Param("month") int month,
                                            @Param("year") int year);
}