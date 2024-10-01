package com.carrus.statsca.dto;

import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.GrandPrize;

public class RemarkableRaceInfosDTO {
    @JsonInclude(Include.NON_NULL)
    private GrandPrizeDTO grandPrix;

    
    private SortedSet<RemarkableRaceDateInfoDTO> dates;

    public RemarkableRaceInfosDTO(GrandPrize gp) {
        this.dates = new TreeSet<>();
        this.grandPrix = new GrandPrizeDTO(gp);
    }

    public boolean addDate(RemarkableRaceDateInfoDTO date) {
        return dates.add(date);
    }

    public GrandPrizeDTO getGrandPrix() {
        return grandPrix;
    }

    public void setGrandPrix(GrandPrizeDTO grandPrix) {
        this.grandPrix = grandPrix;
    }

    public SortedSet<RemarkableRaceDateInfoDTO> getDates() {
        return dates;
    }

    public void setDates(SortedSet<RemarkableRaceDateInfoDTO> dates) {
        this.dates = dates;
    }
}
