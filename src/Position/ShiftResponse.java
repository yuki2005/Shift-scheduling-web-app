package Position;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class ShiftResponse {
    
    // 1. 最終的な割り当て結果
    private Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment;
    
    // 2. 実際に選出された従業員リスト
    private Map<ShiftTime, List<Employee>> workingStaff;
    
    // 3. 警告や情報メッセージ (例: 人数不足)
    private String message;
    
    // コンストラクタ、ゲッター、セッターが必要 (ここでは省略)
    
    public ShiftResponse(Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment, Map<ShiftTime, List<Employee>> workingStaff, String message) {
        this.finalAssignment = finalAssignment;
        this.workingStaff = workingStaff;
        this.message = message;
    }
    
    public ShiftResponse() {
    }

    public Map<ShiftTime, Map<Pos, List<Employee>>> getFinalAssignment() {
        return finalAssignment;
    }

    public Map<ShiftTime, List<Employee>> getWorkingStaff() {
        return workingStaff;
    }

    public String getMessage() {
        return message;
    }

    // (セッターも追加)
    public void setMessage(String message) {
    	this.message = message;
    }
    
}
