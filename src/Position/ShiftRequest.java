package Position;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShiftRequest {
	private String dayOfWeekString;
	
	private String date;
	
	private boolean isHoliday;
	
	private List<EmployeeDto> employeeCandidates;
	
	private List<ShiftPreferenceDto> shiftPreferences;
	
	public ShiftRequest() {
		
	}
	//標準ゲッター
	public String getDate() {return date;}
	
	public String getDayOfWeekString() {
		return dayOfWeekString;
	}
	
	public boolean isHoliday() {
		return isHoliday;
	}
	
	public List<EmployeeDto> getEmployeeCandidates(){
		return employeeCandidates;
	}
	
	public List<ShiftPreferenceDto> getShiftPreferences(){
		return shiftPreferences;
	}
	
	//標準セッター
	public void setDate(String date) {this.date = date;}
	
	public void setDayOfWeekString(String dayOfWeekString) {
		this.dayOfWeekString = dayOfWeekString;
	}
	
	public void setIsHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	
	public void setEmployeeCandidates(List<EmployeeDto> employeeCandidates) {
		this.employeeCandidates = employeeCandidates;
	}
	
	public void setShiftPreferences(List<ShiftPreferenceDto> shiftPreferences) {
        this.shiftPreferences = shiftPreferences;
    }
	
	//---変換ロジック---
	
	public List<Employee> toEmployeeList(){
		if(employeeCandidates == null) return List.of();
			return employeeCandidates.stream().map(EmployeeDto::toEmployee).collect(Collectors.toList());
	}
	
	public List<ShiftPreference> toPreferenceList(List<Employee> employees){
		
		if(shiftPreferences == null || employees == null) {
	        return List.of();
	    }
	    
	    Map<Integer, Employee> employeeMap = employees.stream()
	            .collect(Collectors.toMap(Employee::getId, Function.identity()));
	    
	    List<ShiftPreference> prefs = shiftPreferences.stream()
	        .map(prefDto -> {
	            Employee employee = employeeMap.get(prefDto.getEmployeeId());
	            if (employee == null) return null;
	            return ShiftPreference.fromStringMap(employee, prefDto.getAvailabilityMap(), prefDto.getDate());
	        })
	        .filter(pref -> pref != null)
	        .collect(Collectors.toList());

	    // 🟡 デバッグ出力追加
	    /*System.out.println("[DEBUG] Preferences built: " + prefs.size());
	    for (ShiftPreference sp : prefs) {
	        System.out.println("[DEBUG] " + sp.getEmployee().getName() + " → " + sp);
	    }*/

	    return prefs;
	}
	
	 public DayOfWeek getDayOfWeek() {
        // DTO内で安全に変換し、DayOfWeek を返す
        return DayOfWeek.valueOf(dayOfWeekString.toUpperCase());
	 }
	 
	 public boolean getIsHolidayFlag() {
		 return isHoliday;
	 }
}
