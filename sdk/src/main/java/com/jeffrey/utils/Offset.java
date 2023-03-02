package com.jeffrey.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
public class Offset {
    private static final HashMap<String, Integer> OFFSET = new HashMap<>(4);
    private static final Integer DEFAULT = 0;

    /**
     * 通过调用该方法从而添加处理水印矩形坐标时的偏移量，未提供的自动补 0
     *
     * @param offset 偏移量
     * @param coordinate 坐标轴
     * @param type 移动方向
     * @return this
     */
    public Offset addParam(int offset, OffSetEnum coordinate, OffSetEnum type) {

        String offsetStr = String.valueOf(offset).replace("+", "").replace("-", "");

        if (type.equals(OffSetEnum.POSITIVE)) {
            OFFSET.put("+" + coordinate.toString(), Integer.parseInt(offsetStr));
        } else {
            OFFSET.put("-" + coordinate.toString(), Integer.parseInt(offsetStr));
        }

        return this;
    }

    /**
     * 将添加的矩形偏移量转换为可直接上传的 json 字符串
     * @return offset json str
     */
    public String toJsonStr() {
        this.completion();
        return new Gson().toJson(OFFSET).toLowerCase();
    }

    /**
     * 参数自动补全，如在客户端不补全在服务端会自动补全
     */
    private void completion() {
        if (!OFFSET.containsKey("+X") && !OFFSET.containsKey("-X")) {
            OFFSET.put("+X", DEFAULT);
            log.info("未提供矩形坐标轴 X ，已设为默认值");
        }
        if (!OFFSET.containsKey("+Y") && !OFFSET.containsKey("-Y")) {
            OFFSET.put("+Y", DEFAULT);
            log.info("未提供矩形坐标轴 Y ，已设为默认值");
        }
        if (!OFFSET.containsKey("+W") && !OFFSET.containsKey("-W")) {
            OFFSET.put("+W", DEFAULT);
            log.info("未提供矩形坐标轴 W ，已设为默认值");
        }
        if (!OFFSET.containsKey("+H") && !OFFSET.containsKey("-H")) {
            OFFSET.put("+H", DEFAULT);
            log.info("未提供矩形坐标轴 H ，已设为默认值");
        }
    }
}

