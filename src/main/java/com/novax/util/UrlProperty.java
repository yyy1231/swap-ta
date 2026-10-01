package com.novax.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UrlProperty {
    private static final Properties properties = new Properties();

    static {
        try (InputStream resourceAsStream = PropertyUtil.class.getResourceAsStream("/url.properties")) {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOrder() {
        return properties.getProperty("order");
    }

    public static String getRepealOrder() {
        return properties.getProperty("order.repeal");
    }

    public static String getRepeaAlllOrders() {
        return properties.getProperty("order.repealAll");
    }

    public static String getQueryOrder() {
        return properties.getProperty("order.query");
    }

    public static String getStopProfitLossDetail() {
        return properties.getProperty("stopProfitLoss.detail");
    }

    public static String getPlanOrder() {
        return properties.getProperty("planOrder");
    }

    public static String getRepealPlanOrder() {
        return properties.getProperty("planOrder.repeal");
    }

    public static String getRepealAllPlanOrder() {
        return properties.getProperty("planOrder.repealAll");
    }

    public static String getQueryPlanOrder() {
        return properties.getProperty("planOrder.query");
    }

    public static String getMargin() {
        return properties.getProperty("margin");
    }

    public static String getPosition() {
        return properties.getProperty("position");
    }

    public static String getPositionAll() {
        return properties.getProperty("positionAll");
    }

    public static String getSharePage() {
        return properties.getProperty("sharePage");
    }

    public static String getOrderRecord() {
        return properties.getProperty("order.record");
    }

    public static String getOrderDetail() {
        return properties.getProperty("order.detail");
    }
    public static String getOrderDetail2() {
        return properties.getProperty("order.detail2");
    }

    public static String getSwapCount() {
        return properties.getProperty("swap.count");
    }

    public static String getFinanceRecord() {
        return properties.getProperty("finance.record");
    }

    public static String getTransferBalance() {
        return properties.getProperty("transfer.balance");
    }

    public static String getAssets() {
        return properties.getProperty("assets");
    }

    public static String getAssetList() {
        return properties.getProperty("assetList");
    }

    public static String getTradableAssets() {
        return properties.getProperty("tradableAssets");
    }

    public static String getFundsRatePage() {
        return properties.getProperty("fundsRatePage");
    }

    public static String getFundsRateChart() {
        return properties.getProperty("fundsRateChart");
    }

    public static String getMode() {
        return properties.getProperty("mode");
    }

    public static String getLeverage() {
        return properties.getProperty("leverage");
    }

    public static String getUserConfig() {
        return properties.getProperty("user.config");
    }

    public static String getSwapConfig() {
        return properties.getProperty("swap.config");
    }

    public static String getSymbolConfig() {
        return properties.getProperty("symbol.config");
    }

    public static String getMaxleverage() {
        return properties.getProperty("maxleverage");
    }

    public static String getStrongPrice() {
        return properties.getProperty("strongPrice");
    }

    public static String getDoubleStrongPrice() {
        return properties.getProperty("doubleStrongPrice");
    }

    public static String getAveragePrice() {
        return properties.getProperty("averagePrice");
    }

    public static String getProfit() {
        return properties.getProperty("profit");
    }

    public static String getClosePrice() {
        return properties.getProperty("closePrice");
    }

    public static String getClosePri() {
        return properties.getProperty("closePri");
    }

    public static String getPositCalc() {
        return properties.getProperty("positCalc");
    }
}
