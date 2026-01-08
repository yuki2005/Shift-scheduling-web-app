package Position.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 従業員のシフト希望ヘッダ情報を表す Entity クラス。
 *
 * ・1レコードは「従業員 × 日付」単位のシフト希望を表す
 * ・同一従業員・同一日付の希望は1件のみとする
 *   （employeeNumber + date に一意制約を設定）
 *
 * ShiftPreferenceDetailEntity と1対多で関連付けられ、
 * 時間帯ごとの希望可否は明細として管理する。
 */

@Entity
@Table(
		name = "shiftpreference_header",
		uniqueConstraints = @UniqueConstraint(columnNames = {"employee_number", "date"})
		)
public class ShiftPreferenceEntity {
	
	// DBの主キー（自動採番・技術キー）
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;  // DB側では自動採番を想定（Employee.idとは別管理でもOK）
	 
	// 従業員の社員番号（業務上の一意キー）
	 @Column(nullable = false)
	 private Integer employeeNumber;
	 
	// 希望を提出する対象日付
	 @Column(nullable = false)
	 private LocalDate date;
	 
	// 時間帯ごとのシフト希望明細
	 @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<ShiftPreferenceDetailEntity> details;
	 
	 // === getter/setter ===
	 public Long getId() { return id; }
	 public void setId(Long id) { this.id = id; }
	 
	 public Integer getEmployeeNumber() { return employeeNumber; }
	 public void setEmployeeNumber(int number) { this.employeeNumber = number; }
	 
	 public LocalDate getDate() { return date; }
	 public void setDate(LocalDate date) { this.date = date; }
}
