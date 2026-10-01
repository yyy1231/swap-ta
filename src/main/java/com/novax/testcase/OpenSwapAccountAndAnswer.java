package com.novax.testcase;

import com.novax.entity.mysql.SwapFrontUserConfigEntity;
import com.novax.entity.mysql.UserEntity;
import com.novax.mapper.SwapFrontUserConfigEntityMapper;
import com.novax.mapper.UserEntityMapper;
import com.novax.util.MyBatisUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.IOException;
import java.util.Date;

/**
 * 用户开通合约业务
 * 1、superex_usercenter.`user` 开通合约 答题
 * 2、superex_trade.swap_front_user_config   仓位模式配置 全仓/逐仓
 */
public class OpenSwapAccountAndAnswer {
    static Logger logger = Logger.getLogger(OpenSwapAccountAndAnswer.class);

    // 开通合约、答题
    public static void openSwap(Long userId) {
        MyBatisUtil.executeVoid(UserEntityMapper.class, mapper -> {
            UserEntity user = mapper.selectByPrimaryKey(userId);
            if (user.getIsSwapOpened() == 0 || user.getIsAnswerPassed() == 0) {
                user.setIsSwapOpened(1);
                user.setIsAnswerPassed(1);
                int result = mapper.updateByPrimaryKey(user);
                Assert.assertEquals(result, 1, "user信息更新失败");
            } else {
                logger.info("用户：" + userId + "已开通合约账户");
            }
        });
    }

    // 配置仓位模式 --默认全仓1
    public static void configureMode(Long userId) {
        MyBatisUtil.executeVoid(SwapFrontUserConfigEntityMapper.class, mapper -> {
            SwapFrontUserConfigEntity configEntity = mapper.selectByUserId(userId);
            if (configEntity == null) {
                SwapFrontUserConfigEntity record = SwapFrontUserConfigEntity.builder()
                        .tenantId(1L)
                        .userId(userId)
                        .mode(1)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                int num = mapper.insert(record);
                Assert.assertEquals(num, 1, "swap_front_user_config数据插入失败");
            } else {
                logger.info("用户：" + userId + "已有用户仓位配置");
            }
        });
    }

    // 修改保证金模式
    public static void updateMode(Long userId, int mode) {
        MyBatisUtil.executeVoid(SwapFrontUserConfigEntityMapper.class, mapper -> {
            SwapFrontUserConfigEntity configEntity = mapper.selectByUserId(userId);
            configEntity.setMode(mode);
            int i = mapper.updateByPrimaryKey(configEntity);
            Assert.assertTrue(i != 0, "用户" + userId + "保证金模式修改失败");
        });
    }

    // 是否已开通合约
    public static boolean isSwapOpened(Long userId) {
        UserEntity user = MyBatisUtil.execute(UserEntityMapper.class, m -> m.selectByPrimaryKey(userId));
        if (user.getIsSwapOpened() == 1 && user.getIsAnswerPassed() == 1) {
            logger.info("用户：" + userId + "已开通合约业务");
            return true;
        } else {
            logger.info("用户：" + userId + "未开通合约业务");
            return false;
        }
    }

    // 是否已配置仓位模式mode
    public static boolean isModeConfigured(Long userId) {
        SwapFrontUserConfigEntity configEntity = MyBatisUtil.execute(SwapFrontUserConfigEntityMapper.class, m -> m.selectByUserId(userId));
        if (configEntity == null) {
            logger.info("用户：" + userId + "没有仓位模式配置信息");
            return false;
        } else {
            logger.info("用户：" + userId + "有仓位模式配置信息");
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        updateMode(28670895L,2);
    }

}
