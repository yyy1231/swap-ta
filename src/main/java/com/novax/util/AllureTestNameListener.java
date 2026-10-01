package com.novax.util;

import io.qameta.allure.Allure;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

/**
 * 全局 Allure 动态用例名称监听器
 * 实现 IHookable 拦截每个测试方法，从 DataProvider 参数中提取用例标识，
 * 在运行时强行更新 Allure 报告中展示的用例名称。
 */
public class AllureTestNameListener implements IHookable {

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        try {
            Object[] parameters = testResult.getParameters();
            // 只有数据驱动用例（DataProvider 行参数 >= 3）才更新 Allure 用例名
            if (parameters != null && parameters.length >= 3) {
                String caseNum = String.valueOf(parameters[0]);// A列：用例编号
                String caseName = String.valueOf(parameters[1]);// B列：用例名称
                String caseDescription = String.valueOf(parameters[2]);// C列：用例描述
                // 格式：用例名称_用例编号_用例描述
                String dynamicName = String.format("%s_%s_%s", caseName, caseNum, caseDescription);
                Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid ->
                        Allure.getLifecycle().updateTestCase(uuid, tc -> tc.setName(dynamicName))
                );
            }
        } catch (Exception ignored) {
        }
        // 必须调用这一行，让原有的测试用例代码继续往下正常执行
        callBack.runTestMethod(testResult);
    }
}
