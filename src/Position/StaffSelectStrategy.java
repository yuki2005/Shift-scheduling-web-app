package Position;

import java.util.List;

public interface StaffSelectStrategy {
	/**
	 * 出勤可能な従業員から出勤する人を選ぶ
	 * @param staff 出勤可能な従業員のリスト
	 * @param conditions その日のシフト条件
	 * @return 出勤する人のリスト
	 */
	List<Employee> setStaff(List<Employee> ableStaff, Schedule conditions);
}
