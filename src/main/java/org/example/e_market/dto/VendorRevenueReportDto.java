package org.example.e_market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRevenueReportDto {
    private BigDecimal totalRevenue;
    private BigDecimal totalCommissionPaid;
    private BigDecimal totalPayoutReceived;
    private List<MonthlyBreakdownDto> breakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyBreakdownDto {
        private String month;
        private BigDecimal revenue;
        private BigDecimal commission;
        private BigDecimal payout;
    }
}
