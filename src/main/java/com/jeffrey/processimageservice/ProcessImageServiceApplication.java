package com.jeffrey.processimageservice;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.File;
import java.util.Objects;

/**
 * @author jeffrey
 */
@SpringBootApplication
//@EnableWebSecurity
@EnableAsync
@EnableCaching
@EnableScheduling
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.jeffrey.processimageservice.mapper")
public class ProcessImageServiceApplication {

    private static boolean projectStartedMode;
    private static String appPath;
    private static File originImagePath;
    private static File targetImage;

//    static {
//        // 在启动时启用 Java 远程调试
//        System.setProperty("java.awt.headless", "false");
//        System.setProperty("java.rmi.server.hostname", "localhost");
//        System.setProperty("com.sun.management.jmxremote", "");
//        System.setProperty("com.sun.management.jmxremote.port", "9000");
//        System.setProperty("com.sun.management.jmxremote.ssl", "false");
//        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
//        System.setProperty("com.sun.management.jmxremote.local.only", "false");
//        System.setProperty("com.sun.management.jmxremote.rmi.port", "9000");
//        System.setProperty("com.sun.management.jmxremote.host", "localhost");
//        System.setProperty("java.net.preferIPv4Stack", "true");
//        System.setProperty("java.rmi.server.useLocalHostname", "true");
//        System.setProperty("java.rmi.server.codebase", ProcessImageServiceApplication.class.getProtectionDomain().getCodeSource().getLocation().toString());
//    }

    public static void main(String[] args) {

        for (String str : Objects.requireNonNull(ProcessImageServiceApplication.class.getResource("/")).getPath().split("/")) {
            if (str.contains(".jar")) {
                projectStartedMode = true;
                break;
            }
        }

        ApplicationHome applicationHome = new ApplicationHome(ProcessImageServiceApplication.class);

        File source = applicationHome.getSource();

        if (source == null) {
            throw new RuntimeException("无法确定项目资源路径");
        }

        appPath = source.getParentFile().getPath();

        originImagePath = new File(appPath, "/originImage");
        if (!originImagePath.exists() && !originImagePath.mkdirs()) {
            throw new RuntimeException("创建资源目录失败");
        }

        targetImage = new File(appPath, "/targetImage");
        if (!targetImage.exists() && !targetImage.mkdirs()) {
            throw new RuntimeException("创建资源目录失败");
        }

        SpringApplication.run(ProcessImageServiceApplication.class, args);

        log.info("项目正运行于 {}", appPath);
        log.info("项目正在以 {} 方式启动", projectStartedMode ? "jar 环境" : "非 jar 环境");
    }

    public static File getAppPath() {
        return new File(appPath);
    }

    public static File getOriginImagePath() {
        return originImagePath;
    }

    public static File getTargetImage() {
        return targetImage;
    }

}
