package com.jeffrey.processimageservice.entities.enums;

public class ResponseObject {

    /**
     * 信息
     */
    private String msg;

    /**
     * 响应码
     */
    private int code;

    /**
     * 数据
     */
    private Object data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // 返回数据
    public static ResponseObject success(Object object) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(object);
        responseObject.setCode(200);
        responseObject.setMsg("操作成功");
        return responseObject;
    }

    // 返回信息
    public static ResponseObject success(String msg) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(true);
        responseObject.setCode(200);
        responseObject.setMsg(msg);
        return responseObject;
    }
    // 直接返回
    public static ResponseObject success() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(true);
        responseObject.setCode(200);
        responseObject.setMsg("操作成功");
        return responseObject;
    }

    // 自定义返回数据和信息
    public static ResponseObject success(Object object, String msg) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(object);
        responseObject.setCode(200);
        responseObject.setMsg(msg);
        return responseObject;
    }

    // 自定义返回数据和内容
    public static ResponseObject fail(Object data, String msg) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(data);
        responseObject.setCode(500);
        responseObject.setMsg(msg);
        return responseObject;
    }

    // 自定义返回信息
    public static ResponseObject fail(String msg) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(false);
        responseObject.setCode(400);
        responseObject.setMsg(msg);
        return responseObject;
    }

    // 直接返回
    public static ResponseObject fail() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(false);
        responseObject.setCode(400);
        responseObject.setMsg("操作失败");
        return responseObject;
    }

    // 自定义返回信息和编码
    public static ResponseObject fail(String msg, int code) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(false);
        responseObject.setCode(code);
        responseObject.setMsg(msg);
        return responseObject;
    }
}
