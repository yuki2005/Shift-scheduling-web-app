package Position;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;

public class ShiftPreference {
	//従業員の情報
	private final Employee employee;
	
	//日付
	private final LocalDate date;
	
	//各時間帯で出勤可能かどうか　false:出勤不可 true:出勤可
	private final Map<ShiftTime, Integer> availabilityMap;
	
	//コンストラクタ
	public static ShiftPreference fromStringMap(Employee employee, Map<String, Integer> initialAvailabilityMap, String dateString) {
		
		if (initialAvailabilityMap == null) {
	        initialAvailabilityMap = new HashMap<>();
	    }
		
		Map<ShiftTime, Integer> converted = new EnumMap<>(ShiftTime.class);
		
        for (Map.Entry<String, Integer> entry : initialAvailabilityMap.entrySet()) {
            try {
                ShiftTime time = ShiftTime.valueOf(entry.getKey().toUpperCase());
                converted.put(time, entry.getValue());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Unknown ShiftTime key -> " + entry.getKey());
            }
        }
        
        LocalDate date = LocalDate.parse(dateString);
     /* 🟡 デバッグ出力追加
        System.out.println("[DEBUG] ShiftPreference created for " + employee.getName() + ": " + converted);
        */
        return new ShiftPreference(employee, converted, date);
	}
	
	public ShiftPreference(Employee employee, Map<ShiftTime, Integer> availabilityMap, LocalDate date) {
        this.employee = employee;
        this.availabilityMap = availabilityMap;
        this.date = date;
    }
	
	//受け取った時間帯に出勤可能かを取得
	public int getAvailability(ShiftTime time) {
		return availabilityMap.getOrDefault(time, 0);
	}
	
	//出勤可能状況を格納したマップを返す
	public Map<ShiftTime, Integer> getAvailabilityMap() {
		return availabilityMap;
	}
	
	//従業員の情報を取得
	public Employee getEmployee() {
		return employee;
	}
	
	//いつの希望なのかを取得
	public LocalDate getDate() {
		return date;
	}
}
