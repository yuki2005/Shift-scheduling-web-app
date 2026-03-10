package position.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 確定シフト履歴を表す Entity クラス。
 *
 * ・1日分の確定シフトを1レコードとして管理する
 * ・シフト全体は JSON 形式で保存し、表示・復元を容易にする
 * ・同時に、検索・集計用として明細テーブル（Assignment）に正規化して保持する
 *
 * 表示性能とデータ分析の両立を目的とした設計。
 */

@Entity
@Table(name = "final_shift_record")
public class FinalShiftRecordEntity {
	
	// DBキー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // シフト日付
    private LocalDate date;

    // 曜日
    private String dayOfWeek;

    // 休日フラグ
    private boolean holiday;
    
    // シフト明細のリスト（時間帯 × ポジション × 従業員）
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FinalShiftRecordAssignmentEntity> assignments;
    
    // JSON形式の確定シフト情報（画面表示・復元用）
    @Column(columnDefinition = "TEXT")
    private String finalAssignmentJson;
    
    // メッセージ（DBにも残す）
    @Column(columnDefinition = "TEXT")
    private String message;
    
    // デフォルトコンストラクタ
    public FinalShiftRecordEntity() {}
    
    // ===== Getter/Setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public boolean isHoliday() { return holiday; }
    public void setHoliday(boolean holiday) { this.holiday = holiday; }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<FinalShiftRecordAssignmentEntity> getAssignments() {
        return assignments;
    }
    public void setAssignments(List<FinalShiftRecordAssignmentEntity> assignments) {
        this.assignments = assignments;
    }
    
    public String getFinalAssignmentJson() { return finalAssignmentJson; }
    public void setFinalAssignmentJson(String json) { this.finalAssignmentJson = json; }

}
