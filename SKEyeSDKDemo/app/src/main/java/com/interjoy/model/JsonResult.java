package com.interjoy.model;

/**
 * 基础model类
 *
 * @author wangcan  Interjoy
 */
public class JsonResult {
    private int error_code = 0;// 错误码
    private String error_msg = "";// 错误消息

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

}
