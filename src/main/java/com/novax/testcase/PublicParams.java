package com.novax.testcase;

import com.novax.util.PropertyUtil;
import com.novax.util.StringToMap;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublicParams {
    static Logger logger = Logger.getLogger(PublicParams.class);

    public String baseUrl = "http://" + PropertyUtil.getServerUrl() + ":" + PropertyUtil.getServerPort();
    public static List<Long> traders = new ArrayList<>();
    public static List<Long> tradersOfCross = new ArrayList<>();
    public static List<Long> tradersOfIsolated = new ArrayList<>();
    public static List<Long> tradersOfSplit = new ArrayList<>();
    public String headers = "{language=en,Content-type=application/json}";
    public HashMap<String, String> reqHeaders = StringToMap.stringToMap(headers);

    @BeforeMethod(alwaysRun = true)
    public void setLogContext(Method method, Object[] testData) {
        String caseID;
        if (testData != null && testData.length > 2) {
            // 数据驱动用例：tcNum(index 0) + tcName(index 1)+ tcDescp(index 2)
            String tcNum = String.valueOf(testData[0]);
            String tcName = String.valueOf(testData[1]);
            String tcDescp = String.valueOf(testData[2]);
            caseID = tcNum + "_" + tcName + "_" + tcDescp;
        } else {
            // 非数据驱动用例（如 traderReady）：方法名 + 线程ID
            caseID = method.getName() + "_" + Thread.currentThread().getId();
        }
        MDC.put("caseID", caseID);
    }

    @AfterMethod(alwaysRun = true)
    public void clearLogContext() {
        MDC.remove("caseID");
    }
}
