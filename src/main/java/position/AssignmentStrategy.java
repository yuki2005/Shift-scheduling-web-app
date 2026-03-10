package position;

import java.util.List;
import java.util.Map;

public interface AssignmentStrategy {
	
	/**
	 * ポジション割り振りのロジックを実行し、最適な割り振り結果を計算します
	 * @param staff 出勤可能な従業員のリスト
	 * @param conditions その日のシフト条件
	 * @param time 割り当て対象の時間帯
	 * @return 決定された割り当て結果
	 */
	
	Map<Pos, List<Employee>> assign (List<Employee> staff, Schedule conditions, ShiftTime time);
}
