package Position;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Position パッケージを明示的にスキャンさせる
@ComponentScan(basePackages = {"Position"}) 
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
