package com.carrus.statsca.admin;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.dto.ParametreDTO;
import com.carrus.statsca.admin.restws.utils.SecurityUtil;

public class StoreAdmin {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreAdmin.class);

	
	public final static String APPLICATION_NAME_ID = "statsca.application.name";
	
	public final static String JWT_PUBLIC_KEY_ID = "statsca.jwt.public.key";
	
	public final static String CONTACT_EMAIL = "statsca.contact.email";

	public final static String EMAIL_REPLY = "statsca.email.reply";
	
	
	public final static String ATT_IMPORT = "statsca.att.import";
	
	public final static String INJECTOR_FILESYSTEM= "statsca.services.recipes.injector.path";
	
	public final static String ACTIVATION_HISTORY= "statca.date.activation.history";
	
	public final static String ATT_TO_FILTER= "statca.att.filter";

	private static Map<String, ParametreDTO> parameters = new HashMap<>();
	
	
	private static StoreAdmin instance = null;
	static {
		instance = new StoreAdmin();
	}

	public static StoreAdmin getInstance() {
		synchronized (StoreAdmin.class) {
			return instance;
		}
	}
	
	public void pushParameters(List<ParametreDTO> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(param -> parameters.put(param.getKey(), param));
		}
		
	}

	public Set<ParametreDTO> retrieveParameters() {
		return new HashSet<>(parameters.values());
	}
	
	public String getApplicationName() {
		return parameters.get(APPLICATION_NAME_ID).getValue();
	}

	public PublicKey getJwtPublicKey() {
		
		return SecurityUtil.getKey(parameters.get(JWT_PUBLIC_KEY_ID).getValue());
	}

	public String retrieveEmailReply() {
		return parameters.get(EMAIL_REPLY).getValue();
	}
	
	public String retrieveContactEmail() {
		return parameters.get(CONTACT_EMAIL).getValue();
	}
	public List<String> getAttImport() {
		if(parameters.get(ATT_IMPORT) != null) {
			return List.of(parameters.get(ATT_IMPORT).getValue().substring(4).split(",")); //PPI:80,89
		}
		return Collections.emptyList();
	}
	
	public String retrieveParameter(String parameter) {
		if(parameters.get(parameter) != null) {
			return parameters.get(parameter).getValue();
		}
		return null;
	}
	
	
}
