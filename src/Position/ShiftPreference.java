package Position;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class ShiftPreference {
	//従業員の情報
	private final Employee employee;
	
	//各時間帯で出勤可能かどうか　0:出勤不可 1:出勤可
	private final Map<ShiftTime, Integer> availablityMap;
	
	//コンストラクタ
	public ShiftPreference(Employee employee, Map<ShiftTime, Integer> initialAvailablityMap) {
		this.employee = employee;
		this.availablityMap = Collections.unmodifiableMap(new HashMap<>(initialAvailablityMap));
	}
	
	public int getAvailablity(ShiftTime time) {
		return availablityMap.getOrDefault(time, 0);
	}
	
	public Employee getEmployee() {
		return employee;
	}
}
