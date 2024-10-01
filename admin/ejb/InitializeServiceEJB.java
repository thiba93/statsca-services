package com.carrus.statsca.admin.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.DataServiceAdmin;
import com.carrus.statsca.admin.StoreAdmin;
import com.carrus.statsca.admin.dto.ParametreDTO;
import com.carrus.statsca.admin.entity.ParametreEntity;

@Singleton
@Startup
public class InitializeServiceEJB {
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializeServiceEJB.class);
	
	@Inject
	private DataServiceAdmin dataServiceAdmin;
	
//	private static final String[] parameters = new String[] {
//			StoreAdmin.APPLICATION_NAME_ID, 
//			StoreAdmin.JWT_PUBLIC_KEY_ID, 
//			"statsca.contact.phone", 
//			StoreAdmin.CONTACT_EMAIL, 
//			"statsca.contact.request.maxlength",
//			StoreAdmin.EMAIL_REPLY
//	};

	@PostConstruct
	public void initialize() {
		List<ParametreDTO> params = new ArrayList<>();

		
		List<ParametreEntity> paramEntities = dataServiceAdmin.retrieveAllParameters();
		if(paramEntities != null && !paramEntities.isEmpty())
		{
			paramEntities.forEach(entity -> {
				if(StoreAdmin.APPLICATION_NAME_ID.equals(entity.getId()) && ( entity.getTextValue() == null || "".equals(entity.getTextValue()))) {
					LOGGER.error("***********************************************************************************");
					LOGGER.error("Nom d'application non renseigné !!! Impossible d'obtenir un jeton SSO applicatif !!");
					LOGGER.error("***********************************************************************************");
				} else if (StoreAdmin.JWT_PUBLIC_KEY_ID.equals(entity.getId()) && ( entity.getTextValue() == null || "".equals(entity.getTextValue()))) {
					LOGGER.error("*************************************************************************************");
					LOGGER.error("Clef publique JWT non renseignée !!! Impossible de décoder un jeton SSO applicatif !!");
					LOGGER.error("*************************************************************************************");
				} else {
					LOGGER.info("initialize() : key=[{}] value=[{}]", entity.getId(), entity.getTextValue());
					params.add(new ParametreDTO(entity.getId(), entity.getTextValue()));
				}
			});
		}
		
		if (CollectionUtils.isNotEmpty(params)) {
			params.forEach(param -> LOGGER.debug("initialize() : key = [{}] value = [{}]", param.getKey(), param.getValue()));
			StoreAdmin.getInstance().pushParameters(params);
		}
	}
}
