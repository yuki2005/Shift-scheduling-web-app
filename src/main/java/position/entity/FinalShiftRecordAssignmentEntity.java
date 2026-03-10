package position.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

/**
 * 確定シフトの明細情報を表す Entity クラス。
 *
 * ・1件のレコードは「1日分のシフト」の中の
 *   「時間帯 × ポジション × 従業員」1割り当てを表す
 * ・FinalShiftRecordEntity（ヘッダ）と多対一で関連付けられる
 *
 * JSON形式で保存されたシフト情報とは別に、
 * 検索・集計・分析を容易にするための正規化データとして保持する。
 */

@Entity
@Table(name = "final_shift_assignment")
public class FinalShiftRecordAssignmentEntity {
	
	// DBキー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 親となる確定シフトレコード（ヘッダ）
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private FinalShiftRecordEntity record;
    
    // シフト時間帯（TOP, LUNCH, IDLE, DINNER, LAST）
    @Column(nullable = false)
    private String shiftTime;
    
    // 担当ポジションコード
    @Column(nullable = false)
    private String posCode;
    
    // 割り当てられた従業員の社員番号（業務上の一意キー）
    @Column(nullable = false)
    private Integer employeeNumber;

    // ===== Getter / Setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FinalShiftRecordEntity getRecord() { return record; }
    public void setRecord(FinalShiftRecordEntity record) { this.record = record; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public String getPosCode() { return posCode; }
    public void setPosCode(String posCode) { this.posCode = posCode; }

    public Integer getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
