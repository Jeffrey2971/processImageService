package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.conf.BCryptPasswordEncoder;
import com.jeffrey.processimageservice.conf.InitAccountParamProperties;
import com.jeffrey.processimageservice.entities.register.RegisterParams;
import com.jeffrey.processimageservice.entities.register.RegisterParamsWrapper;
import com.jeffrey.processimageservice.enums.LoginStatus;
import com.jeffrey.processimageservice.exception.exception.clitent.RegisterException;
import com.jeffrey.processimageservice.mapper.RegisterControllerServiceMapper;
import com.jeffrey.processimageservice.service.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";

    private static final String DATA_FOR_RANDOM_PUBLIC_KEY = CHAR_LOWER + CHAR_UPPER + NUMBER;

    private static final String DATA_FOR_RANDOM_PRIVATE_KEY = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BCryptPasswordEncoder encoder;
    private final RegisterControllerServiceMapper registerControllerServiceMapper;
    private final InitAccountParamProperties initAccountParamProperties;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[\\w-]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");


    @Autowired
    public RegisterServiceImpl(BCryptPasswordEncoder encoder, RegisterControllerServiceMapper registerControllerServiceMapper, InitAccountParamProperties initAccountParamProperties) {
        this.encoder = encoder;
        this.registerControllerServiceMapper = registerControllerServiceMapper;
        this.initAccountParamProperties = initAccountParamProperties;
    }

    @Override
    public boolean paramsCheck(RegisterParams registerParams) {

        if (Stream.of(registerParams.getUsername(), registerParams.getPassword(), registerParams.getEnsure(), registerParams.getEmail()).anyMatch(StringUtils::isEmpty)) {
            return false;
        }
        if (!USERNAME_PATTERN.matcher(registerParams.getUsername()).matches()) {
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(registerParams.getPassword()).matches()) {
            return false;
        }
        if (!registerParams.getPassword().equals(registerParams.getEnsure())) {
            return false;
        }
        if (!EMAIL_PATTERN.matcher(registerParams.getEmail()).matches()) {
            return false;
        }
        return true;



    }

    @Override
    public Enum<LoginStatus> accountIsExists(RegisterParams registerParams) {

//        registerParams = registerControllerServiceMapper.usernameIsExists(registerParams.getUsername(), registerParams.getEmail());

        return null;
    }

    @Override
    public Enum<LoginStatus> usernameCanUse(String username) {
        Integer count = registerControllerServiceMapper.usernameIsExists(username);
        if (count == null || count <= 0) {
            return LoginStatus.OK;
        }
        return LoginStatus.SC_REGISTER_USERNAME_EXISTS;
    }

    @Override
    public Enum<LoginStatus> emailCanUse(String email) {
        Integer count = registerControllerServiceMapper.emailIsExists(email);
        if (count == null || count <= 0) {
            return LoginStatus.OK;
        }
        return LoginStatus.SC_REGISTER_EMAIL_EXISTS;
    }

    @Override
    public Integer register(RegisterParams registerParams) throws RegisterException{

        String hashPassword = encoder.encode(registerParams.getPassword());

        registerParams.setPassword(hashPassword);

        RegisterParamsWrapper registerParamsWrapper = new RegisterParamsWrapper(
                registerParams,
                initAccountParamProperties,
                SDF.format(new Date(System.currentTimeMillis())),
                null
        );

        try {
            registerControllerServiceMapper.register(registerParamsWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegisterException("账号注册异常");
        }

        return registerParamsWrapper.getId();
    }

    @Override
    public void creationKey(int aid) {

        String creationPublicKey = generateRandomString(16, DATA_FOR_RANDOM_PUBLIC_KEY);
        String creationPrivateKey = generateRandomString(32, DATA_FOR_RANDOM_PRIVATE_KEY);

        registerControllerServiceMapper.creationKey(aid, creationPublicKey, creationPrivateKey);

    }

    @Override
    public boolean usernameIsLegal(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    @Override
    public boolean passwordIsLegal(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private static String generateRandomString(int length, String targetStr) {

        if (length < 1) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = RANDOM.nextInt(targetStr.length());
            char rndChar = targetStr.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }
}
