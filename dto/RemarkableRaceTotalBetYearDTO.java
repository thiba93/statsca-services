package com.carrus.statsca.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class RemarkableRaceTotalBetYearDTO {
    @JsonInclude(Include.NON_NULL)
    private int year;

    @JsonInclude(Include.NON_NULL)
    private BigDecimal stake;

    public RemarkableRaceTotalBetYearDTO(Integer year, BigDecimal stake) {
        this.year = year;
        this.stake = stake;
    }

    public BigDecimal getStake() {
        return stake;
    }

    public void setStake(BigDecimal stake) {
        this.stake = stake;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
