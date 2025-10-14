package Position;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.springframework.stereotype.Component;

@Component
public class EfficiencySelectStrategy implements StaffSelectStrategy{
	
	@Override
	public List<Employee> setStaff(List<Employee> ableStaff, Schedule conditions){
		List<Employee> result = new ArrayList<>();
		//各従業員のポジションのスキル値の合計を格納するMap
		Map<Employee, Integer> allSkills = new HashMap<>(allSkill(ableStaff));
		
		List<Employee> sortedStaffs = new ArrayList<>(getSortedSkillList(allSkills));
		
		//その日必要な最低人数を求める
		int requiredCount = 0;
		for(Pos p : Pos.values()) {
			requiredCount += conditions.getRequiredCounts().getOrDefault(p, 0);
		}
		
		int actualCount = Math.min(requiredCount, sortedStaffs.size());
		
		//能力値の高い順にrequiredCount人取り出してresultに格納する
		for(int i = 0; i < actualCount; i++) {
			result.add(sortedStaffs.get(i));
		}
		
		return Collections.unmodifiableList(result);
	}
	
	private Map<Employee, Integer> allSkill(List<Employee> staffs){
		Map<Employee, Integer> result = new HashMap<>();
		
		for(Employee e : staffs) {
			int allValue = 0;
			for(Pos p : Pos.values()) {
				allValue += e.getSkill(p);
			}
			result.put(e, allValue);
		}
		
		return Collections.unmodifiableMap(result);
	}
	
	//ポジションごとの総合スキル値を大きい順に従業員を格納したListを返すメソッド
	private ArrayList<Employee> getSortedSkillList(Map<Employee, Integer> allSkill){
		List<Map.Entry<Employee, Integer>> sortedEmployee = new ArrayList<>(allSkill.entrySet());
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
