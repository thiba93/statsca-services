package com.carrus.statsca.admin;

import com.carrus.statsca.admin.dto.UserDTO;

public interface Authentication {

//	public void cleanTokens();

	public String parseToken(String token);

//	public UserDTO getUserFromToken(String token);

//	public void invalidateTokens(Long userID);

//	public void invalidateTokens(String token, String email);

//	public UserDTO validateToken(String token);

//	public boolean validateCode(String code);

//	public String issueToken(UserDTO us, int expirationTime, String code);

//	UserDTO authenticateWithCredentials(String email, String password, String uuid);
	UserDTO authenticateWithCredentials(UserDTO user, String uuid);
	
//	void logout(String token, String email);

}
