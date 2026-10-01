package com.novax.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionCalculation {
    // 杠杆倍数开始范围
    private Integer minLeverage;
    // 杠杆倍数结束范围
    private Integer maxLeverage;
    // 头寸
    private Long maxVolume;
}
