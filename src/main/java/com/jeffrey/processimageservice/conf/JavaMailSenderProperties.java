package com.jeffrey.processimageservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 *     host: smtp.office365.com
 *     port: 587
 *     # 需要先创建双从认证授权码后再创建应用密码
 *     username: ServiceSafetyReport@outlook.com
 *     password: nrcjjxetwdoismsc
 *     properties:
 *       # mail.smtp.auth: true
 *       # mail.smtp.ssl.enable: true
 *       mail.smtp.starttls.enable: true
 *       mail.smtp.starttls.required: true
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@ConfigurationProperties(prefix = "mail-conf")
@Data
public class JavaMailSenderProperties {
    private String host;
    private Integer port;
    private String dualAuthenticationUsername;
    private String dualAuthenticationPassword;
    private String from;
    private String[] cc;
    private Boolean mailSmtpStarttlsEnable;
    private Boolean mailSmtpStarttlsRequired;
}
