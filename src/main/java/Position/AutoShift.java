package Position;

import org.springframework.stereotype.Service; 
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@Service
public class AutoShift {
	private final StaffSelectStrategy staffSelectStrategy;
	
	public AutoShift(StaffSelectStrategy strategy) {
		this.staffSelectStrategy = strategy;
	}
	
	public Map<ShiftTime, List<Employee>> selectWorkingStaffByTime(List<Employee> ableStaff, Schedule conditions, List<ShiftPreference> shiftPreferences){
		Map<ShiftTime, List<Employee>> result = new HashMap<>();
		
		for(ShiftTime targetTime : ShiftTime.values()) {
			
			List<ShiftTime> targetTimes = List.of(targetTime);
			
			List<Employee> selectedStaff = staffSelectStrategy.selectStaff(
	                ableStaff, 
	                conditions, 
	                shiftPreferences, 
	                targetTimes // ターゲット時間帯を渡す
	            );
			
			result.put(targetTime, Collections.unmodifiableList(selectedStaff)); 
			
		}
		return Collections.unmodifiableMap(result);
	}
}
