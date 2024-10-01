package com.carrus.statsca.dto;

import java.time.ZonedDateTime;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.carrus.statsca.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.pmc.club.entity.ExternalRaceState;
import com.pmc.club.entity.GroundConditionEnum;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceCategory;
import com.pmc.club.entity.RaceDiscipline;
import com.pmc.club.entity.enums.MatterEnum;
import com.pmc.club.entity.enums.RailEnum;
import com.pmc.club.entity.enums.StartType;

/**
 * DataTransfertObject de l'entité Race
 * 
 * @author BT - ARTSYS 2021
 * @since 20 septembre 2021
 */
@AutoFillFrom(value = Race.class, fillerPath = "raceDetails")
public class RaceDetailDTO {
	
	/**clé primaire de l'objet dans la base club*/
	private Long pk;
	


	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	/** Identifiant de la source */
	private String sourceId = null;
	/** Nom court de la source */
	private String srcShortName = null;
	/** Nom abbrégé de la course sur le TOTE */
	private String s3kShortName;
	/** Importance de la course */
	private MatterEnum matterCode;
	/** Conditions de participation à la course */
	private RaceConditionsDTO raceConditions;
	/** Etat externe de la course */
	private ExternalRaceState externalRaceState;
	/** Catégorie de la course (Trot / Galop) */
	private RaceCategory category;
	/** Libellé de la catégorie de course */
	private String raceType;
	/** Discipline de la course (catégorie issue du TOTE) */
	private RaceDiscipline discipline;
	/** Type de départ */
	private StartType startTypeCode = null;
	/** Distance de la course pour information */
	private Integer distance;
	/** Unité de mesure de la distance */
	private String distanceUnit = "m.";
	/** Libellé du parcours */
	private String course;
	/** Type de piste, énumération à terme */
	private Byte trackCode = null;
	/** Libellé de la piste */
	private String trackName;
	/** Type de corde sur la piste */
	private RailEnum railCode;
	/** Observation de l'état du terrain */
	private String groundSight;
	/** Date et heure de mesure des conditions de terrain */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime groundConditionDate;
	/** Enumération des conditions du terrain */
	private GroundConditionEnum groundConditionCode;
	/** Peloton */
	private String pack;
	/** Vidéo sur la course ? */
	private boolean track = false;

	/** Nb de partants de la course */
	private Short runnerNumber = 0;
	/** Permet de définir les courses prioritaires */
	private boolean priority;
	/** Heure de début des opérations */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime operationsStart;
	/** Durée de la course du vainqueur en secondes */
	private Integer duration;
	/** Prix attribués à la course */
	private PricesDTO prices;
	/** Références de la course */
	private ReferencesDTO references;
	/** Enquête éventuelle à l'arrivée */
	private InvestigationDTO investigation;
	
