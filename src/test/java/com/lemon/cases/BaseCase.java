package com.lemon.cases;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.Utils.AuthenticationUtils;
import com.lemon.Utils.ExcelUtils;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BaseCase {
    private static Logger logger = Logger.getLogger(BaseCase.class);
    public int sheetIndex;

    @BeforeSuite
    //初始化请求头，提取相同的请求头进行初始化 因为都一样，所以选择在beforesuite前进行，运行一次就可以了
    public void init() throws Exception {
        logger.info("======================init===================");
        Constants.HEADERS.put("X-Lemonban-Media-Type","lemonban.v2");
        Constants.HEADERS.put("Content-Type", "application/json");
        //存入参数的变量
        //创建properties对象
        Properties prop =new Properties();
        //获取配置路径
        String path = BaseCase.class.getClassLoader().getResource("./params.properties").getPath();
        FileInputStream fis = new FileInputStream(path);
        //读取配置文件中的内容并添加到prop中
        prop.load(fis);
        //关流
        fis.close();
        //把prop中所有的内容一次性放入VARS
        AuthenticationUtils.VARS.putAll((Map)prop);
        logger.info("AuthenticationUtils.VARS==========="+AuthenticationUtils.VARS);
    }
    @AfterSuite
    public void finish() {
        //执行批量回写
        logger.info("======================finish===================");
        ExcelUtils.batchWrite();

    }

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    //创建回写对象,添加到批量回写集合中
    public void addWriteBackData(int sheetIndex, int rowNum,int cellNum, String content) {
        WriteBackData wbd =
                new WriteBackData(sheetIndex,rowNum, cellNum,content);
        //添加到回写集合
        ExcelUtils.wbdList.add(wbd);
    }
    /**
     * 接口响应断言
     * @param body                  接口响应字符串
     * @param expectResult          excel中响应期望值
     * @return                      断言结果
     */
    public boolean assertResponse(String body, String expectResult) {
        //json转成map
        Map<String,Object> map = JSONObject.parseObject(expectResult, Map.class);
        Set<String> keySet = map.keySet();
        boolean assertResponseFlag = true;
        for (String expression : keySet) {
            //1、获取期望值
            Object expectValue = map.get(expression);
            //2、通过jsonpath找到实际值
            Object actualValue = JSONPath.read(body,expression);
            //3、比较期望值和实际值
            if(expectValue == null && actualValue != null) {
                assertResponseFlag = false;
                break;
            }
            if(expectValue == null && actualValue == null) {
                continue;
            }
            if(!expectValue.equals(actualValue)) {
                assertResponseFlag = false;
                break;
            }
        }
        System.out.println("响应断言结果：" + assertResponseFlag);
        return assertResponseFlag;
    }

    public void parameter(CaseInfo caseInfo) throws Exception {
        //获取VARS中所有的key
        Set<String> keySet = AuthenticationUtils.VARS.keySet();
        //遍历key取出value
        //key是占位符，value是实际值
        for (String key:keySet) {
            String value = AuthenticationUtils.VARS.get(key).toString();
            //替换sql   replace
            if(StringUtils.isNotBlank(caseInfo.getSql())) {
                String sql = caseInfo.getSql().replace(key, value);
                caseInfo.setSql(sql);
            }
            //替换params   replace
            if(StringUtils.isNotBlank(caseInfo.getParams())) {
                String params = caseInfo.getParams().replace(key, value);
                caseInfo.setParams(params);
            }
            //替换expectResult   replace
            if(StringUtils.isNotBlank(caseInfo.getExpectResult())) {
                String expectResult = caseInfo.getExpectResult().replace(key, value);
                caseInfo.setExpectResult(expectResult);

            }
        }
    }
}


