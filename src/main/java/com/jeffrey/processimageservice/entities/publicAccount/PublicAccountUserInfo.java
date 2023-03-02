package com.jeffrey.processimageservice.entities.publicAccount;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class PublicAccountUserInfo {
    private Integer id;
    private Integer subscribe;
    private String openid;
    private String language;
    private Object subscribe_time;
    private String unionid;
    private String remark;
    private Integer groupid;
    private Object tagid_list;
    private String subscribe_scene;
    private Integer qr_scene;
    private String qr_scene_str;
}
