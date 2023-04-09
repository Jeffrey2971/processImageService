package com.jeffrey.processimageservice.utils;

/**
 * 编辑距离（计算两个字符串之间的相似度）
 *
 * @author jeffrey
 * @since JDK 1.8
 */


public class EditDistanceUtil {

    public static float getSimilarityRatio(String str, String target) {

        int[][] d = new int[str.length() + 1][target.length() + 1];
        int maxLen = Math.max(str.length(), target.length());

        for (int i = 0; i <= str.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= target.length(); j++) {
            d[0][j] = j;
        }

        char[] strChars = str.toCharArray();
        char[] targetChars = target.toCharArray();

        for (int i = 1; i <= str.length(); i++) {
            for (int j = 1; j <= target.length(); j++) {
                int temp = (strChars[i - 1] == targetChars[j - 1]) ? 0 : 1;
                d[i][j] = Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + temp));
            }
        }

        return (1 - (float) d[str.length()][target.length()] / maxLen) * 100;
    }
}
