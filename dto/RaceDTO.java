package com.carrus.statsca.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciateList;
import com.carrus.statsca.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceState;

/**
 * DataTransfertObject de l'entité Race
 * 
 * @author BT - ARTSYS 2021
 * @since 20 septembre 2021
 */

@AutoFillFrom(value = Race.class, fillerPath = "race")
public class RaceDTO implements Comparable<RaceDTO> {
	
	/**clé primaire de l'objet dans la base club*/
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	/** Numéro de la course dans la réunion */
	private int number;
	
	/** Nom de la course */
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	/** Nom court de la course*/
	@JsonInclude(Include.NON_NULL)
	private String shortName;
	
	/**
	 * Give the expected start date and time. It is only for information
	 * purpose, the real start date will be given by the chronology.
	 */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime expectedStart;
	/** Etat de la course */
	@JsonInclude(Include.NON_NULL)
	private RaceState state;
	/** Représentation de l'arrivée de la course, si elle l'est */
//	@JsonInclude(Include.NON_NULL)
//	private List<List<Short>> arrival = null;
	/** Liste des formulations dsponibles */
	@JsonInclude(Include.NON_NULL)
	private Collection<AuthorisedFormulationDTO> formulations;
	/** Détail de la course si disponible */
//	@JsonInclude(Include.NON_NULL)
//	private RaceDetailDTO raceDetails = null;
	
	private Short runnerNumber = 0;
	

//	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
//	@JsonSerialize(using = ZonedDateTimeSerializer.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
//	@JsonInclude(Include.NON_NULL)
//	private ZonedDateTime operationsStart;

//	@JsonInclude(Include.NON_NULL)
//	private ExternalRaceState externalState;
//
//	@JsonInclude(Include.NON_NULL)
//	private GroundConditionEnum groundCondition;

	@JsonInclude(Include.NON_NULL)
	private List<RemarkableDTO> remarkables;
	
	@JsonInclude(Include.NON_EMPTY)
	private List<AuthorisedPartnerDTO> authorizedPartners; 
	
	@JsonInclude(Include.NON_NULL)
	private Boolean remarkableRace;
	
	
	/** Constructeur par défaut */
	public RaceDTO() {}

	/** Constructeur par copie */
	public RaceDTO(Race race) {
		AutoFillerEngine.autoFill(this, race);
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	@AutoCopy("getNumber")
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@AutoCopy(value = "getName", chain = "trim")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the expectedStart
	 */
	public ZonedDateTime getExpectedStart() {
		return expectedStart;
	}

	/**
	 * @param expectedStart the expectedStart to set
	 */
	@AutoCopy("getExpectedStart")
	public void setExpectedStart(ZonedDateTime expectedStart) {
		this.expectedStart = expectedStart;
	}

	/**
	 * @return the state
	 */
	public RaceState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	@AutoCopy("getState")
	public void setState(RaceState state) {
		this.state = state;
	}

//	/**
//	 * @return the arrival
//	 */
//	public List<List<Short>> getArrival() {
//		return arrival;
//	}
//
//	/**
//	 * @param arrival the arrival to set
//	 */
//	public void setArrival(List<List<Short>> arrival) {
//		this.arrival = arrival;
//	}

	/**
	 * @return the formulations
	 */
	public Collection<AuthorisedFormulationDTO> getFormulations() {
		return formulations;
	}

	/**
	 * @param formulations the formulations to set
	 */
	@AutoInstanciateList(value = "getFormulations")
	public void setFormulations(Collection<AuthorisedFormulationDTO> formulations) {
		this.formulations = formulations;
	}

//	/**
//	 * @return the raceDetails
//	 */
//	public RaceDetailDTO getRaceDetails() {
//		return raceDetails;
//	}
//
//	/**
//	 * @param raceDetails the raceDetails to set
//	 */
//	@AutoInstanciate(activated = false)
//	public void setRaceDetails(RaceDetailDTO raceDetails) {
//		this.raceDetails = raceDetails;
//	}

	public Short getRunnerNumber() {
		return runnerNumber;
	}

	/**
	 * @param runnerNumber the runnerNumber to set
	 */
	@AutoCopy(value = "getRunnerNumber")
	public void setRunnerNumber(Short runnerNumber) {
		this.runnerNumber = runnerNumber;
	}

//	public ZonedDateTime getOperationsStart() {
//		return operationsStart;
//	}
//
//	@AutoCopy("getOperationsStart")
//	public void setOperationsStart(ZonedDateTime operationsStart) {
//		this.operationsStart = operationsStart;
//	}

	public String getShortName() {
		return shortName;
	}

	@AutoCopy("getShortName")
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

//	public ExternalRaceState getExternalState() {
//		return externalState;
//	}
//
//	@AutoCopy("getExternalState")
//	public void setExternalState(ExternalRaceState externalState) {
//		this.externalState = externalState;
//	}
//
//	public GroundConditionEnum getGroundCondition() {
//		return groundCondition;
//	}
//
//	@AutoCopy("getGroundCondition")
//	public void setGroundCondition(GroundConditionEnum groundCondition) {
//		this.groundCondition = groundCondition;
//	}

	public List<RemarkableDTO> getRemarkables() {
		return remarkables;
	}

	@AutoInstanciateList(value = "getRemarkables")
	public void setRemarkables(List<RemarkableDTO> remarkables) {
		this.remarkables = remarkables;
	}

	public List<AuthorisedPartnerDTO> getAuthorizedPartners() {
		return authorizedPartners;
	}

	@AutoInstanciateList(value = "getAuthorisedPartners", activated = true)
	public void setAuthorizedPartners(List<AuthorisedPartnerDTO> partners) {
		this.authorizedPartners = partners;
	}

	@Override
	public int compareTo(RaceDTO o) {
		return Integer.compare(this.getNumber(), o.getNumber());
	}

	public Boolean getRemarkableRace() {
		return remarkableRace;
	}

	public void setRemarkableRace(Boolean grandPrizeTag) {
		this.remarkableRace = grandPrizeTag;
	}
}