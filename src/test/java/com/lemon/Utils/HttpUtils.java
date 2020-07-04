package com.lemon.Utils;

import com.alibaba.fastjson.JSONObject;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Step;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static Logger logger = Logger.getLogger(HttpUtils.class);
    @Step("CALL方法")
    public static HttpResponse call(CaseInfo caseInfo,Map<String,String>headers) {

        String method = caseInfo.getMethod();
        String contentType = caseInfo.getContentType();
        String url = caseInfo.getUrl();
        //
        try {
            if ("post".equalsIgnoreCase(method)) {
                //参数类型是json
                if ("json".equalsIgnoreCase(contentType)) {
                    return HttpUtils.jsonPost(caseInfo.getUrl(), caseInfo.getParams(),headers);
                    //参数类型是form
                } else if ("form".equalsIgnoreCase(contentType)) {
                    String params = json2KeyValue(caseInfo.getParams());
                    return HttpUtils.formPost(caseInfo.getUrl(), params);
                }else{
                    System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url);
                }
                //请求方式是get
            } else if ("get".equalsIgnoreCase(method)) {
                return HttpUtils.get(caseInfo.getUrl(),headers);
                //请求方式是patch
            } else if ("patch".equalsIgnoreCase(method)) {
                return HttpUtils.patch(caseInfo.getUrl(), caseInfo.getParams(),headers);
            }else{
                System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //json 转成from参数
    public static String json2KeyValue(String jsonStr) {
        Map<String, String> map = JSONObject.parseObject(jsonStr, Map.class);
        Set<String> keySet = map.keySet();
        String result = "";
        for (String key : keySet) {
            String value = map.get(key);
            result += key + "=" + value + "&";
        }
        result = result.substring(0, result.length() - 1);
        System.out.println("result");
        return result;

    }


    //get方法
    public static HttpResponse get(String url,Map<String,String>headers) throws Exception {
        //1、创建get请求并写入接口地址
        HttpGet get = new HttpGet(url);
        //2、在get请求上添加请求头
        addHeaders(headers, get);
        //get.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
        //3、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //4、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(get);
        //5、获取响应头、响应体、响应状态码
        //5.1、获取响应头
        return response;

    }


    //post方法json
    public static HttpResponse jsonPost(String url, String params,Map<String,String>headers) throws Exception {
        //1、创建POST请求并写入接口地址
        HttpPost post = new HttpPost(url);
        //2、在POST请求上添加请求头
        addHeaders(headers, post);
        //post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
        //post.addHeader("Content-Type", "application/json");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        post.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(post);
        //6、获取响应头、响应体、响应状态码
        return response;

    }

    //post方法form
    public static HttpResponse formPost(String url, String params) throws Exception {
        //1、创建POST请求并写入接口地址
        HttpPost post = new HttpPost(url);
        //2、在POST请求上添加请求头
        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        post.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(post);
        //6、获取响应头、响应体、响应状态码
        return response;

    }



    //patch 方法
    public static HttpResponse patch(String url, String params,Map<String,String>headers) throws Exception {
        //1.创建patch请求并写入地址
        HttpPatch patch = new HttpPatch(url);
        //2、在patch请求上添加请求头
        addHeaders(headers, patch);
//        patch.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        patch.addHeader("Content-Type", "application/json");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        patch.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(patch);
        return response;
    }

    //提取相同的代码进行封装

    public static String printResponse(HttpResponse response) {
        //6.1、获取响应头
        Header[] allHeaders = response.getAllHeaders();
        //Header[] headers = response.getHeaders("Content-Type");
        logger.info(Arrays.toString(allHeaders));
        //5.2、获取响应体
        HttpEntity entity = response.getEntity();
        String body = null;
        try {
            body = EntityUtils.toString(entity);

            logger.info(body);
            //5.3、响应状态码
            //链式编程 调用方法之后继续调用方法
            int statusCode = response.getStatusLine().getStatusCode();
            //上面的一句等于下面两句
            logger.info(statusCode);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return"";
    }

    //添加请求头方法
    private static void addHeaders(Map<String, String> headers, HttpRequest request) {
        Set<String> keySet = headers.keySet();
        for (String name:keySet) {
            String value =headers.get(name);
            request.addHeader(name,value);
        }
    }
}
