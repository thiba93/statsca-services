package com.carrus.statsca.event;

import java.io.StringWriter;
import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.pmc.club.event.BetChange;
import com.pmc.club.event.BetChange.BetChangeTypeEnum;

/**
 * Classe de factorisation des evènements de chronologie qui portent
 * sur le pari d'une course particulière.
 * 
 * @author BT - ARTSYS 2018
 * @since 19 janvier 2018
 */
public class BetEvt extends RaceEvtAbstract {
	/** Code du pari concerné */
	private final int code;
	/** Type de changement sur le pari */
	private final BetChangeTypeEnum betChangeType;	
	/** Masse des enjeux si elle est modifiée */
	private final BigDecimal stake;	
	
	/** Constructeur à partir de l'évènement CDI, et de l'identifiant du système d'origine */
	public BetEvt(BetChange betChange, String origin) {
		super(betChange, origin);

		this.code = betChange.getCode();
		this.betChangeType = betChange.getBetChangeType();
		this.stake = betChange.getStake();
	}
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the betChangeType
	 */
	public BetChangeTypeEnum getBetChangeType() {
		return betChangeType;
	}

	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.AbstractEvt#toJson()
	 */
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());
			builder.add("racePk", this.getRacePk());
			if (getDate() != null) {
				builder.add("date", getDate().toString());
			}
			builder.add("eventId", getId());
			builder.add("raceNumber", getNumber());
			builder.add("betCode", getCode());
			builder.add("changeType", getBetChangeType().name());
			if (stake != null) {
				builder.add("stake", stake);
			} else {
				builder.addNull("stake");
			}
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	};

}