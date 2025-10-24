package Position;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.EnumMap;

public class ShiftPreference {
	//従業員の情報
	private final Employee employee;
	
	//各時間帯で出勤可能かどうか　false:出勤不可 true:出勤可
	private final Map<ShiftTime, Integer> availabilityMap;
	
	//コンストラクタ
	public static ShiftPreference fromStringMap(Employee employee, Map<String, Integer> initialAvailabilityMap) {
		Map<ShiftTime, Integer> converted = new EnumMap<>(ShiftTime.class);
        for (Map.Entry<String, Integer> entry : initialAvailabilityMap.entrySet()) {
            try {
                ShiftTime time = ShiftTime.valueOf(entry.getKey().toUpperCase());
                converted.put(time, entry.getValue());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Unknown ShiftTime key -> " + entry.getKey());
            }
        }
        
     /* 🟡 デバッグ出力追加
        System.out.println("[DEBUG] ShiftPreference created for " + employee.getName() + ": " + converted);
        */
        return new ShiftPreference(employee, converted);
	}
	
	public ShiftPreference(Employee employee, Map<ShiftTime, Integer> availabilityMap) {
        this.employee = employee;
        this.availabilityMap = availabilityMap;
    }
	
	//受け取った時間帯に出勤可能かを取得
	public int getAvailability(ShiftTime time) {
		return availabilityMap.getOrDefault(time, 0);
	}
	
	//従業員の情報を取得
	public Employee getEmployee() {
		return employee;
	}
}
