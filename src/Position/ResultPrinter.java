package Position;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultPrinter {
	
	//コンストラクタ
	public ResultPrinter() {
		
	}
	
	public void printFullResult(List<Employee> workingStaff, Schedule conditions, Map<Pos, List<Employee>> finalList) {
		printPreAssignDetails(workingStaff, conditions);
		printFullAssignment(finalList);
	}
	
	private void printPreAssignDetails(List<Employee> workingStaff, Schedule conditions) {
		System.out.println("\n--- シフト開始前の情報 ---");
		System.out.println("出勤者数: " + workingStaff.size() + "名");
		
		System.out.println("\n--- 出勤者リスト ---");
		System.out.println("-----------------------------");
		for(Employee e : workingStaff) {
			System.out.printf("| %-15s |\n", e.getName());
		}
		System.out.println("-----------------------------");
		
		System.out.println("\n[ポジション必要人数]");
		System.out.println("----------------------");
		for(Map.Entry<Pos, Integer> entry : conditions.getRequiredCounts().entrySet()) {
			System.out.printf("| %-15s | %-5d |\n", entry.getKey().getDisplayName(), entry.getValue());
		}
		System.out.println("----------------------");
		
		System.out.println("ポジション割り振り後の結果を出力します。");
	}
	
	//ポジション割り振り後の結果
	private void printFullAssignment(Map<Pos, List<Employee>> finalList) {
		
		System.out.println("\n--- 決定されたポジション割り振り ---");
		System.out.println("==================================================");
		
		System.out.printf("| %-15s | %s\n", "ポジション", "担当者"); // ヘッダー
		System.out.println("----------------------------------------------");
		
		for(Pos p : Pos.values()) {
			
			 List<Employee> assignedStaff =  finalList.get(p);
			String staffNames;
			
			if(finalList.get(p) == null || finalList.get(p).isEmpty()) {
				staffNames = "---担当者なし　---";
			}
			else {
				staffNames = assignedStaff.stream()
						.map(Employee::getName)
						.collect(Collectors.joining(", "));
			}
			
			System.out.printf("| %-15s | %s\n", p.getDisplayName(), staffNames);
		}
		
		System.out.println("==================================================");
		System.out.println("割り振り処理が完了しました。");
	}
}
