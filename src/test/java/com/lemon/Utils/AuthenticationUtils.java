package com.lemon.Utils;
import com.alibaba.fastjson.JSONPath;
import com.lemon.constants.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationUtils {

    public static Map<String, Object> VARS= new HashMap<>();

    public static void json2Vars(String json, String expression, String key) {
        if (StringUtils.isNotBlank(json)) {
            Object obj = JSONPath.read(json, expression);
            System.out.println(key + ":" + obj);
            if (obj != null) {
                AuthenticationUtils.VARS.put(key, obj);
            }
        }
    }

    /**
     *  获取带token的请求头Map集合
     * @return
     */
    public static Map<String, String> getTokenHeader() {
        Object token = AuthenticationUtils.VARS.get("${token}");
        System.out.println("Recharge token:"+token);
        //添加到请求头
        //改造call方法支持传递请求体
        Map<String, String> headers= new HashMap<>();
        headers.put("Authorization", "Bearer "+token);
        headers.putAll(Constants.HEADERS);
        return headers;
    }
}
