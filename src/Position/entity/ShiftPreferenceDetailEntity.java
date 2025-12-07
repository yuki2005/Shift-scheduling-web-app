package Position.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shift_preference_detail")
public class ShiftPreferenceDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", nullable = false)
    private ShiftPreferenceEntity header;

    @Column(nullable = false)
    private String shiftTime;  // TOP, LUNCH, IDLE, DINNER, LAST

    @Column(nullable = false)
    private Integer availability; // 0 or 1
    
    public Long getId() { return id; }
    public ShiftPreferenceEntity getHeader() { return header; }
    public void setHeader(ShiftPreferenceEntity header) { this.header = header; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public Integer getAvailability() { return availability; }
    public void setAvailability(Integer availability) { this.availability = availability; }

}
