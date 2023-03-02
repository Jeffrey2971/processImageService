package com.jeffrey;

import java.io.File;

/**
 * public_key 和 private_key 请 <a href="https://www.processimage.cn:4433/login">到这里获取</a>
 *
 * @author jeffrey
 * @since JDK 1.8
 */

public class Const {
    public static final String PUBLIC_KEY = "fCmTdaaURBy0IGf6";
    public static final String PRIVATE_KEY = "91Ut*$seTuHbNp8aVmp6z98mtaVBEO_U";
    public static final File IMAGE_FILE = new File("/Users/jeffrey/Desktop/demo.jpg");
    public static final String TARGET_URL = "https://www.processimage.cn:4433/access";

    static {

        if (PUBLIC_KEY == null || PUBLIC_KEY.length() != 16) {
            throw new RuntimeException("参数 PUBLIC_KEY 为空或它的长度不等于 16 位");
        } else if (PRIVATE_KEY == null || PRIVATE_KEY.length() != 32) {
            throw new RuntimeException("参数 PRIVATE_KEY 为空或它的长度不等于 32 位");
        } else if (IMAGE_FILE == null || !IMAGE_FILE.exists() || !IMAGE_FILE.isFile()) {
            throw new RuntimeException("参数 IMAGE_FILE 不能为空，以及它必须存在而且是一个文件");
        }
    }
}
