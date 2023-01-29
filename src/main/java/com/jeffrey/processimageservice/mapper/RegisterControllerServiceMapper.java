package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.RegisterParams;
import com.jeffrey.processimageservice.entities.RegisterParamsWrapper;
import org.springframework.stereotype.Repository;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Repository
public interface RegisterControllerServiceMapper {
    void creationKey(int id, String publicKey, String privateKey);

    void register(RegisterParamsWrapper registerParamsWrapper);

    Integer usernameIsExists(String username);

    Integer emailIsExists(String email);
}
