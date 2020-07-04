package com.lemon.cases;
import com.lemon.Utils.AuthenticationUtils;
import com.lemon.Utils.ExcelUtils;
import com.lemon.Utils.HttpUtils;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Description;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginCase extends BaseCase{
    @Test(dataProvider = "datas",description  = "登录属性测试")
    @Description("注解")
    public void test(CaseInfo caseInfo) throws Exception {
        //  1、参数化替换
        parameter(caseInfo);
        //	2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        //	3、调用接口
        HttpResponse response = HttpUtils.call(caseInfo,Constants.HEADERS);
        //添加接口响应回写内容
        String body = HttpUtils.printResponse(response);
        //3.1从响应体获取token
        //使用jsonpath获取$.data.token_info.token
        AuthenticationUtils.json2Vars(body,"$.data.token_info.token","${token}");
        //3.2使用jsonpath获取$.member_id
        AuthenticationUtils.json2Vars(body,"$.data.id","${member_id}");
        //	4、断言响应结果
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        //	5、添加接口响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getCaseId(),Constants.RESPONSE_WRTIE_BACK_CELLNUM,body);
        //	6、数据库后置查询结果
        //	7、据库断言
        //	8、添加断言回写内容
        String assertResult = assertResponseFlag ? "passed" : "failed";
        addWriteBackData(sheetIndex,caseInfo.getCaseId(), Constants.ASSERT_WRTIE_BACK_CELLNUM,assertResult);
        //	9、添加日志
        //	10、报表断言
        Assert.assertEquals(assertResult,"passed");
    }

    @DataProvider
    public Object[] datas(){
        Object[] datas = ExcelUtils.getDatas(sheetIndex,1,CaseInfo.class);
        return datas;
    }

}
