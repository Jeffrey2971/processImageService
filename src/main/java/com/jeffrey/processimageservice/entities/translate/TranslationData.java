package com.jeffrey.processimageservice.entities.translate;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 封装了翻译的信息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@Component
public class TranslationData {

    /**
     * 响应状态码
     */
    private Integer error_code;

    /**
     * 响应状态消息
     */
    private String error_msg;

    /**
     * 图片信息对象
     */
    private DataInfo data;

    @Data
    public static class DataInfo {

        /**
         * 原语种
         */
        private String from;

        /**
         * 目标语种
         */
        private String to;

        /**
         * 未分段翻译原文
         */
        private String sumSrc;

        /**
         * 未分段翻译译文
         */
        private String sumDst;

        /**
         * 图片贴合 (整屏贴合)，paste=1有效，base64格式
         */
        private String pasteImg;

        /**
         * 分段内容，Content内部是一个数组形式，具体数据格式参考 SegmentedData
         */
        private List<SegmentedData> content;

        @Data
        public static class SegmentedData {

            /**
             * 分段翻译的原文
             */
            private String src;

            /**
             * 分段翻译的译文
             */
            private String dst;

            /**
             * 表示识别出的文字的位置，坐标为左上角，依次是left、top、width、height
             */
            private String rect;

            /**
             * 表示该分段信息是原文的多少行合并在一起
             */
            private Integer lineCount;

            /**
             * 译文矩形坐标，坐标为左上角，坐标顺序左上，右上，右下，左下
             *      格式：
             *          {
             *              {
             *                  {"x": 254,"y": 280},
             *                  {"x": 506,"y": 278},
             *                  {"x": 506,"y": 303},
             *                  {"x": 254,"y": 305}
             *              },
             *              {
             *                  {"x": 254,"y": 280},
             *                  {"x": 506,"y": 278},
             *                  {"x": 506,"y": 303},
             *                  {"x": 254,"y": 305}
             *              }
             *          }
             */
            private List<Point> points;

            @Data
            public static class Point {

                /**
                 * 坐标轴 x
                 */
                private Integer x;

                /**
                 * 坐标轴 y
                 */
                private Integer y;
            }
        }
    }
}
