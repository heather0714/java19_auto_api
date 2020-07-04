package com.lemon.Utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luojie
 * @date 2020/6/11 - 21:42
 * 柠檬班创新教育极致服务
 *
 * excel 工具类
 */
public class ExcelUtils {
    //批量回写集合
    public static List<WriteBackData> wbdList = new ArrayList<>();

    public static Object[] getDatas(int sheetIndex, int sheetNum, Class clazz){
        try {
            List list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
            Object[] datas = list.toArray();
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //读取excel文件
    //封装时read（）参数设为动态，可以灵活读取sheet
    //封装 Class clazz是为了灵活接受多个CaseInfo

    public static List read(int sheetIndex,int sheetNum,Class clazz) throws Exception {
        //1、加载excel
        //避免无法读取到文件
        //   String path = ExcelUtils.class.getClassLoader().getResource("./cases_v3.xlsx").getPath();
        FileInputStream fis = new FileInputStream(Constants.EXCEL_PATH);
        //2.easypoi
        //导入参数
        ImportParams params = new ImportParams();
        //从第n个sheet读取
        params.setStartSheetIndex(sheetIndex);
        //每次读取n个sheet
        params.setSheetNum(sheetNum);

        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(fis, clazz, params);
        for (CaseInfo caseInfo : caseInfoList) {
            System.out.println(caseInfo);
        }
        return  caseInfoList;
    }

    //批量回写方法
    public static void batchWrite() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(Constants.EXCEL_PATH);
            //1.2、解析数据必须用poi提供对象
            Workbook excel = WorkbookFactory.create(fis);
            //循环 批量回写集合 wbdList
            for (WriteBackData writeBackData : wbdList) {
                //取出sheetIndex
                int sheetIndex = writeBackData.getSheetIndex();
                //取出行号
                int rownum = writeBackData.getRowNum();
                //取出列号
                int cellnum = writeBackData.getCellNum();
                //取出回写内容
                String content = writeBackData.getContent();
                //2、选择sheet
                Sheet sheet = excel.getSheetAt(sheetIndex);
                //3、读取每一行
                Row row = sheet.getRow(rownum);
                //4、读取每一个单元格 MissingCellPolicy 当cell为null时，返回一个空的cell对象
                Cell cell = row.getCell(cellnum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                //5、修改
                cell.setCellValue(content);
            }
            fos = new FileOutputStream(Constants.EXCEL_PATH);
            //6、写回去
            excel.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //一定会执行（无论是否发生异常都会执行）
            //释放资源
            //6、关流
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}



