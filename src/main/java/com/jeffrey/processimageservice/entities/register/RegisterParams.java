package com.jeffrey.processimageservice.entities.register;

import lombok.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
@Getter
@Setter
public class RegisterParams extends GenericLoginParams {

    private String ensure;
    private String email;

    public RegisterParams(String username, String password, String ensure, String email) {
        super(username, password);
        this.ensure = ensure;
        this.email = email;
    }

    public RegisterParams(String ensure, String email) {
        this.ensure = ensure;
        this.email = email;
    }
}
