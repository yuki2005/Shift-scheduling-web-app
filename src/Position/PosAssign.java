package Position;

import org.springframework.stereotype.Service; 
import java.util.Map;
import java.util.List;

@Service
public class PosAssign {
	private final AssignmentStrategy strategy;
	
	public PosAssign(AssignmentStrategy strategy) {
		this.strategy = strategy;
	}
	
	public Map<Pos, List<Employee>> execute(List<Employee> staff, Schedule conditions){
		return strategy.assign(staff, conditions);
	}
}
