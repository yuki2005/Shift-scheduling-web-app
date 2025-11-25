package Position;

import java.util.Map;

public class ShiftPreferenceDto {
	private int employeeId; //従業員を識別するためのID
	private String date;
	private Map<String, Integer> availabilityMap;
	
	public ShiftPreferenceDto() {
	}
	
	
	//ゲッター、セッター
	public int getEmployeeId() { return employeeId; }
	public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
	
	public String getDate() { return date; }
	public void setDate(String d) { this.date = d; }
	
	public Map<String, Integer> getAvailabilityMap(){ return availabilityMap; }
	public void setAvailabilityMap(Map<String, Integer> availabilityMap) { this.availabilityMap = availabilityMap; }
}
