package Position;

import java.time.LocalDate;
import java.util.Map;

public class ShiftPreferenceDto {
	private int employeeId;
	private LocalDate date;
	private Map<String, Integer> availabilityMap;
	
	public ShiftPreferenceDto() {
	}
	
	
	//ゲッター、セッター
	public int getEmployeeId() { 
		return employeeId; 
		}
	public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
	
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate d) { this.date = d; }
	
	public Map<String, Integer> getAvailabilityMap(){ return availabilityMap; }
	public void setAvailabilityMap(Map<String, Integer> availabilityMap) { this.availabilityMap = availabilityMap; }
}
