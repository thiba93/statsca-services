package com.carrus.statsca.admin.ejb.interfaces;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.carrus.statsca.admin.entity.FcmToken;

@Local
public interface FcmTokenDataAccessService {

	public void saveToken(FcmToken fcmToken);

	public boolean updateToken(FcmToken fcmToken);

	public FcmToken retrieveTokenByValueAndPlateforme(String value, String plateforme);

	public Set<FcmToken> retrieveTokenByValue(String value);

	public Set<FcmToken> retrieveAllTokens();

	public Set<FcmToken> retrieveAllTokensByPlateform(String plateform);

	boolean deleteTokenByValue(String value);

	public List<FcmToken> getExpiredTokens();

	public boolean deleteTokensByIds(Set<Long> ids);

}