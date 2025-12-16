package Position.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRawValue;

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

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalShiftRecordAssignmentEntity> assignments;
    
    @JsonRawValue
    @Column(columnDefinition = "TEXT")
    private String finalAssignmentJson;
    
    // メッセージ（DBにも残す）
    @Column(columnDefinition = "TEXT")
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
