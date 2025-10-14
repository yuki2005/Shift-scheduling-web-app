package Position;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift")

public class ShiftController {
	
	private final AutoShift autoShift;
	private final PosAssign posAssign;
	
	//2. DI用のコンストラクタ
	public ShiftController(AutoShift autoShift, PosAssign posAssign) {
		this.autoShift = autoShift;
		this.posAssign = posAssign;
	}
	
	 // 3. Webからのリクエストを処理するメソッド
    @PostMapping("/assign")
    public Map<Pos, List<Employee>> assignShift(@RequestBody ShiftRequest request) {
        
        // 4. DTOからビジネスロジックの型へ変換 (ScheduleとEmployeeリストの生成)
        DayOfWeek day = request.getDayOfWeek();
        boolean isHoliday = request.getIsHolidayFlag();
        List<Employee> allAvailableEmployees = request.toEmployeeList();
        
        Schedule shiftConditions = new Schedule(day, isHoliday);
        
        // 5. ロジックの実行
        List<Employee> workingStaff = autoShift.selectEmployees(allAvailableEmployees, shiftConditions);
        
        Map<Pos, List<Employee>> finalAssignment = posAssign.execute(workingStaff, shiftConditions);
        
        // 6. Springが Map を自動で JSON に変換してクライアントに返す
        return finalAssignment;
    }
	
}
