package position;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * シフト管理アプリケーションのエントリーポイント。
 *
 * Spring Boot を起動し、Controller / Service / Repository などの
 * コンポーネントを自動検出してアプリケーション全体を開始する。
 */
@SpringBootApplication
public class Main {

    /**
     * アプリケーションの起動処理。
     *
     * @param args 起動引数
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}