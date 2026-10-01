package com.novax.testcase;

import com.novax.entity.mysql.*;
import com.novax.mapper.*;
import com.novax.pojo.TransferableAsset;
import com.novax.util.DateUtil;
import com.novax.util.MyBatisUtil;
import org.testng.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 合约基础配置
 */
public class SwapConfigInfo {
    // 合约面值
    public static BigDecimal queryContractSize(String symbol) {
        SwapFrontConfigEntity configEntity = MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, m -> m.selectBySymbol(symbol));
        return configEntity.getContractSize();
    }

    // 单笔最大挂单张数
    public static Long queryMaxPendingVolume(String symbol) {
        SwapFrontConfigEntity configEntity = MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, m -> m.selectBySymbol(symbol));
        return configEntity.getMaxVolume();
    }

    // 查用户合约杠杆倍数
    public static SwapFrontUserLeverageEntity queryUserLeverage(Long userId, String symbol) {
        return MyBatisUtil.execute(SwapFrontUserLeverageEntityMapper.class, m -> m.selectByUserAndSymbol(userId, symbol));
    }

    // 设置用户杠杆倍数
    public static void initialUserLeverage(Long userId, String symbol) {
        MyBatisUtil.executeVoid(SwapFrontUserLeverageEntityMapper.class, mapper -> {
            SwapFrontUserLeverageEntity entity = SwapFrontUserLeverageEntity.builder()
                    .tenantId(1L)
                    .symbol(symbol)
                    .userId(userId)
                    .crossedLeverage(10)
                    .fissionLeverage(10)
                    .fixedLeverage(10)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            int insert = mapper.insert(entity);
            Assert.assertEquals(insert, 1, "user leverage initial fail");
        });
    }

    // 查用户合约保证金模式
    public static SwapFrontUserConfigEntity queryMode(Long userId) {
        return MyBatisUtil.execute(SwapFrontUserConfigEntityMapper.class, m -> m.selectByUserId(userId));
    }

    // 查询合约交易市场配置
    public static List<SwapFrontConfigEntity> querySwapConfig() {
        return MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, SwapFrontConfigEntityMapper::selectAll);
    }

    // 查询合约指定币对配置
    public static SwapFrontConfigEntity querySymbolConfig(String symbol) {
        return MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, m -> m.selectBySymbol(symbol));
    }

    // 获取配置了最大杠杆倍数的币对
    public static List<SwapFrontConfigEntity> queryMaxLeverageSymbolList() {
        return MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, SwapFrontConfigEntityMapper::selectMaxLeverageSymbolList);
    }

    // 查询币对维持保证金、速算额、杠杆、持仓量配置 --symbol
    public static List<SwapMarginEntity> querySwapMarginConfig(String symbol) {
        return MyBatisUtil.execute(SwapMarginEntityMapper.class, m -> m.selectBySymbol(symbol));
    }

    // 资金费率
    public static List<SwapFrontFundsRateEntity> queryFundsRate(String symbol) {
        return MyBatisUtil.execute(SwapFrontFundsRateEntityMapper.class, m -> m.selectBySymbol(symbol));
    }

    // 资金费率
    public static List<SwapFrontFundsRateEntity> queryPeriodFundsRate(String symbol, int day) throws ParseException {
        Date nDaysAgo = DateUtil.addDay(new Date(), -7);
        return MyBatisUtil.execute(SwapFrontFundsRateEntityMapper.class, m -> m.selectBySymbolAndTime(symbol, nDaysAgo));
    }

    // 平台可划转资产列表
    public static List<TransferableAsset> queryAssetList() {
        // 启用中的交易对
        List<SwapFrontConfigEntity> list = MyBatisUtil.execute(SwapFrontConfigEntityMapper.class, SwapFrontConfigEntityMapper::selectSwapConfig);

        // usdt 全仓/逐仓资产
        List<TransferableAsset> transferableAssetList = new ArrayList<>();
        TransferableAsset transferableOfUsdt = new TransferableAsset();
        transferableOfUsdt.setSymbol("usdt");
        transferableOfUsdt.setCurrency("usdt");
        transferableOfUsdt.setType(1);
        transferableAssetList.add(transferableOfUsdt);

        // et 平台币
        TransferableAsset transferableOfEt = new TransferableAsset();
        transferableOfEt.setSymbol("et");
        transferableOfEt.setCurrency("et");
        transferableOfEt.setType(2);
        transferableAssetList.add(transferableOfEt);

        // symbol_usdt 分仓资产
        for (SwapFrontConfigEntity item : list) {
            TransferableAsset transferableAsset = new TransferableAsset();
            transferableAsset.setSymbol(item.getSymbol());
            transferableAsset.setCurrency(item.getSettlementCurrency());
            transferableAsset.setType(3);
            transferableAssetList.add(transferableAsset);
        }
        return transferableAssetList;
    }

    public static void main(String[] args) throws ParseException {
        // System.out.println(queryPeriodFundsRate("btc_usdt", 7));
        // initialUserLeverage(28670971L, "btc_usdt");
        // initialUserLeverage(28670971L, "eth_usdt");
        System.out.println(queryAssetList());
    }
}
