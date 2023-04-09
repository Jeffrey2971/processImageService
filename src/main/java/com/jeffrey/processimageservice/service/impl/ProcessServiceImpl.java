package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.ProcessImageServiceApplication;
import com.jeffrey.processimageservice.aop.annotation.api.CacheProcessedResponse;
import com.jeffrey.processimageservice.aop.annotation.api.UpdateAccountUsed;
import com.jeffrey.processimageservice.entities.*;
import com.jeffrey.processimageservice.enums.MarkType;
import com.jeffrey.processimageservice.enums.ResponseStatus;
import com.jeffrey.processimageservice.enums.SupportUploadImageType;
import com.jeffrey.processimageservice.entities.response.Data;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.response.Point;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.entities.translate.TranslationData;
import com.jeffrey.processimageservice.exception.exception.ArgumentsOverwriteException;
import com.jeffrey.processimageservice.exception.exception.ProcessImageFailedException;
import com.jeffrey.processimageservice.exception.exception.clitent.ImageTypeNotSupportedException;
import com.jeffrey.processimageservice.model.GenericMark;
import com.jeffrey.processimageservice.service.ProcessService;
import com.jeffrey.processimageservice.service.TranslateService;

import com.jeffrey.processimageservice.utils.EditDistanceUtil;
import com.jeffrey.processimageservice.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
@SuppressWarnings("unchecked")
public class ProcessServiceImpl implements ProcessService, Serializable {
    private final LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue;
    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;
    private final TranslateService translateService;
    private final TreeMap<Long, File> cacheTaskMap;
    private static String prev;
    private final Info info;

    @Autowired
    public ProcessServiceImpl(LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal, TranslateService translateService, TreeMap<Long, File> cacheTaskMap, Info info) {
        this.asyncPendingTasksItemLinkedBlockingQueue = asyncPendingTasksItemLinkedBlockingQueue;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
        this.translateService = translateService;
        this.cacheTaskMap = cacheTaskMap;
        this.info = info;
    }

    @SneakyThrows
    @Override
    @CacheProcessedResponse
    @UpdateAccountUsed
    public GenericResponse taskAllocation(RequestParamsWrapper requestParamsWrapper, boolean task, EncryptedInfo encryptedInfo, GenericResponse cacheGenericResponse) {

        // 如果存在缓存数据则直接返回
        if (cacheGenericResponse != null) {
            return cacheGenericResponse;
        }

        // 检查是否异步请求且不是异步任务回调
        if (requestParamsWrapper.getRequestParams().getSync() != null && (Boolean) requestParamsWrapper.getRequestParams().getSync() && !task) {

            AsyncPendingTasksItem asyncPendingTasksItem = new AsyncPendingTasksItem();
            asyncPendingTasksItem.setTaskCreateTimeStamp(System.currentTimeMillis());
            asyncPendingTasksItem.setRequestParamsWrapper(requestParamsWrapper);
            asyncPendingTasksItem.setEncryptedInfo(encryptedInfoThreadLocal.get());

            if (!asyncPendingTasksItemLinkedBlockingQueue.contains(asyncPendingTasksItem)) {
                asyncPendingTasksItemLinkedBlockingQueue.put(asyncPendingTasksItem);
                log.info("添加异步任务，当前长度：{}", asyncPendingTasksItemLinkedBlockingQueue.size());
            }

            return new GenericResponse(
                    ResponseStatus.SC_OK.getValue(),
                    "SUCCESS::调用成功",
                    "json", null,
                    null, null,
                    null,
                    new Data(ResponseStatus.SC_PROCESS_ASYNC_REQUEST.getValue(), null, "WAITING::异步请求", null, null),
                    null
            );
        }

        // 处理请求
        Data data = this.genericProcess(requestParamsWrapper);

        return new GenericResponse(ResponseStatus.SC_OK.getValue(),
                "SUCCESS::调用成功",
                "json",
                null,
                null,
                null,
                null,
                data,
                encryptedInfo
        );
    }

