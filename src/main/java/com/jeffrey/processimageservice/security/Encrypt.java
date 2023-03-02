package com.jeffrey.processimageservice.security;

import com.jeffrey.processimageservice.entities.Info;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Data
public class Encrypt {
    private final Info info;

    @Autowired
    public Encrypt(Info info) {
        this.info = info;
    }

}
