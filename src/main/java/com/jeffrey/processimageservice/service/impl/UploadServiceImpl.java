package com.jeffrey.processimageservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeffrey.processimageservice.entities.response.Point;
import com.jeffrey.processimageservice.entities.RequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.enums.SupportUploadImageType;
import com.jeffrey.processimageservice.entities.enums.UploadMethodType;
import com.jeffrey.processimageservice.exception.exception.ArgumentsOverwriteException;
import com.jeffrey.processimageservice.exception.exception.FileTooLargeException;
import com.jeffrey.processimageservice.exception.exception.ImageTypeNotSupportedException;
import com.jeffrey.processimageservice.exception.exception.UnknownArgumentException;
import com.jeffrey.processimageservice.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j

public class UploadServiceImpl implements UploadService {

    private static final ArrayList<String> SUPPORT_OFFSET = new ArrayList<>(4);
    private static final ArrayList<String> SUPPORT_IMAGE_BASE64_PREFIX = new ArrayList<>(3);

    private static final Long IMAGE_MAX_SIZE = 4194304L;

    static {
        SUPPORT_OFFSET.add("+x");
        SUPPORT_OFFSET.add("-x");

        SUPPORT_OFFSET.add("+y");
        SUPPORT_OFFSET.add("-y");

        SUPPORT_OFFSET.add("+w");
        SUPPORT_OFFSET.add("-w");

        SUPPORT_OFFSET.add("+h");
        SUPPORT_OFFSET.add("-h");

        SUPPORT_IMAGE_BASE64_PREFIX.add("data:image/png;base64,");
        SUPPORT_IMAGE_BASE64_PREFIX.add("data:image/bmp;base64,");
        SUPPORT_IMAGE_BASE64_PREFIX.add("data:image/jpeg;base64,");
    }