    public Data genericProcess(RequestParamsWrapper wrapper) throws ProcessImageFailedException {

        this.ensureParams(wrapper);

        int processStatus;

        RequestParams requestParams = wrapper.getRequestParams();

        Object ocrRectangles = requestParams.getRectangles();

        ArrayList<Point> rectangles = (ArrayList<Point>) ocrRectangles;

        if (rectangles.size() == 0) {
            return new Data(ResponseStatus.SC_PROCESS_WHITE_IMAGE.getValue(), 0, "UNHANDLED::未明确或正确指定水印矩形坐标，且服务器无法识别文字水印坐标", rectangles, null);
        }

        if (((Boolean) requestParams.getOcrOnly())) {
            return new Data(ResponseStatus.SC_PROCESS_ONLY_OCR.getValue(), null, "SUCCESS::处理成功", rectangles, null);
        }

        InputStream finalImageInputStream = new ByteArrayInputStream(wrapper.getFinalImageBytes());

        String enumTypeName = wrapper.getSupportUploadImageTypeEnum().name();

        String imageType = "IMAGE_PNG".equals(enumTypeName) ? ".png" : "IMAGE_BMP".equals(enumTypeName) ? ".bmp" : "IMAGE_JPEG".equals(enumTypeName) ? ".jpeg" : null;

        String innerPrev = createName();

        File originImage = new File(ProcessImageServiceApplication.getOriginImagePath(), innerPrev + imageType);
        File targetImage = new File(ProcessImageServiceApplication.getTargetImage(), innerPrev + imageType);

        try (OutputStream outputStream = Files.newOutputStream(originImage.toPath())) {
            FileCopyUtils.copy(finalImageInputStream, outputStream);
        } catch (IOException e) {
            throw new ProcessImageFailedException("图片处理过程中发生异常");
        }

        processStatus = process(originImage, rectangles, targetImage, requestParams.getShowOnly());

        if (processStatus == ResponseStatus.SC_PROCESS_INVALID_OFFSET.getValue()) {
            rectangles = (ArrayList<Point>) requestParams.getOriginRectangles();
            processStatus = process(originImage, rectangles, targetImage, requestParams.getShowOnly());
            if (processStatus != 0) {
                return new Data(ResponseStatus.SC_PROCESS_FAILED.getValue(), 0, "FAILED::服务端异常", null, null);
            }
            processStatus = ResponseStatus.SC_PROCESS_INVALID_OFFSET.getValue();
        }

        if (processStatus == ResponseStatus.SC_PROCESS_FAILED.getValue()) {
            return new Data(ResponseStatus.SC_PROCESS_FAILED.getValue(), 0, "FAILED::服务端异常", null, null);
        }

        return new Data(processStatus, rectangles.size(), "SUCCESS::处理成功", rectangles, info.getServerDomain() + "/targetImages/" + targetImage.getName());
    }

