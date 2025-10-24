package Position;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift")

public class ShiftController {
	//出勤する従業員を格納する
	private final AutoShift autoShift;
	//従業員のポジションを格納する
	private final PosAssign posAssign;
	
	//2. DI用のコンストラクタ
	public ShiftController(AutoShift autoShift, PosAssign posAssign) {
		this.autoShift = autoShift;
		this.posAssign = posAssign;
	}
	
	 // 3. Webからのリクエストを処理するメソッド
    @PostMapping("/assign")
    public ShiftResponse assignShift(@RequestBody ShiftRequest request) {
        
        // 4. DTOからビジネスロジックの型へ変換 (ScheduleとEmployeeリストの生成)
        DayOfWeek day = request.getDayOfWeek();
        boolean isHoliday = request.getIsHolidayFlag();
        List<Employee> allAvailableEmployees = request.toEmployeeList();
        //シフトの希望リストを作成
        List<ShiftPreference> allPreferences = request.toPreferenceList(allAvailableEmployees); 
        
        Schedule shiftConditions = new Schedule(day, isHoliday);
        
        // 5. ロジックの実行
        Map<ShiftTime, List<Employee>> workingStaff = autoShift.selectWorkingStaffByTime(allAvailableEmployees, shiftConditions, allPreferences);
        
        Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment = posAssign.execute(workingStaff, shiftConditions);
        
        String message = "シフト割り当てが完了しました。";
        if(finalAssignment.isEmpty()) {
        	message = "警告: 割り当て可能なポジションが見つかりませんでした。";
        }
        
        // 6. Springが Map を自動で JSON に変換してクライアントに返す
        return new ShiftResponse(finalAssignment, workingStaff, message);
    }
	
}
