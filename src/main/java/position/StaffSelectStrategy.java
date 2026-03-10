package position;

import java.util.List;

public interface StaffSelectStrategy {
	/**
	 * 出勤可能な従業員から出勤する人を選ぶ
	 * @param staff 出勤可能な従業員のリスト
	 * @param conditions その日のシフト条件
	 * @param shiftPreferences 各従業員の出勤可能リスト
	 * @param targetTimes 従業員の選出を行う時間帯
	 * @return 出勤する人のリスト
	 */
	List<Employee> selectStaff(List<Employee> ableStaff, Schedule conditions, List<ShiftPreference> shiftPreferences, ShiftTime targetTime);
}
