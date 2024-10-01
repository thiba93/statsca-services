package com.carrus.statsca.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class RemarkableRaceDateInfoDTO implements Comparable<RemarkableRaceDateInfoDTO> {
    @JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonInclude(Include.NON_NULL)
    private LocalDate date;

    @JsonInclude(Include.NON_NULL)
    private Integer raceNumber;

    public RemarkableRaceDateInfoDTO(LocalDate date, Integer raceNumber) {
        this.date = date;
        this.raceNumber = raceNumber;
    }
    
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Integer getRaceNumber() {
        return raceNumber;
    }
    public void setRaceNumber(Integer raceNumber) {
        this.raceNumber = raceNumber;
    }

    @Override
    public int compareTo(RemarkableRaceDateInfoDTO o) {
        return date.isBefore(o.date) ? -1 : 1;
    }
}