	/** Constructeur par copie */
	public RaceDetailDTO(Race race) {
		AutoFillerEngine.autoFill(this, race);
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the srcShortName
	 */
	public String getSrcShortName() {
		return srcShortName;
	}

	/**
	 * @param srcShortName the srcShortName to set
	 */
	public void setSrcShortName(String srcShortName) {
		this.srcShortName = srcShortName;
	}

	/**
	 * @return the s3kShortName
	 */
	public String getS3kShortName() {
		return s3kShortName;
	}

	/**
	 * @param s3kShortName the s3kShortName to set
	 */
	@AutoCopy(value = "getShortName", chain = "trim")
	public void setS3kShortName(String s3kShortName) {
		this.s3kShortName = s3kShortName;
	}

	/**
	 * @return the matterCode
	 */
	public MatterEnum getMatterCode() {
		return matterCode;
	}

	/**
	 * @param matterCode the matterCode to set
	 */
	@AutoCopy("getMatter")
	public void setMatterCode(MatterEnum matterCode) {
		this.matterCode = matterCode;
	}

	/**
	 * @return the raceConditions
	 */
	public RaceConditionsDTO getRaceConditions() {
		return raceConditions;
	}

	/**
	 * @param raceConditions the raceConditions to set
	 */
	@AutoInstanciate(value = "getRaceConditions", useProfil = false)
	public void setRaceConditions(RaceConditionsDTO raceConditions) {
		this.raceConditions = raceConditions;
	}

	/**
	 * @return the externalRaceState
	 */
	public ExternalRaceState getExternalRaceState() {
		return externalRaceState;
	}

	/**
	 * @param externalRaceState the externalRaceState to set
	 */
	@AutoCopy("getExternalState")
	public void setExternalRaceState(ExternalRaceState externalRaceState) {
		this.externalRaceState = externalRaceState;
	}

	/**
	 * @return the discipline
	 */
	public RaceDiscipline getDiscipline() {
		return discipline;
	}

	/**
	 * @param discipline the discipline to set
	 */
	@AutoCopy("getDiscipline")
	public void setDiscipline(RaceDiscipline discipline) {
		this.discipline = discipline;
	}

	/**
	 * @return the category
	 */
	public RaceCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	@AutoCopy("getCategory")
	public void setCategory(RaceCategory category) {
		this.category = category;
	}

	/**
	 * @return the raceType
	 */
	public String getRaceType() {
		return raceType;
	}

	/**
	 * @param raceType the raceType to set
	 */
	@AutoCopy("getRaceType")
	public void setRaceType(String raceType) {
		this.raceType = raceType;
	}

	/**
	 * @return the startTypeCode
	 */
	public StartType getStartTypeCode() {
		return startTypeCode;
	}

	/**
	 * @param startTypeCode the startTypeCode to set
	 */
	@AutoCopy("getStartType")
	public void setStartTypeCode(StartType startTypeCode) {
		this.startTypeCode = startTypeCode;
	}

	/**
	 * @return the distance
	 */
	public Integer getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	@AutoCopy("getDistance")
	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	/**
	 * @return the distanceUnit
	 */
	public String getDistanceUnit() {
		return distanceUnit;
	}

	/**
	 * @param distanceUnit the distanceUnit to set
	 */
	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	/**
	 * @return the course
	 */
	public String getCourse() {
		return course;
	}

	/**
	 * @param course the course to set
	 */
	@AutoCopy("getCourse")
	public void setCourse(String course) {
		this.course = course;
	}

	/**
	 * @return the trackCode
	 */
	public Byte getTrackCode() {
		return trackCode;
	}

	/**
	 * @param trackCode the trackCode to set
	 */
	public void setTrackCode(Byte trackCode) {
		this.trackCode = trackCode;
	}

	/**
	 * @return the trackName
	 */
	public String getTrackName() {
		return trackName;
	}

	/**
	 * @param trackName the trackName to set
	 */
	@AutoCopy("getTrack")
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	/**
	 * @return the railCode
	 */
	public RailEnum getRailCode() {
		return railCode;
	}

	/**
	 * @param railCode the railCode to set
	 */
	@AutoCopy("getRail")
	public void setRailCode(RailEnum railCode) {
		this.railCode = railCode;
	}

	/**
	 * @return the runnerNumber
	 */
	public Short getRunnerNumber() {
		return runnerNumber;
	}

	/**
	 * @param runnerNumber the runnerNumber to set
	 */
	@AutoCopy("getRunnerNumber")
	public void setRunnerNumber(Short runnerNumber) {
		this.runnerNumber = runnerNumber;
	}

	/**
	 * @return the groundSight
	 */
	public String getGroundSight() {
		return groundSight;
	}

	/**
	 * @param groundSight the groundSight to set
	 */
	@AutoCopy("getGroundSight")
	public void setGroundSight(String groundSight) {
		this.groundSight = groundSight;
	}

	/**
	 * @return the groundConditionDate
	 */
	public ZonedDateTime getGroundConditionDate() {
		return groundConditionDate;
	}

	/**
	 * @param groundConditionDate the groundConditionDate to set
	 */
	@AutoCopy("getGroundConditionDate")
	public void setGroundConditionDate(ZonedDateTime groundConditionDate) {
		this.groundConditionDate = groundConditionDate;
	}

	/**
	 * @return the groundConditionCode
	 */
	public GroundConditionEnum getGroundConditionCode() {
		return groundConditionCode;
	}

	/**
	 * @param groundConditionCode the groundConditionCode to set
	 */
	@AutoCopy("getGroundCondition")
	public void setGroundConditionCode(GroundConditionEnum groundConditionCode) {
		this.groundConditionCode = groundConditionCode;
	}

	/**
	 * @return the pack
	 */
	public String getPack() {
		return pack;
	}

	/**
	 * @param pack the pack to set
	 */
	@AutoCopy("getPack")
	public void setPack(String pack) {
		this.pack = pack;
	}

	/**
	 * @return the priority
	 */
	public boolean isPriority() {
		return priority;
	}

	/**
	 * @return the track
	 */
	public boolean isTrack() {
		return track;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(boolean track) {
		this.track = track;
	}

	/**
	 * @param priority the priority to set
	 */
	@AutoCopy("getPriority")
	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	/**
	 * @return the operationsStart
	 */
	public ZonedDateTime getOperationsStart() {
		return operationsStart;
	}

	/**
	 * @param operationsStart the operationsStart to set
	 */
	@AutoCopy("getOperationsStart")
	public void setOperationsStart(ZonedDateTime operationsStart) {
		this.operationsStart = operationsStart;
	}

	/**
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	@AutoCopy("getDuration")
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * @return the prices
	 */
	public PricesDTO getPrices() {
		return prices;
	}

	/**
	 * @param prices the prices to set
	 */
	@AutoInstanciate(value = "getPrices", useProfil = false)
	public void setPrices(PricesDTO prices) {
		this.prices = prices;
	}

	/**
	 * @return the references
	 */
	public ReferencesDTO getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	@AutoInstanciate(value = "getReferences", useProfil = false)
	public void setReferences(ReferencesDTO references) {
		this.references = references;
	}

	/**
	 * @return the investigation
	 */
	public InvestigationDTO getInvestigation() {
		return investigation;
	}

	/**
	 * @param investigation the investigation to set
	 */
	@AutoInstanciate(value = "getInvestigation")
	public void setInvestigation(InvestigationDTO investigation) {
		this.investigation = investigation;
	}
}