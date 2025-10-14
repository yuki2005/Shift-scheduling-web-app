package Position;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@ComponentScan(basePackages = {"Position"})

public class ShiftApplication {
		
	private final InputHandler inputHandler;
	private final AutoShift autoShift;
	private final ResultPrinter resultPrinter;
	private final PosAssign posAssign;
	
		
	public ShiftApplication() {
		this.inputHandler = new InputHandler();
		this.autoShift = new AutoShift(new EfficiencySelectStrategy());
		this.resultPrinter = new ResultPrinter();
		this.posAssign = new PosAssign(new MaxSkillStrategy());
	}
	
	//処理全体を管理する
	public void start(Scanner scanner) {
		
		//入力をおこなう
		List<Employee> employees = inputHandler.readEmployees(scanner);
		DayOfWeek day = inputHandler.readDayOfWeek(scanner);
		boolean isHoliday = inputHandler.readIsHoliday(scanner);
		Schedule dayDate = new Schedule(day, isHoliday);
		
		int totalRequired = dayDate.getRequiredCounts().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
		
		if(totalRequired > employees.size()) {
			System.out.println("募集人数が推奨人数に達していません。募集をかけてください。");
		}
		else {
			System.out.println("入力が完了しました。");
		}
		
		//出勤する人を決める
		List<Employee> commuteStaff = autoShift.selectEmployees(employees, dayDate);
		
		//ポジションを割り振る
		Map<Pos, List<Employee>> result = posAssign.execute(commuteStaff, dayDate);
		
		//結果を出力する
		resultPrinter.printFullResult(employees, dayDate, result);
	}
	
	//プログラムのエントリーポイント	
	public static void ShiftApp(String args[]) {
		try(Scanner scanner = new Scanner(System.in)){
			ShiftApplication app = new ShiftApplication();
			app.start(scanner);
		}
		catch(Exception e) {
			System.err.println("致命的なエラーが発生しました:" + e.getMessage());
		}
	}
}
