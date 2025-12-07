package Position;

import java.util.Scanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@ComponentScan(basePackages = {"Position"})

public class ShiftApplication {
		
	
		
	public ShiftApplication() {
	}
	
	//処理全体を管理する
	public void start(Scanner scanner) {
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
