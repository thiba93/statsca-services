package com.carrus.statsca.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.carrus.statsca.bethistory.RemarkableRaceHistoryPeriod;
import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

@AutoFillFrom(value = RemarkableRaceHistoryPeriod.class, fillerPath = "remarkableRaceHistoryPeriod")
public class RemarkableRaceHistoryPeriodDTO implements Serializable {
	private static final long serialVersionUID = 476904405734282888L;

	@JsonInclude(Include.NON_NULL)
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime periodEndTime;
	
	@JsonInclude(Include.NON_NULL)
	private BigDecimal totalStake;
	
	@JsonInclude(Include.NON_NULL)
	private Map<Integer, RemarkableRaceHistoryPartnerDTO> perPartner;
	
	public RemarkableRaceHistoryPeriodDTO(RemarkableRaceHistoryPeriod history) {
		AutoFillerEngine.autoFill(this, history);
		perPartner = new HashMap<>();
		
		if (history.getPerPartner() != null) {
			history.getPerPartner().forEach((key, value) -> perPartner.put(key, new RemarkableRaceHistoryPartnerDTO(value)));
		}
	}

	public LocalTime getPeriodEndTime() {
		return periodEndTime;
	}

	@AutoCopy("getPeriodEndTime")
	public void setPeriodEndTime(LocalTime periodEndTime) {
		this.periodEndTime = periodEndTime;
	}

	public BigDecimal getTotalStake() {
		return totalStake;
	}
	
	@AutoCopy("getTotalStake")
	public void setTotalStake(BigDecimal totalStake) {
		this.totalStake = totalStake;
	}

	public Map<Integer, RemarkableRaceHistoryPartnerDTO> getPerPartner() {
		return perPartner;
	}

	public void setPerPartner(Map<Integer, RemarkableRaceHistoryPartnerDTO> perPartner) {
		this.perPartner = perPartner;
	}
}
