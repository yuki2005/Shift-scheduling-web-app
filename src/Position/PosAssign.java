package Position;

import org.springframework.stereotype.Service; 
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

@Service
public class PosAssign {
	private final AssignmentStrategy strategy;
	
	public PosAssign(AssignmentStrategy strategy) {
		this.strategy = strategy;
	}
	
	public Map<ShiftTime, Map<Pos, List<Employee>>> execute(Map<ShiftTime, List<Employee>> staff, Schedule conditions){
		//結果を格納するMapを用意する
		Map<ShiftTime, Map<Pos, List<Employee>>> result = new HashMap<>();
		
		for(ShiftTime targettime : ShiftTime.values()) {
			
			List<Employee> staffForTime = staff.getOrDefault(targettime, Collections.emptyList());
			
			if(!staffForTime.isEmpty()) {
				
				Map<Pos, List<Employee>> posAssignments = strategy.assign(staffForTime, conditions, targettime);
				
				result.put(targettime, posAssignments);
			}
		}
		return Collections.unmodifiableMap(result);
	}
}
