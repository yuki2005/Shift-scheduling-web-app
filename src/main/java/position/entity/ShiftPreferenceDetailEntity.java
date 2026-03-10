package position.entity;

import jakarta.persistence.*;

/**
 * シフト希望の明細情報を表す Entity クラス。
 *
 * ・1レコードは、1人の従業員が
 *   1日分のシフト希望の中で、
 *   特定の時間帯に勤務可能かどうかを表す
 *
 * ShiftPreferenceEntity（ヘッダ）と多対一で関連付けられ、
 * 時間帯ごとの希望可否を正規化して保持する。
 */

@Entity
@Table(name = "shift_preference_detail")
public class ShiftPreferenceDetailEntity {
	
	// DBの主キー（自動採番）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 親となるシフト希望ヘッダ（従業員 × 日付）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", nullable = false)
    private ShiftPreferenceEntity header;
    
    // シフト時間帯（TOP, LUNCH, IDLE, DINNER, LAST）
    @Column(nullable = false)
    private String shiftTime;  // TOP, LUNCH, IDLE, DINNER, LAST
    
    // 勤務可否（0: 不可, 1: 可）
    @Column(nullable = false)
    private Integer availability; // 0 or 1
    
    // === getter/setter ===
    public Long getId() { return id; }
    
    public ShiftPreferenceEntity getHeader() { return header; }
    public void setHeader(ShiftPreferenceEntity header) { this.header = header; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public Integer getAvailability() { return availability; }
    public void setAvailability(Integer availability) { this.availability = availability; }

}
