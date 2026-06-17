package com.example.web_ban_sach.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MonthlyRevenueResponse {
    private Integer month;
    private Double revenue;

    public MonthlyRevenueResponse(Number month, Number revenue) {
        this.month = month != null ? month.intValue() : 0;
        this.revenue = revenue != null ? revenue.doubleValue() : 0.0;
    }
}
