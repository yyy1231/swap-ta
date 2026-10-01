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
public class CalculatorResults {
    // 收益
    private BigDecimal income;
    // 回报率
    private BigDecimal returnRate;
    // 保证金
    private BigDecimal margin;
}
