package Position;

import java.util.Map;

public class ShiftPreferenceDto {
	private int employeeId; //従業員を識別するためのID
	private Map<String, Integer> availabilityMap;
	
	public ShiftPreferenceDto() {
	}
	
	public int getEmployeeId() { return employeeId; }
	public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
	public Map<String, Integer> getAvailabilityMap(){ return availabilityMap; }
	public void setAvailabilityMap(Map<String, Integer> availabilityMap) { this.availabilityMap = availabilityMap; }
}
