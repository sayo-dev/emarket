package org.example.e_market.dto;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    String getMonth();
    BigDecimal getRevenue();
    BigDecimal getCommission();
    BigDecimal getPayout();
}
