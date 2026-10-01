package com.novax.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SwapFrontOrderPlanEntity {
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long orderId;

    private Long stopOrderId;

    private String symbol;

    private Integer triggerType;

    private BigDecimal triggerPrice;

    private BigDecimal orderPrice;

    private Integer orderPriceType;

    private Long volume;

    private Integer tradeType;

    private Integer mode;

    private Integer leverage;

    private String failReason;

    private Byte source;

    private Long sourceId;

    private Integer status;

    private Date createTime;

    private Date triggerTime;
}