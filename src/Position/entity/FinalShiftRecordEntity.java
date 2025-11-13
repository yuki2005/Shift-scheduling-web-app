package Position.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "final_shift_record")
public class FinalShiftRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // シフト日付
    private LocalDate date;

    // 曜日
    private String dayOfWeek;

    // 休日フラグ
    private boolean holiday;

    // 最終的な割り当て結果（JSON文字列）
    @Lob
    private String finalAssignmentJson;

    // 実際に選出された従業員（JSON）
    @Lob
    private String workingStaffJson;

    // メッセージ（DBにも残す）
    private String message;

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

    public String getFinalAssignmentJson() { return finalAssignmentJson; }
    public void setFinalAssignmentJson(String finalAssignmentJson) {
        this.finalAssignmentJson = finalAssignmentJson;
    }

    public String getWorkingStaffJson() { return workingStaffJson; }
    public void setWorkingStaffJson(String workingStaffJson) {
        this.workingStaffJson = workingStaffJson;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
    }
}
