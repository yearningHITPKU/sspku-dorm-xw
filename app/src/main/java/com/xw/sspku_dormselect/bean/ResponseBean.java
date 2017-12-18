package com.xw.sspku_dormselect.bean;

/**
 * Created by xw on 2017/12/17.
 */

public class ResponseBean<T> {

    private int errcode;

    private T data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "errcode=" + errcode +
                ", data=" + data +
                '}';
    }
}
