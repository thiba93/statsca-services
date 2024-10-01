package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.References;

/**
 * Sous-structure invariante de la course qui contient les informations des prix sur cette course
 * 
 * @author BT - ARTSYS 2021
 * @since 22 septembre 2021
 */
@AutoFillFrom(value = References.class, fillerPath = "references")
public class ReferencesDTO {
	/** Référence 1 */
	private int reference1;
	/** Référence 2 */
	private int reference2;
	/** Référence 3 */
	private int reference3;
	
	
	public ReferencesDTO(References ref) {
		this.reference1 = ref.getReference1();
		this.reference2 = ref.getReference2();
		this.reference3 = ref.getReference3();
	}

	/**
	 * @return the reference1
	 */
	public int getReference1() {
		return reference1;
	}

	/**
	 * @param reference1 the reference1 to set
	 */
	@AutoCopy("getReference1")
	public void setReference1(int reference1) {
		this.reference1 = reference1;
	}

	/**
	 * @return the reference2
	 */
	public int getReference2() {
		return reference2;
	}

	/**
	 * @param reference2 the reference2 to set
	 */
	@AutoCopy("getReference2")
	public void setReference2(int reference2) {
		this.reference2 = reference2;
	}

	/**
	 * @return the reference3
	 */
	public int getReference3() {
		return reference3;
	}

	/**
	 * @param reference3 the reference3 to set
	 */
	@AutoCopy("getReference3")
	public void setReference3(int reference3) {
		this.reference3 = reference3;
	}

}
