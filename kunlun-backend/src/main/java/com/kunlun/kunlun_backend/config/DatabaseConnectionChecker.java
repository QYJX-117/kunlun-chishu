/**
 * 数据库连接检测器 —— 启动时验证数据库连接
 * 连接成功打印数据库版本信息，失败打印详细错误原因
 */

package com.kunlun.kunlun_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Slf4j
@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseConnectionChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            log.info("========================================");
            log.info("  数据库连接成功！");
            log.info("  数据库: {}", meta.getDatabaseProductName());
            log.info("  版本: {}", meta.getDatabaseProductVersion());
            log.info("  URL: {}", meta.getURL());
            log.info("  驱动: {} {}", meta.getDriverName(), meta.getDriverVersion());
            log.info("========================================");
        } catch (Exception e) {
            log.error("========================================");
            log.error("  数据库连接失败！");
            log.error("  原因: {}", e.getMessage());
            log.error("  异常类型: {}", e.getClass().getName());
            log.error("========================================");
        }
    }
}
