package Position;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

public class ResultPrinter {
	
	//コンストラクタ
	public ResultPrinter() {
		
	}
	
	public void printFullResult(List<Employee> workingStaff, Schedule conditions, Map<ShiftTime, Map<Pos, List<Employee>>> finalList) {
		printPreAssignDetails(workingStaff, conditions.getRequiredCountsByTime());
		printFullAssignment(finalList);
	}
	
	private void printPreAssignDetails(List<Employee> workingStaff, Map<ShiftTime, Map<Pos, Integer>> requiredCountsByTime) {
		System.out.println("\n--- シフト開始前の情報 ---");
		System.out.println("出勤者数: " + workingStaff.size() + "名");
		
		System.out.println("\n--- 出勤者リスト ---");
		System.out.println("-----------------------------");
		for(Employee e : workingStaff) {
			System.out.printf("| %-15s |\n", e.getName());
		}
		System.out.println("-----------------------------");
		
		System.out.println("\n[時間帯ごとのポジション必要人数]");
		System.out.println("----------------------");
		for(Map.Entry<ShiftTime, Map<Pos, Integer>> timeEntry : requiredCountsByTime.entrySet()) {
			ShiftTime time = timeEntry.getKey();
            Map<Pos, Integer> posRequirements = timeEntry.getValue();

            System.out.println("<<< 時間帯: " + time.name() + " >>>");
            System.out.println("----------------------------------------------");
            
            // ポジションをループし、人数を出力
            for(Map.Entry<Pos, Integer> posEntry : posRequirements.entrySet()) {
                System.out.printf("| %-15s | %-5d |\n", posEntry.getKey().getDisplayName(), posEntry.getValue());
            }
            System.out.println("----------------------------------------------");
		}
		
		System.out.println("ポジション割り振り後の結果を出力します。");
	}
	
	//ポジション割り振り後の結果
	private void printFullAssignment(Map<ShiftTime, Map<Pos, List<Employee>>> finalShiftTable) {
		
		System.out.println("\n--- 決定されたポジション割り振り ---");
		System.out.println("==================================================");
		
		for(ShiftTime targetTime : ShiftTime.values()){
			
			Map<Pos, List<Employee>> posAssignments = finalShiftTable.getOrDefault(targetTime, Collections.emptyMap());
			
			System.out.println("\n<<< 時間帯 : " + targetTime.name() + " >>>");
			System.out.println("--------------------------------------------------------");
		
			System.out.printf("| %-15s | %s\n", "ポジション", "担当者"); // ヘッダー
			System.out.println("----------------------------------------------");
			
			boolean assigned = false;
		
			for(Pos p : Pos.values()) {
			
				List<Employee> assignedStaff =  posAssignments.getOrDefault(p, Collections.emptyList());
				String staffNames;
			
				if(assignedStaff.isEmpty()) {
					staffNames = "---担当者なし　---";
				}
				else {
					staffNames = assignedStaff.stream()
							.map(Employee::getName)
							.collect(Collectors.joining(", "));
					assigned = true;
				}
			
				System.out.printf("| %-15s | %s\n", p.getDisplayName(), staffNames);
			}
			
			if(!assigned) {
				System.out.printf("| %-31s |\n", "この時間帯にはスタッフの割り当てがありませんでした");
			}
		}
		
		System.out.println("==================================================");
		System.out.println("割り振り処理が完了しました。");
	}
}
