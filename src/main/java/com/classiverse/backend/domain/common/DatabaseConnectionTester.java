package com.classiverse.backend.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DatabaseConnectionTester implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("\n=========================================================");
            System.out.println("✅ DB 연결 성공! (URL: " + connection.getMetaData().getURL() + ")");
            System.out.println("=========================================================\n");
        } catch (Exception e) {
            System.out.println("\n=========================================================");
            System.out.println("❌ DB 연결 실패! 설정 파일을 확인하세요.");
            System.out.println("에러 메시지: " + e.getMessage());
            System.out.println("=========================================================\n");
        }
    }
}