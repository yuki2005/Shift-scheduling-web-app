package Position;

import java.util.List;
import java.util.stream.Collectors;

public class ShiftRequest {
	private String dayOfWeekString;
	private boolean isHoliday;
	
	private List<EmployeeDto> employeeCandidates;
	
	public ShiftRequest() {
		
	}
	//標準ゲッター
	public String getDayOfWeekString() {
		return dayOfWeekString;
	}
	
	public boolean isHoliday() {
		return isHoliday;
	}
	
	public List<EmployeeDto> getEmployeeCandidates(){
		return employeeCandidates;
	}
	
	public void setDayOfWeekString(String dayOfWeekString) {
		this.dayOfWeekString = dayOfWeekString;
	}
	
	public void setIsHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	
	public void setEmployeeCandidates(List<EmployeeDto> employeeCandidates) {
		this.employeeCandidates = employeeCandidates;
	}
	
	//---変換ロジック---
	
	public List<Employee> toEmployeeList(){
		if(employeeCandidates == null) return List.of();
			return employeeCandidates.stream().map(EmployeeDto::toEmployee).collect(Collectors.toList());
	}
	
	 public DayOfWeek getDayOfWeek() {
        // DTO内で安全に変換し、DayOfWeek を返す
        return DayOfWeek.valueOf(dayOfWeekString.toUpperCase());
	 }
	 
	 public boolean getIsHolidayFlag() {
		 return isHoliday;
	 }
}
