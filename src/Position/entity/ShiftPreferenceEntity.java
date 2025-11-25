package Position.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "shiftPreference")
public class ShiftPreferenceEntity {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;  // DB側では自動採番を想定（Employee.idとは別管理でもOK）
	 
	 @Column(nullable = false)
	 private Integer employeeNumber;
	 
	 @Column(columnDefinition = "TEXT")
	 private String preferenceJson;
	 
	 @Column(nullable = false)
	 private LocalDate date;
	 
	 //ゲッター、セッター
	 public Long getId() { return id; }
	 public void setId(Long id) { this.id = id; }
	 
	 public int getEmployeeNumber() { return employeeNumber; }
	 public void setEmployeeNumber(int number) { this.employeeNumber = number; }
	 
	 public String getPreferenceJson() { return preferenceJson; }
	 public void setPreferenceJson(String preference) { this.preferenceJson = preference; }
	 
	 public LocalDate getDate() { return date; }
	 public void setDate(LocalDate date) { this.date = date; }
}
