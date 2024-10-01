package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.RegulatoryBet;

@AutoFillFrom(value = RegulatoryBet.class, fillerPath = "remarkable")
public class RemarkableDTO {
	
	/**clé primaire de l'objet dans la base club*/
	private Long pk;
	


	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	public RemarkableDTO(RegulatoryBet bet) {
		super();
		this.code = bet.getCode();
		this.name = bet.getName();
		this.longName = bet.getLongName();
		this.remarkable = bet.isRemarkable();
		this.jackpot = bet.isJackpot();
	}

	private int code;
	
	private String name;
	
	private String longName;
	
	private boolean remarkable;
	
	private Boolean jackpot;

	public int getCode() {
		return code;
	}

	@AutoCopy("getCode")
	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	@AutoCopy("getName")
	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	@AutoCopy("getLongName()")
	public void setLongName(String longName) {
		this.longName = longName;
	}

	public boolean isRemarkable() {
		return remarkable;
	}

	@AutoCopy("isRemarkable()")
	public void setRemarkable(boolean remarkable) {
		this.remarkable = remarkable;
	}

	public Boolean isJackpot() {
		return (jackpot != null && jackpot);
	}

	@AutoCopy("isJackpot")
	public void setJackpot(Boolean jackpot) {
		this.jackpot = jackpot;
	}
}
