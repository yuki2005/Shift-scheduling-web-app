package Position;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import Position.Employee;
import Position.Pos;
import Position.ShiftTime;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class ShiftResponse {
    
    // 1. 最終的な割り当て結果
    private Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment;
    
    // 2. 実際に選出された従業員リスト
    private Map<ShiftTime, List<Employee>> workingStaff;
    
    // 3. 警告や情報メッセージ (例: 人数不足)
    private String message;
    
    // 4. 日付
    private LocalDate date;
    
    // 5. 曜日
    private String dayOfWeek;
    
    // 6. 祝日かどうか
    private boolean isHoliday;
    
    //コンストラクタ
    public ShiftResponse(Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment, Map<ShiftTime, List<Employee>> workingStaff, String message,
    		LocalDate date, String dayOfWeek, boolean holiday) {
        this.finalAssignment = finalAssignment;
        this.workingStaff = workingStaff;
        this.message = message;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.isHoliday = holiday;
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
    
    public void setMessage(String message) {
    	this.message = message;
    }
    
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isHoliday() {
        return isHoliday;
    }
    public void setHoliday(boolean holiday) {
        this.isHoliday = holiday;
    }
}
