package Position;

import org.springframework.stereotype.Service; 
import java.util.List;

@Service
public class AutoShift {
	private final EfficiencySelectStrategy efficiencySelectStrategy;
	
	public AutoShift(EfficiencySelectStrategy e) {
		this.efficiencySelectStrategy = e;
	}
	
	public List<Employee> selectEmployees(List<Employee> l, Schedule d){
		return efficiencySelectStrategy.setStaff(l, d);
	}
}
