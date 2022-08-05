package com.bei.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用响应类
 * */
@Data
public class CommonResult<T> {
    private static final Integer SUCCESS_CODE = 1;
    private static final Integer FAILED_CODE = 0;
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> CommonResult<T> success(T object) {
        CommonResult<T> r = new CommonResult<T>();
        r.data = object;
        r.code = SUCCESS_CODE;
        return r;
    }

    public static <T> CommonResult<T> error(String msg) {
        CommonResult r = new CommonResult();
        r.msg = msg;
        r.code = FAILED_CODE;
        return r;
    }

    public CommonResult<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
