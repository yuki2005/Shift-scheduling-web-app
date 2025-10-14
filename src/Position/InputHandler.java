package Position;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Scanner;

public class InputHandler {
	
	//コンストラクタ
	public InputHandler() {
		
	}
	
	//その日出勤する従業員を入力する
	public List<Employee> readEmployees(Scanner scanner) {
		//
		final int count;
		List<Employee> employees = new ArrayList<>();
		
		System.out.println("出勤可能な応募数を入力してください。");
		while(true) {
			int inputCount = readAndValidateInt(scanner);
			if(inputCount > 0) {
				count = inputCount;
				break;
			}
			else {
				System.out.println("応募数は1以上の整数で入力してください。");
			}
		}
		
		//従業員を入力していく
		for(int i = 0; i < count; i++) {
			Map<Pos, Integer> skills = new HashMap<Pos, Integer>();
			System.out.println("社員番号を入力してください。");
			int id = readAndValidateInt(scanner);
			
			System.out.println("名前を入力してください。");
			String name = readString(scanner);
			
			System.out.println("各ポジションの強さを入力してください。");
			for(Pos pos : Pos.values()) {
				System.out.print(pos.getDisplayName() + ":");
				int skillValue = readAndValidateSkill(scanner);
				skills.put(pos, skillValue);
			}
			Employee newEmployee = new Employee(id, name, skills);
			employees.add(newEmployee);
		}
		
		
		return Collections.unmodifiableList(employees);
	}
	//曜日を入力する
	public DayOfWeek readDayOfWeek(Scanner scanner) {
		while(true) {
			System.out.println("曜日を入力してください。");
			String day = scanner.nextLine().trim().toUpperCase();
			
			try {
				//文字列をDayOfWeekに変換
				return DayOfWeek.valueOf(day);
			}
			catch(IllegalArgumentException e) {
				System.out.println("曜日を正しく入力してください。");
			}
			
		}
	}
	//祝日かどうかを入力
	public boolean readIsHoliday(Scanner scanner) {
		System.out.println("今日は祝日ですか？");
		
		while(true) {
			String input = scanner.nextLine().trim().toLowerCase();
			
			if(input.equals("y") || input.equals("yes")) {
				return true;
			}else if(input.equals("n") || input.equals("no")) {
				return false;
			}
			else {
				System.out.println("'y'または'n'で入力してください。");
			}
		}
	}
	
	private int readAndValidateInt(Scanner scanner) {
		while(true) {
			String line = scanner.nextLine().trim();
			try {
				return Integer.parseInt(line);
			}catch(NumberFormatException e) {
				System.out.println("無効な入力です。整数値を入力してください。");
			}
		}
	}
	
	private int readAndValidateSkill(Scanner scanner) {
		while(true) {
			String line = scanner.nextLine().trim();
			try {
				int value = Integer.parseInt(line);
				if(value >= 0 && value <= 10) return value;
				else System.out.println("0以上10以下の整数で入力してください。");
			}catch(NumberFormatException e) {
				System.out.println("整数値を入力してください。");
			}
		}
	}
	
	private String readString(Scanner scanner) {
		return scanner.nextLine().trim();
	}
}
