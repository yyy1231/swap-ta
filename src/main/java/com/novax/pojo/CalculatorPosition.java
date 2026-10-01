package com.novax.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculatorPosition {
    // 开仓价格
    private BigDecimal openPrice;
    // 开仓数量
    private Long openVolume;
}
