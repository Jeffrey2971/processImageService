package com.jeffrey.processimageservice.service;


import com.jeffrey.processimageservice.entities.register.RegisterParams;
import com.jeffrey.processimageservice.entities.enums.LoginStatus;
import com.jeffrey.processimageservice.exception.exception.clitent.RegisterException;

/**
 * @author jeffrey
 */
public interface RegisterControllerService {

    /**
     * 根据 username 查询该账号是否存在（username 和 email 任意一个相同都视为存在），
     *      如存在则返回该账号的唯一 id 如不存在则直接返回 null
     * @param registerParams RegisterParams 对象，需要使用到 username 字段
     * @return Integer
     */
    Enum<LoginStatus> accountIsExists(RegisterParams registerParams);

    Enum<LoginStatus> usernameCanUse(String username);

    Enum<LoginStatus> emailCanUse(String email);

    /**
     * 查看参数是否符合预期要求，需确认前端正则规则和后端正则规则相匹配
     * @param registerParams RegisterParams 对象
     * @return boolean
     */
    boolean paramsCheck(RegisterParams registerParams);

    /**
     * 注册账号，RequestParams 在插入数据库钱会被包装为一个 RequestParamsWrapper 对象，插入成功后会返回一个插入后自增的 id
     * @param registerParams RequestParams 对象，为校验后的请求参数基本信息
     * @return account id
     */
    Integer register(RegisterParams registerParams) throws RegisterException;
    /**
     * 为新建的账号初始化一个共钥和私钥，并存储到数据库
     */
    void creationKey(int aid);

    boolean usernameIsLegal(String username);

    boolean passwordIsLegal(String password);

}
