package position.strategy;

import org.springframework.stereotype.Component;

import position.model.Employee;
import position.model.Pos;
import position.model.Schedule;
import position.model.ShiftTime;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

@Component
public class MaxSkillStrategy implements AssignmentStrategy {
	
	@Override
	public Map<Pos, List<Employee>> assign(List<Employee> staff, Schedule conditions, ShiftTime time){
		//結果を格納するMap
		Map<Pos, List<Employee>> result = new HashMap<>();
		
		//Schedule から時間帯ごとの人数マップ全体を取得
		Map<ShiftTime, Map<Pos, Integer>> requiredCountsByTime = conditions.getRequiredCountsByTime();
        
        // PosAssignが時間帯ごとに呼び出しているため、ここでは最初の時間帯の人数マップを代用。
        Map<Pos, Integer> requiredCount = requiredCountsByTime.get(time);
		
		//割り当て済み従業員を格納するSet
		Set<Employee> assignedstaff = new HashSet<>();
		
		//Posを重み順にソートする
		List<Pos> sortedPos = new ArrayList<>(List.of(Pos.values()));
		sortedPos.sort((o1, o2) -> o2.getWeight() - o1.getWeight());
		
		//各ポジションごとの従業員の総合スキル値を格納するMap
		Map<Pos, Map<Employee, Integer>> OverallSkill = this.getOverallSkill(staff);
		
		//割り振りのロジックを記述する
		for(Pos p : sortedPos) {
			//ポジションpのスキル値が高い順に従業員が格納されたリストを用意する
			ArrayList<Employee> pOverallSkill = new ArrayList<Employee>(getSortedSkillList(p, OverallSkill));
			List<Employee> p_e = new ArrayList<>();
			
            // requiredCount マップを使用
			for(int i = 0; i < requiredCount.getOrDefault(p, 0); i++) {
				for(Employee e : pOverallSkill) {
					if(!assignedstaff.contains(e)) {
						p_e.add(e);
						assignedstaff.add(e);
						break;
					}
				}
			}
			result.put(p, Collections.unmodifiableList(p_e));
		}
		return Collections.unmodifiableMap(result);
	}
	
	//各ポジションの従業員の総合スキル値のMapを返す
	private Map<Pos, Map<Employee, Integer>> getOverallSkill(List<Employee> staff){
		Map<Pos, Map<Employee, Integer>> result = new HashMap<>();
		for(Pos p : Pos.values()) {
			Map<Employee, Integer> tmp = new HashMap<>();
			for(Employee e : staff) {
				tmp.put(e, (e.getSkill(p) * p.getWeight()));
			}
			result.put(p, tmp);
		}
		return result;
	}
	
	//ポジションごとの総合スキル値を大きい順に従業員を格納したListを返すメソッド
	private ArrayList<Employee> getSortedSkillList(Pos p, Map<Pos, Map<Employee, Integer>> OverallSkill){
		Map<Employee, Integer> skill = new HashMap<>(OverallSkill.get(p));
		List<Map.Entry<Employee, Integer>> sortedEmployee = new ArrayList<>(skill.entrySet());
		sortedEmployee.sort((entry1, entry2) -> {
			return entry2.getValue().compareTo(entry1.getValue());
		});
		
		ArrayList<Employee> result = new ArrayList<>();
		for(Map.Entry<Employee, Integer> entry : sortedEmployee) {
			result.add(entry.getKey());
		}
		return result;
	}
}
