package Position.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
		name = "shiftpreference_header",
		uniqueConstraints = @UniqueConstraint(columnNames = {"employee_number", "date"})
		)
public class ShiftPreferenceEntity {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;  // DB側では自動採番を想定（Employee.idとは別管理でもOK）
	 
	 //社員番号
	 @Column(nullable = false)
	 private Integer employeeNumber;
	 
	 //希望を入力する日付
	 @Column(nullable = false)
	 private LocalDate date;
	 
	 @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<ShiftPreferenceDetailEntity> details;
	 
	 //ゲッター、セッター
	 public Long getId() { return id; }
	 public void setId(Long id) { this.id = id; }
	 
	 public Integer getEmployeeNumber() { return employeeNumber; }
	 public void setEmployeeNumber(int number) { this.employeeNumber = number; }
	 
	 public LocalDate getDate() { return date; }
	 public void setDate(LocalDate date) { this.date = date; }
}
