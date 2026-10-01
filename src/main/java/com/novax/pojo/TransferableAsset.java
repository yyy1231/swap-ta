package com.novax.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合约可划出资产
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferableAsset {
    // 交易市场
    private String symbol;
    // 账户余额币种
    private String currency;
    // "账户类型1:全逐仓2:平台币3:分仓"
    private Integer type;
}
