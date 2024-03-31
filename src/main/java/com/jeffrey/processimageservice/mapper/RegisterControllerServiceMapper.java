package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.register.RegisterParamsWrapper;
import org.springframework.stereotype.Repository;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Repository
public interface RegisterControllerServiceMapper {
    void creationKey(int id, String appId, String appSecret);

    void register(RegisterParamsWrapper registerParamsWrapper);

    Integer usernameIsExists(String username);

    Integer emailIsExists(String email);
}
