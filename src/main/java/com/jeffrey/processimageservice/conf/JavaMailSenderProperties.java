package com.jeffrey.processimageservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
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
