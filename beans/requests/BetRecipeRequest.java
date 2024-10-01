/**
 * 
 */
package com.carrus.statsca.beans.requests;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author h.ramananjara
 * This class represents a request of betrecipes for a given race
 */
@Schema(name = "BetRecipeRequest", description = "Bet recipe request")
public class BetRecipeRequest {
	
	@Schema(description = "race primary key", nullable = true, example = "1234")
	public Long racePk;
	
	@Schema(description = "race stakes last update", nullable = true)
	public ZonedDateTime updateTime;
	
	public BetRecipeRequest() {
		super();
	}

	public BetRecipeRequest(Long pk) {
		super();
		this.racePk = pk;
	}

	public Long getRacePk() {
		return racePk;
	}

	public void setRacePk(Long racePk) {
		this.racePk = racePk;
	}

	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
	
	

}
