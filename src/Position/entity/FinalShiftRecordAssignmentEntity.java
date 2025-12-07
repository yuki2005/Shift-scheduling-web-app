package Position.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "final_shift_assignment")
public class FinalShiftRecordAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private FinalShiftRecordEntity record;

    @Column(nullable = false)
    private String shiftTime;

    @Column(nullable = false)
    private String posCode;

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
