package Position.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * 従業員情報を表す Entity クラス。
 *
 * ・DBの主キー（id）は技術的な識別子として使用する
 * ・業務上の一意キーとして社員番号（employeeNumber）を保持する
 *
 * スキル情報は柔軟な拡張を考慮し、
 * JSON形式で保存する設計としている。
 */

@Entity
@Table(name = "employee")
public class EmployeeEntity {
	
	// DB側では自動採番を想定（Employee.idとは別管理でもOK）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    
    //名前
    @Column(nullable = false)
    private String name;	
    
    // 社員番号（業務上の一意キー）
    @Column(nullable = false, unique = true)
    private int employeeNumber;	   
    
    // スキル情報をJSON形式で保持（柔軟な拡張を想定）
    @Column(columnDefinition = "TEXT")
    private String skillsJson;

    // --- Getter / Setter ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getEmployeeNumber() { 
    	return employeeNumber;
    }
    public void setEmployeeNumber(int employeeNumber) {
    	this.employeeNumber = employeeNumber; 
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkillsJson() {
        return skillsJson;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }

    // --- equals / hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity)) return false;
        EmployeeEntity that = (EmployeeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
