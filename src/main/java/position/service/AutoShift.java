package position.service;

import org.springframework.stereotype.Service;

import position.model.Employee;
import position.model.Schedule;
import position.model.ShiftPreference;
import position.model.ShiftTime;
import position.strategy.StaffSelectStrategy;

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
			
			List<Employee> selectedStaff = staffSelectStrategy.selectStaff(
	                ableStaff, 
	                conditions, 
	                shiftPreferences, 
	                targetTime // ターゲット時間帯を渡す
	            );
			
			result.put(targetTime, Collections.unmodifiableList(selectedStaff)); 
			
		}
		return Collections.unmodifiableMap(result);
	}
}
