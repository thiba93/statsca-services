package com.carrus.statsca.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class RemarkableRaceTotalBetDTO {
    @JsonInclude(Include.NON_NULL)
    private Long gpId;

    @JsonInclude(Include.NON_NULL)
    private List<RemarkableRaceTotalBetYearDTO> stakes;

    public RemarkableRaceTotalBetDTO(Long gpId, Map<Integer, BigDecimal> stakes) {
        this.gpId = gpId;
        this.stakes = stakes.entrySet().stream().map(elem -> new RemarkableRaceTotalBetYearDTO(elem.getKey(), elem.getValue())).toList();
    }

    public List<RemarkableRaceTotalBetYearDTO> getStakes() {
        return stakes;
    }

    public void setStakes(List<RemarkableRaceTotalBetYearDTO> stakes) {
        this.stakes = stakes;
    }

    public Long getGpId() {
        return gpId;
    }

    public void setGpId(Long gpId) {
        this.gpId = gpId;
    }
}