    @Override
    public RequestParamsWrapper argumentsOverwrite(RequestParams params) {


        String imageUrl = params.getImageUrl();
        String imageBase64 = params.getImageBase64();
        MultipartFile imageFile = params.getUploadFile();

        Object offset = params.getOffset();
        Object rectangles = params.getRectangles();
        Object watermarkName = params.getWatermarkName();

        Object sync = params.getSync();
        String callback = params.getCallback();

        Object excludeKeywords = params.getExcludeKeywords();

        RequestParamsWrapper requestParamsWrapper = new RequestParamsWrapper();


        // Pattern urlReg = Pattern.compile("^(((ht|f)tps?)://)?[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$");
        Pattern urlReg = Pattern.compile("^((ht|f)tps?)://\\S*");

        try {

            if (!StringUtils.hasText(imageUrl) && !StringUtils.hasText(imageBase64) && (imageFile == null || imageFile.isEmpty())) {
                throw new ArgumentsOverwriteException("没有指定任何一种文件上传方式");
            }

            try {

                if (!imageFile.isEmpty()) {
                    if (imageFile.getSize() > IMAGE_MAX_SIZE) {
                        throw new FileTooLargeException("上传图片的大小超过 4Mb");
                    }
                    byte[] imageBytes = FileCopyUtils.copyToByteArray(imageFile.getInputStream());

                    SupportUploadImageType supportUploadImageType = getSupportUploadImageType(imageBytes);

                    if (supportUploadImageType == null) {
                        throw new ImageTypeNotSupportedException("上传的文件不是受支持的类型");
                    }

                    requestParamsWrapper.setSupportUploadImageTypeEnum(supportUploadImageType);
                    requestParamsWrapper.setUploadMethodTypeEnum(UploadMethodType.FILE);
                    requestParamsWrapper.setFinalImageBytes(imageBytes);


                    params.setImageUrl(null);
                    params.setImageBase64(null);

                } else if (StringUtils.hasText(imageUrl)) {

                    if (!urlReg.matcher(imageUrl).matches()) {
                        throw new ArgumentsOverwriteException("提供的图片 URL 地址无效");
                    }

                    URLConnection urlConnection = new URL(imageUrl).openConnection();

                    if (urlConnection.getContentLength() > IMAGE_MAX_SIZE) {
                        throw new FileTooLargeException("URL 目标文件大小超过 4Mb");
                    }

                    // 如需校验，一定要放到大小校验之后
                    InputStream is = urlConnection.getInputStream();
                    byte[] imageBytes = FileCopyUtils.copyToByteArray(is);


                    SupportUploadImageType supportUploadImageType = getSupportUploadImageType(imageBytes);

                    if (supportUploadImageType == null) {
                        throw new ImageTypeNotSupportedException("URL 目标文件不是受支持的类型");
                    }

                    requestParamsWrapper.setSupportUploadImageTypeEnum(supportUploadImageType);
                    requestParamsWrapper.setUploadMethodTypeEnum(UploadMethodType.URL);
                    requestParamsWrapper.setFinalImageBytes(imageBytes);

                    params.setUploadFile(null);
                    params.setImageBase64(null);

                } else if (StringUtils.hasText(imageBase64)) {
                    // data:[][;charset=][;base64],

                    if (imageBase64.length() > 23 && SUPPORT_IMAGE_BASE64_PREFIX.contains(imageBase64.substring(0, imageBase64.indexOf(',') + 1))) {


                        String base64 = imageBase64.substring(imageBase64.indexOf(",", 1) + 1, imageBase64.length());

                        BASE64Decoder base64Decoder = new BASE64Decoder();
                        byte[] bytes = base64Decoder.decodeBuffer(base64);

                        SupportUploadImageType supportUploadImageType = getSupportUploadImageType(bytes);

                        if (supportUploadImageType == null) {
                            throw new ImageTypeNotSupportedException("上传的图片 base64 编码不是受支持的类型，请提供受支持的图片类型");
                        }

                        requestParamsWrapper.setSupportUploadImageTypeEnum(supportUploadImageType);
                        requestParamsWrapper.setUploadMethodTypeEnum(UploadMethodType.BASE64);
                        requestParamsWrapper.setFinalImageBytes(bytes);

                        params.setUploadFile(null);
                        params.setImageUrl(null);
                    } else {
                        throw new ImageTypeNotSupportedException("上传的图片 base64 编码不是受支持的类型，请遵循 data:[][;charset=][;base64],");
                    }
                } else {
                    throw new ArgumentsOverwriteException("未知参数异常");
                }
            } catch (IOException e) {
                throw new UnknownArgumentException("下载目标 URL 资源失败");
            } finally {

            }

            // 重写 rectangles
            if (rectangles instanceof String) {
                String param = (String) rectangles;
                if (StringUtils.hasText(param)) {
                    JsonNode root = new ObjectMapper().readTree(param);
                    if (root.size() <= 0) {
                        throw new ArgumentsOverwriteException("空的 Json 树");
                    }
                    ArrayList<Point> points = new ArrayList<>();
                    for (JsonNode node : root) {
                        JsonNode x = node.get("x");
                        JsonNode y = node.get("y");
                        JsonNode w = node.get("w");
                        JsonNode h = node.get("h");
                        if (x != null && x.isNumber() && y != null && y.isNumber() && w != null && w.isNumber() && h != null && h.isNumber()) {
                            points.add(new Point(x.asInt(), y.asInt(), w.asInt(), h.asInt(), null, null));
                        } else {
                            throw new ArgumentsOverwriteException("提供了不正确的矩阵坐标");
                        }
                    }
                    params.setRectangles(points);
                }
            } else if (rectangles != null) {
                throw new ArgumentsOverwriteException("提供的参数不是字符串");
            }

            // 校验并重写水印名称
            if (watermarkName instanceof String) {
                String param = (String) watermarkName;
                if (StringUtils.hasText(param)) {
                    JsonNode root = new ObjectMapper().readTree(param);
                    JsonNode markNames = root.get("markNames");
                    if (markNames != null && markNames.size() > 0) {
                        ArrayList<String> markNamesList = new ArrayList<>();
                        for (JsonNode markName : markNames) {
                            if (StringUtils.hasText(markName.toString())) {
                                markNamesList.add(markName.toString());
                            } else {
                                throw new ArgumentsOverwriteException("在参数 markNames 中，发现空项");
                            }
                        }
                        params.setWatermarkName(markNamesList);
                    } else {
                        throw new ArgumentsOverwriteException("无效或空的 Json 树");
                    }
                }
            } else if (watermarkName != null) {
                throw new ArgumentsOverwriteException("提供的参数不是字符串");
            }

            if (excludeKeywords instanceof String) {
                String param = (String) excludeKeywords;
                if (StringUtils.hasText(param)) {
                    JsonNode root = new ObjectMapper().readTree(param);
                    if (root.get("keywords") != null && root.size() > 0) {
                        ArrayList<String> keyWords = new ArrayList<>();
                        for (JsonNode jsonNode : root) {
                            if (StringUtils.hasText(jsonNode.toString())) {
                                keyWords.add(jsonNode.toString());
                            } else {
                                throw new ArgumentsOverwriteException("在参数 excludeKeywords 中，发现空项");
                            }
                        }
                        params.setExcludeKeywords(keyWords);
                    } else {
                        throw new ArgumentsOverwriteException("空的 Json 树");
                    }
                }
            } else if (excludeKeywords != null) {
                throw new ArgumentsOverwriteException("提供的参数不是字符串");
            }

            if (offset instanceof String) {
                String param = (String) offset;
                if (StringUtils.hasText(param)) {
                    JsonNode root = new ObjectMapper().readTree(param);
                    if (root.size() <= 0) {
                        throw new ArgumentsOverwriteException("空的 Json 树");
                    }
                    if (root.size() > 4) {
                        throw new ArgumentsOverwriteException("参数过多");
                    }

                    // 使用 LinkedHashMap 确保原始索引
                    LinkedHashMap<String, Integer> offsetMap = new LinkedHashMap<>(4);
                    root.fields().forEachRemaining(item -> {
                        String key = item.getKey();
                        if (SUPPORT_OFFSET.contains(key)) {
                            JsonNode value = item.getValue();
                            String str = value.toString();
                            if (StringUtils.hasText(str) && value.isNumber()) {
                                offsetMap.put(key, value.asInt());
                            } else {
                                throw new ArgumentsOverwriteException("offer 坐标的 value 为空或不是整形");
                            }
                        } else {
                            throw new ArgumentsOverwriteException("offset 坐标的 key 只能为 +x -x +y -y +w -w +h -h");
                        }
                    });
                    params.setOffset(offsetMap);
                }
            } else if (offset != null) {
                throw new ArgumentsOverwriteException("提供的参数不是字符串");
            }

            if (sync instanceof String) {
                String param = (String) sync;
                if (StringUtils.hasText(param)) {
                    if ("true".equals(param)) {
                        params.setSync(true);
                    } else if ("false".equals(param)) {
                        params.setSync(false);
                    } else {
                        throw new ArgumentsOverwriteException("参数 sync 只能设置为 true 或 false");
                    }
                } else {
                    params.setSync(null);
                }
            } else if (sync != null) {
                throw new ArgumentsOverwriteException("提供的参数不是字符串");
            }

            Object param = params.getSync();
            if (param instanceof Boolean && (Boolean) param) {
                if (StringUtils.hasText(callback)) {
                    if (!urlReg.matcher(callback).matches()) {
                        throw new ArgumentsOverwriteException("提供的 callback 不是一个有效的回调接口");
                    }
                } else {
                    throw new ArgumentsOverwriteException("当参数 sync 为 true 时，必须设置 callback 回调接口");
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ArgumentsOverwriteException("无法解析提供的 json 字符串，请检查语法是否有误");
        }

        // log.info("param requestParams may changed.");

        requestParamsWrapper.setRequestParams(params);

        return requestParamsWrapper;
    }

    /**
     * 根据给定的输入流返回该输入流属于什么图片类型，该方法需要将输入流重制回起点，避免其他地方用到这个流抛 stream is closed 异常
     *
     * @param bytes 图片输入字节数组
     * @return SupportUploadImageType 为一个枚举类型
     * @throws IOException IOException
     */
    private SupportUploadImageType getSupportUploadImageType(byte[] bytes) throws IOException {

        String type = new Tika().detect(bytes);

        return "image/png".equals(type)
                ? SupportUploadImageType.IMAGE_PNG : "image/bmp".equals(type)
                ? SupportUploadImageType.IMAGE_BMP : "image/jpeg".equals(type)
                ? SupportUploadImageType.IMAGE_JPEG : null;
    }
}

