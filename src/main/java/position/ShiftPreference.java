package position;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;

/**
 * 従業員のシフト希望を表すドメインモデルクラス。
 *
 * ・1人の従業員が、特定の日付において
 *   各時間帯に勤務可能かどうかを保持する
 * ・希望情報は ShiftTime をキーとしたマップで管理される
 *
 * 本クラスは業務ロジックで使用されるモデルであり、
 * 永続化や通信の責務は持たない。
 */
public class ShiftPreference {
	
	// 希望を提出した従業員
	private final Employee employee;
	
	// 希望対象の日付
	private final LocalDate date;
	
	// 各時間帯における勤務可否（0: 不可, 1: 可）
	// 将来的な段階評価（0〜n）への拡張を考慮
	private final Map<ShiftTime, Integer> availabilityMap;
	
	/**
	 * 文字列キーの希望マップを ShiftTime enum に変換し、
	 * ShiftPreference インスタンスを生成する。
	 *
	 * DTO や JSON 入力を想定した変換処理であり、
	 * 不正なキーは警告を出した上で無視する。
	 *
	 * @param employee 従業員情報
	 * @param initialAvailabilityMap 文字列キーの希望マップ
	 * @param date 希望対象日付
	 * @return 生成された ShiftPreference
	 */
	public static ShiftPreference fromStringMap(Employee employee, Map<String, Integer> initialAvailabilityMap, LocalDate date) {
		
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
        
        return new ShiftPreference(employee, converted, date);
	}
	
	/**
	 * ShiftTime をキーとした希望マップから
	 * ShiftPreference を生成する。
	 *
	 * 内部的には Map を不変化して保持する。
	 */
	public ShiftPreference(Employee employee, Map<ShiftTime, Integer> availabilityMap, LocalDate date) {
        this.employee = employee;
        this.availabilityMap = Collections.unmodifiableMap(new EnumMap<>(availabilityMap));
        this.date = date;
    }
	

	/**
	 * 指定した時間帯における勤務可否を取得する。
	 *
	 * @param time 時間帯
	 * @return 勤務可否（0: 不可, 1: 可）
	 */
	// 将来性を考えて実装
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
