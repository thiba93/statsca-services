package com.carrus.statsca.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.carrus.statsca.event.UpdatedRacesRecipesEvt;

public class UpdatedRacesRecipesDTO {
	
	private List<RaceRecipeDTO> raceRecipes;
	
	public UpdatedRacesRecipesDTO(UpdatedRacesRecipesEvt urr) {
		raceRecipes = urr.getRaceRecipes().stream().map(RaceRecipeDTO::new).collect(Collectors.toList());
	}
	
	public UpdatedRacesRecipesDTO() {
		raceRecipes = new ArrayList<>();
	}

	public List<RaceRecipeDTO> getRaceRecipes() {
		return raceRecipes;
	}

	public void setRaceRecipes(List<RaceRecipeDTO> raceRecipes) {
		this.raceRecipes = raceRecipes;
	}

}
