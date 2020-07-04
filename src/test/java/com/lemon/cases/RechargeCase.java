package com.lemon.cases;

import com.alibaba.fastjson.JSONPath;
import com.lemon.Utils.AuthenticationUtils;
import com.lemon.Utils.ExcelUtils;
import com.lemon.Utils.HttpUtils;
import com.lemon.Utils.SQLUtils;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

public class RechargeCase extends BaseCase{
    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        //  1、参数化替换
        parameter(caseInfo);
        //	2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        Object beforeSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	3.1获取带token的请求头
        //从VARS中取token memeber_id
        Map<String, String> headers = AuthenticationUtils.getTokenHeader();
        //	3.2调用接口
        HttpResponse response = HttpUtils.call(caseInfo,headers);
        //添加接口响应回写内容
        String body = HttpUtils.printResponse(response);
        //	4、断言响应结果
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        //	5、添加接口响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getCaseId(),Constants.RESPONSE_WRTIE_BACK_CELLNUM,body);
        //	6、数据库后置查询结果
        Object afterSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	7、数据库断言
        boolean assertSqlFlag = sqlAssert(caseInfo, beforeSqlResult, afterSqlResult);
        //	8、添加断言回写内容
        String assertResult = assertResponseFlag && assertSqlFlag ? "passed" : "failed";
        addWriteBackData(sheetIndex,caseInfo.getCaseId(), Constants.ASSERT_WRTIE_BACK_CELLNUM,assertResult);
        //	9、添加日志
        //	10、报表断言
        Assert.assertEquals(assertResult,"passed");
    }



    private boolean sqlAssert(CaseInfo caseInfo, Object beforeSqlResult, Object afterSqlResult) {
        boolean flag=false;
        if(StringUtils.isNotBlank(caseInfo.getSql())) {
            if (beforeSqlResult == null || afterSqlResult == null) {
                System.out.println("数据库断言失败");
            } else {
                BigDecimal b1 = (BigDecimal) beforeSqlResult;
                BigDecimal b2 = (BigDecimal) afterSqlResult;
                //充值后 - 充值前得到的结果  b2 - b1
                BigDecimal result1 = b2.subtract(b1);
                //jsonpath获取参数
                Object obj = JSONPath.read(caseInfo.getParams(),"$.amount");
                //参数amount
                BigDecimal result2 = new BigDecimal(obj.toString());
                //结果 == 参数amount
                System.out.println(b1);
                System.out.println(b2);
                System.out.println(result1);
                System.out.println(result2);
                if(result1.compareTo(result2) == 0) {
                    System.out.println("数据库断言成功");
                    flag=true;
                }else {
                    System.out.println("数据库断言失败");
                }
            }
        }else {
            System.out.println("sql为空，不需要数据库断言");
        }
        return flag;

    }
    //	8、添加断言回写内容
    //	9、添加日志
    //	10、报表断言

    @DataProvider
    public Object[] datas(){
        Object[] datas = ExcelUtils.getDatas(sheetIndex,1,CaseInfo.class);
        return datas;
    }
}
