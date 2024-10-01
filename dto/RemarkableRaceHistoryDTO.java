package com.carrus.statsca.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.carrus.statsca.bethistory.RemarkableRaceHistory;
import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

@AutoFillFrom(value = RemarkableRaceHistory.class, fillerPath = "remarkableRaceHistory")
public class RemarkableRaceHistoryDTO implements Serializable {
	private static final long serialVersionUID = 6771967344780587039L;
	
	@JsonInclude(Include.NON_NULL)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate raceDate;
	
	@JsonInclude(Include.NON_NULL)
	private List<RemarkableRaceHistoryPeriodDTO> perPeriod;
	
	public RemarkableRaceHistoryDTO(RemarkableRaceHistory history) {
		AutoFillerEngine.autoFill(this, history);
		if (history.getHistoryPerPeriod() != null) {
			perPeriod = new ArrayList<>();
			history.getHistoryPerPeriod().forEach(
					(key, value) -> perPeriod.add(new RemarkableRaceHistoryPeriodDTO(value))
			);
		}
	}

	public LocalDate getRaceDate() {
		return raceDate;
	}

	@AutoCopy("getRaceDate")
	public void setRaceDate(LocalDate raceDate) {
		this.raceDate = raceDate;
	}

	public List<RemarkableRaceHistoryPeriodDTO> getPerPeriod() {
		return perPeriod;
	}

	public void setPerPeriod(List<RemarkableRaceHistoryPeriodDTO> perPeriod) {
		this.perPeriod = perPeriod;
	}
}
