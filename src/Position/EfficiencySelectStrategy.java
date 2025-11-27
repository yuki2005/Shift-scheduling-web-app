package Position;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EfficiencySelectStrategy implements StaffSelectStrategy{
	
	@Override
	public List<Employee> selectStaff(List<Employee> ableStaff, Schedule conditions, 
                                      List<ShiftPreference> shiftPreferences, List<ShiftTime> targetTimes){
		//デバッグ出力
		/*
		System.out.println("=== WorkingStaff selection start ===");
		for (ShiftPreference pref : shiftPreferences) {
			for(ShiftTime time : ShiftTime.values()) {
				System.out.println(pref.getEmployee().getName() + " => " + pref.getAvailability(time));
			}
		}*/

		
        // Employee ID をキーとする ShiftPreference の Map を作成 (高速検索のため)
        Map<Integer, ShiftPreference> preferenceMap = shiftPreferences.stream()
                .collect(Collectors.toMap(p -> p.getEmployee().getId(), p -> p));
                
        // フィルタリング: targetTimes の制約を満たす従業員のみ残す
        List<Employee> viableStaff = ableStaff.stream()
                .filter(employee -> {
                    ShiftPreference pref = preferenceMap.get(employee.getId());
                    if (pref == null) {
                        // 希望未提出 → 選定対象外
                        return false;
                    }

                    // 🔧 修正：1つでも出勤可能な時間があればOK（以前は「全て出られないと除外」だった）
                    boolean canWork = targetTimes.stream()
                            .anyMatch(time -> pref.getAvailability(time) > 0);

                    // --- デバッグ出力 ---
                    System.out.println("【DEBUG】" + employee.getName() + " の可用性:");
                    for (ShiftTime time : targetTimes) {
                        System.out.println("   " + time + ": " + pref.getAvailability(time));
                    }
                    System.out.println("   ⇒ 判定結果: " + (canWork ? "出勤可能" : "除外"));

                    return canWork;
                })
                .collect(Collectors.toList());
        
        if (viableStaff.isEmpty()) {
            System.out.println("⚠️ どの従業員も targetTimes に対応できませんでした。");
        }

        // スキル合計値の計算 (フィルタリング後の viableStaff を使用)
		Map<Employee, Integer> allSkills = allSkill(viableStaff); 
		
		List<Employee> sortedStaffs = new ArrayList<>(getSortedSkillList(allSkills));
		
		// targetTimes は基本1つの時間帯が入る想定
		ShiftTime target = targetTimes.get(0);

		// その時間帯だけの必要人数合計
		int requiredCount = conditions.getRequiredCountsByTime()
		        .getOrDefault(target, Map.of())
		        .values().stream()
		        .mapToInt(Integer::intValue)
		        .sum();
		
		int actualCount = Math.min(requiredCount, sortedStaffs.size());
		
		// 能力値の高い順に選定
		List<Employee> result = new ArrayList<>();
		for(int i = 0; i < actualCount; i++) {
			result.add(sortedStaffs.get(i));
		}
		
		return Collections.unmodifiableList(result);
	}
	
	//各従業員の能力値の合計を求める (変更なし)
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
	
	//ポジションごとの総合スキル値を大きい順に従業員を格納したListを返すメソッド (変更なし)
	private ArrayList<Employee> getSortedSkillList(Map<Employee, Integer> allSkill){
		List<Map.Entry<Employee, Integer>> sortedEmployee = new ArrayList<>(allSkill.entrySet());
		sortedEmployee.sort(Comparator.comparing(Map.Entry<Employee, Integer>::getValue).reversed());
		
		ArrayList<Employee> result = new ArrayList<>();
		for(Map.Entry<Employee, Integer> entry : sortedEmployee) {
			result.add(entry.getKey());
		}
		return result;
	}
}
