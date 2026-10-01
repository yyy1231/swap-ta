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
public class SwapFrontFundsRateEntity {
    private Long id;

    private String symbol;

    private BigDecimal fundsRate;

    private Integer fundingTime;

    private Date createTime;
}