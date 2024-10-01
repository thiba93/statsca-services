package com.carrus.statsca.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.carrus.statsca.bethistory.RemarkableRaceHistoryPartner;
import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@AutoFillFrom(value = RemarkableRaceHistoryPartner.class, fillerPath = "remarkableRaceHistoryPartner")
public class RemarkableRaceHistoryPartnerDTO implements Serializable {
	private static final long serialVersionUID = 2383839909469098066L;

	@JsonInclude(Include.NON_NULL)
	private int partnerId;
	
	@JsonInclude(Include.NON_NULL)
	private BigDecimal totalStake;
	
	@JsonInclude(Include.NON_NULL)
	private Map<Integer, BigDecimal> perBetType;
	
	public RemarkableRaceHistoryPartnerDTO(RemarkableRaceHistoryPartner history) {
		AutoFillerEngine.autoFill(this, history);
		if (history.getPerBetType() != null) {
			perBetType = new HashMap<>();
			history.getPerBetType().forEach((key, value) -> perBetType.put(key,  value));
		}
	}

	public int getPartnerId() {
		return partnerId;
	}

	@AutoCopy("getPartnerId")
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	public BigDecimal getTotalStake() {
		return totalStake;
	}

	@AutoCopy("getTotalStake")
	public void setTotalStake(BigDecimal totalStake) {
		this.totalStake = totalStake;
	}

	public Map<Integer, BigDecimal> getPerBetType() {
		return perBetType;
	}

	@AutoCopy("getPerBetType")
	public void setPerBetType(Map<Integer, BigDecimal> parBetType) {
		this.perBetType = parBetType;
	}
}
