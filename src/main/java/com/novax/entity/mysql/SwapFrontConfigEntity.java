package com.novax.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwapFrontConfigEntity {
    private Long id;

    private Long tenantId;

    private String symbol;

    private String baseCurrency;

    private String quoteCurrency;

    private String settlementCurrency;

    private BigDecimal contractSize;

    private BigDecimal priceTick;

    private Integer sizeTick;

    private Long minVolume;

    private Long maxVolume;

    private Integer minLeverage;

    private Integer maxLeverage;

    private BigDecimal marketRatioBuy;

    private BigDecimal marketRatioSell;

    private BigDecimal limitRatio;

    private BigDecimal ratio;

    private String imgs;

    private Long tolerateVolume;

    private Long buyVolume;

    private Long sellVolume;

    private Integer fundingTime;

    private Integer sort;

    private Date openTime;

    private Integer isOpen;

    private Integer tradeType;

    private Byte status;

    private Integer isDelisting;

    private Date delistingTime;

    private Date createTime;

    private Date updateTime;

    private CurrencyEntity currencyEntity;

}