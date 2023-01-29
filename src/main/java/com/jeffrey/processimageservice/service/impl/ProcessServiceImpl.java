package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.ProcessImageServiceApplication;
import com.jeffrey.processimageservice.aop.annotation.CacheProcessedResponse;
import com.jeffrey.processimageservice.aop.annotation.UpdateAccountUsed;
import com.jeffrey.processimageservice.entities.*;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.Data;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.response.Point;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.entities.translate.TranslationData;
import com.jeffrey.processimageservice.exception.exception.ProcessImageFailedException;

import com.jeffrey.processimageservice.service.ProcessService;
import com.jeffrey.processimageservice.service.TranslateService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

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
    private static long prev;
    private final Info info;

    @Autowired
    public ProcessServiceImpl(LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal, TranslateService translateService, Info info) {
        this.asyncPendingTasksItemLinkedBlockingQueue = asyncPendingTasksItemLinkedBlockingQueue;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
        this.translateService = translateService;
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

        int processStatus = 0;

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

        String imageType = "IMAGE_PNG".equals(enumTypeName) ? ".png" : "IMAGE_BMP".equals(enumTypeName) ? ".bmp" : enumTypeName.equals("IMAGE_JPEG") ? ".jpeg" : null;

        long innerPrev = getDiffTimeStamp();

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

        return new Data(processStatus, rectangles.size(), "SUCCESS::调用成功", rectangles, info.getServerDomain() + "/targetImages/" + targetImage.getName());
    }

    @SuppressWarnings("unchecked")
    private void ensureParams(RequestParamsWrapper wrapper) {

        RequestParams metaData = wrapper.getRequestParams();

        // -------------------------- Process Rectangles Param -------------------------- //

        if (metaData.getRectangles() instanceof String || metaData.getRectangles() == null) {
            // 如果指定了这个参数这里应该为 List 而不是 String

            byte[] finalImageBytes = wrapper.getFinalImageBytes();

            InputStream finalImageInputStream = new ByteArrayInputStream(finalImageBytes);

            TranslationData imageData = translateService.getData(finalImageInputStream);

            ArrayList<Point> points = new ArrayList<>();

            if (imageData.getData() != null) {

                List<TranslationData.DataInfo.SegmentedData> content = imageData.getData().getContent();

                for (TranslationData.DataInfo.SegmentedData segmentedData : content) {

                    String src = segmentedData.getSrc();

                    // ---------------- 在这里判断是否排除或指定水印 ---------------- //



                    // --------------------------------------------------------- //

                    String[] rect = segmentedData.getRect().split(" ");

                    Point point = new Point(Integer.parseInt(

                            rect[0]),
                            Integer.parseInt(rect[1]),
                            Integer.parseInt(rect[2]),
                            Integer.parseInt(rect[3]),
                            segmentedData.getLineCount(),
                            src
                    );

                    points.add(point);
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

                rectangle.setX(keys.get(0).startsWith("+") ? rectangle.getX() + offsetMaps.get(keys.get(0)) : rectangle.getX() - offsetMaps.get(keys.get(0)));

                rectangle.setY(keys.get(1).startsWith("-") ? rectangle.getY() - offsetMaps.get(keys.get(1)) : rectangle.getY() + offsetMaps.get(keys.get(1)));

                rectangle.setW(keys.get(2).startsWith("+") ? rectangle.getW() + offsetMaps.get(keys.get(2)) : rectangle.getW() - offsetMaps.get(keys.get(2)));

                rectangle.setH(keys.get(3).startsWith("-") ? rectangle.getH() - offsetMaps.get(keys.get(3)) : rectangle.getH() + offsetMaps.get(keys.get(3)));

            }
            metaData.setOriginRectangles(deepCopy);
        }

        wrapper.setRequestParams(metaData);
    }

    private synchronized long getDiffTimeStamp() {
        long l = System.currentTimeMillis();
        while (l == prev) {
            l = System.currentTimeMillis();
        }
        prev = l;
        return l;
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

            if (index != rectangles.size()) command.append(",");


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

        }

        return 0;
    }
}