    @SuppressWarnings("unchecked")
    private void ensureParams(RequestParamsWrapper wrapper) {

        RequestParams metaData = wrapper.getRequestParams();

        // -------------------------- Process Rectangles Param -------------------------- //

        if (metaData.getRectangles() == null || !(metaData.getRectangles() instanceof List)) {
            // 为 List 表示指定了 rectangles，为 String 或 null 则未制定

            byte[] finalImageBytes = wrapper.getFinalImageBytes();

            InputStream finalImageInputStream = new ByteArrayInputStream(finalImageBytes);

            TranslationData imageData = translateService.getData(finalImageInputStream);

            // 要去除的水印坐标
            ArrayList<Point> points = new ArrayList<>();

            if (imageData.getData() != null) {

                List<TranslationData.DataInfo.SegmentedData> content = imageData.getData().getContent();

                Object excludeKeywords = metaData.getExcludeKeywords();
                Object markName = metaData.getWatermarkName();

                point: for (TranslationData.DataInfo.SegmentedData segmentedData : content) {

                    String src = segmentedData.getSrc();

                    // ---------------- 在这里判断是否排除或指定水印 ---------------- //


                    if (markName instanceof List) {
                        ArrayList<GenericMark> markNameList = (ArrayList<GenericMark>) markName;
                        float similarity = Float.parseFloat(info.getEditDistanceSimilar().replace("%", ""));
                        for (GenericMark mark : markNameList) {
                            Enum<MarkType> rule = mark.getRule();
                            String value = mark.getValue();
                            if (rule.equals(MarkType.SIMILAR) || rule.equals(MarkType.DEFAULT)) {
                                float similarityRatio = EditDistanceUtil.getSimilarityRatio(value, src);
                                if (!(similarityRatio >= similarity)) {
                                    // 相似
                                    log.info("排除：{}", src);
                                    continue point;
                                }
                            } else if (rule.equals(MarkType.ABSOLUTE)) {
                                if (!value.equals(src)) {
                                    // 绝对一致
                                    log.info("排除：{}", src);
                                    continue point;
                                }
                            }
                            add(segmentedData, points, src);
                        }
                    } else if (excludeKeywords instanceof List) {
                        ArrayList<GenericMark> excludeKeywordList = (ArrayList<GenericMark>) excludeKeywords;
                        float similarity = Float.parseFloat(info.getEditDistanceSimilar().replace("%", ""));
                        for (GenericMark excludeKeyword : excludeKeywordList) {
                            Enum<MarkType> rule = excludeKeyword.getRule();
                            String value = excludeKeyword.getValue();
                            if (rule.equals(MarkType.SIMILAR) || rule.equals(MarkType.DEFAULT)) {
                                float similarityRatio = EditDistanceUtil.getSimilarityRatio(value, src);
                                if (similarityRatio >= similarity) {
                                    // 相似
                                    log.info("排除：{}", src);
                                    continue point;
                                }
                            } else if (rule.equals(MarkType.ABSOLUTE)) {
                                if (value.equals(src)) {
                                    // 绝对相似
                                    log.info("排除：{}", src);
                                    continue point;
                                }
                            }
                            add(segmentedData, points, src);
                        }
                    } else {
                        // 没指定需要去除的水印内容和排除的水印内容
                        add(segmentedData, points, src);
                    }


                }
            }
            metaData.setRectangles(points);

        }

        // ---------------------------- Process Offset Param ---------------------------- //

        if (metaData.getOffset() != null && metaData.getOffset() instanceof Map) {
            // 如果指定了这个参数这里应该为 Map<String, Integer> ，而不是 String

            ArrayList<Point> rectangles = (ArrayList<Point>) metaData.getRectangles();

            // 使用 LinkedHashMap 确保原始索引
            LinkedHashMap<String, Integer> offsetMaps = (LinkedHashMap<String, Integer>) metaData.getOffset();

            ArrayList<String> keys = new ArrayList<>(offsetMaps.keySet());

            ArrayList<Point> deepCopy = new ArrayList<>(rectangles.size());

            for (Point rectangle : rectangles) {

                deepCopy.add(new Point(rectangle.getX(), rectangle.getY(), rectangle.getW(), rectangle.getH(), null, null));

                rectangle.setX(keys.get(0).startsWith("+") ?
                        rectangle.getX() + offsetMaps.get(keys.get(0)) :
                        rectangle.getX() - offsetMaps.get(keys.get(0)));

                rectangle.setY(keys.get(1).startsWith("-") ?
                        rectangle.getY() - offsetMaps.get(keys.get(1)) :
                        rectangle.getY() + offsetMaps.get(keys.get(1)));

                rectangle.setW(keys.get(2).startsWith("+") ?
                        rectangle.getW() + offsetMaps.get(keys.get(2)) :
                        rectangle.getW() - offsetMaps.get(keys.get(2)));

                rectangle.setH(keys.get(3).startsWith("-") ?
                        rectangle.getH() - offsetMaps.get(keys.get(3)) :
                        rectangle.getH() + offsetMaps.get(keys.get(3)));

            }
            metaData.setOriginRectangles(deepCopy);
        }

        wrapper.setRequestParams(metaData);
    }

    private int process(File originImage, ArrayList<Point> rectangles, File targetImage, Object showOnly) {

        StringBuilder command = new StringBuilder();

        int index = 0;

        command.append("/usr/local/ffmpeg ").append("-i ").append(originImage).append(" -strict ").append("-3 ").append("-vf ").append("\" ");
        for (Point point : rectangles) {
            Integer x = point.getX();
            Integer y = point.getY();
            Integer w = point.getW();
            Integer h = point.getH();
            index++;
            command.append("delogo=").append("x=").append(x).append(":").append("y=").append(y).append(":").append("w=").append(w).append(":").append("h=").append(h);

            command.append(((Boolean) showOnly) ? ":show=1" : ":show=0");

            if (index != rectangles.size()) {
                command.append(",");
            }
        }

        command.append(" \" ").append(targetImage);

        log.info("将执行：" + command);

        try (
                ProcessBuilderWrapper processBuilderWrapper = new ProcessBuilderWrapper(new ProcessBuilder("/bin/sh", "-c", command.toString()).redirectErrorStream(true).start());
                InputStreamReader reader = new InputStreamReader(processBuilderWrapper.setWaitFor(5, TimeUnit.SECONDS).getProcessInputStream());
                BufferedReader br = new BufferedReader(reader)
        ) {

            // 及时将输入流消费掉避免阻塞
            log.info("处理开始流 --->");
            String line;

            while ((line = br.readLine()) != null) {

                log.info(line);

                if (line.contains("Logo area is outside of the frame.")) {
                    return ResponseStatus.SC_PROCESS_INVALID_OFFSET.getValue();
                }

                if (line.contains("Conversion failed!")) {
                    return ResponseStatus.SC_PROCESS_FAILED.getValue();
                }
            }

            log.info("处理结束流 <---");

        } catch (Exception e) {
            // AOP AfterThrowing --> 及时撤回扣除次数
            e.printStackTrace();
            // throw new ProcessImageFailedException("图片处理异常，本次请求不扣除使用次数");
            return ResponseStatus.SC_PROCESS_FAILED.getValue();
        } finally {
            long expireTime = System.currentTimeMillis() + info.getCacheConfig().getTimeUnit().toMillis(info.getCacheConfig().getExpire());
            cacheTaskMap.put(expireTime, targetImage);
        }

        if (!targetImage.exists()) {
            log.warn("处理完成但没有找到目标文件");
            return ResponseStatus.SC_PROCESS_FAILED.getValue();
        }

        return ResponseStatus.SC_PROCESS_SUCCESS.getValue();

    }

    @Override
    public GenericResponse simpleDemoProcess(MultipartFile file, LinkedList<String> rect) {

        try {

            int x = Integer.parseInt(rect.get(0));
            int y = Integer.parseInt(rect.get(1));
            int w = Integer.parseInt(rect.get(2));
            int h = Integer.parseInt(rect.get(3));

            ArrayList<Point> points = new ArrayList<>(Collections.singletonList(new Point(x, y, w, h, null, null)));

            byte[] bytes = FileCopyUtils.copyToByteArray(file.getInputStream());
            SupportUploadImageType supportUploadImageType = FileUtil.getSupportUploadImageType(bytes);

            if (supportUploadImageType == null) {
                throw new ImageTypeNotSupportedException("上传文件类型不受支持");
            }

            String enumTypeName = supportUploadImageType.name();
            String imageType = "IMAGE_PNG".equals(enumTypeName) ? ".png" : "IMAGE_BMP".equals(enumTypeName) ? ".bmp" : "IMAGE_JPEG".equals(enumTypeName) ? ".jpeg" : null;

            File originPath = new File(ProcessImageServiceApplication.getOriginImagePath(), createName() + imageType);
            File targetPath = new File(ProcessImageServiceApplication.getTargetImage(), createName() + imageType);

            FileCopyUtils.copy(bytes, originPath);

            int statusCode = this.process(originPath, points, targetPath, true);

            boolean processStatus = ResponseStatus.SC_PROCESS_SUCCESS.getValue() == statusCode && targetPath.exists();

            return new GenericResponse(
                    ResponseStatus.SC_OK.getValue(),
                    "SUCCESS::调用成功",
                    "json",
                    null,
                    null,
                    null,
                    null,
                    new Data(
                            statusCode,
                            null,
                            processStatus ? "SUCCESS::处理成功" : "FAILED::处理失败",
                            points,
                            processStatus ? info.getServerDomain() + "/targetImages/" + targetPath.getName() : null),
                    null
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new ArgumentsOverwriteException("提供的坐标不是一个有效的 Integer 类型");
        }
    }

    private synchronized String createName() {

        String uid = UUID.randomUUID().toString();

        while (uid.equals(prev)) {
            prev = UUID.randomUUID().toString();;
        }
        prev = uid;

        return uid;
    }

    private void add(TranslationData.DataInfo.SegmentedData segmentedData, ArrayList<Point> points, String src){
        // --------------------------------------------------------- //

        String[] rect = segmentedData.getRect().split(" ");
        points.add(new Point(Integer.parseInt(

                rect[0]),
                Integer.parseInt(rect[1]),
                Integer.parseInt(rect[2]),
                Integer.parseInt(rect[3]),
                segmentedData.getLineCount(),
                src
        ));
    }
}