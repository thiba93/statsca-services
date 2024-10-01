package com.carrus.statsca.admin;

import java.util.List;

import javax.ejb.Local;

import com.carrus.statsca.admin.dto.UserDTO;

@Local
public interface UserService {
	
	public List<UserDTO> retrieveAllUsers();

	public UserDTO retrieveByID(Long userID);

	// public UserDTO retrieveByCredentials(String cryptedPassword, String email);

	public UserDTO retrieveByEmail(String email);

	public void createUser(UserDTO user);

	public boolean updateUser(UserDTO user);
	
	public boolean updateLanguageUser (UserDTO user);

	public void deleteUser(UserDTO user);

	public void deleteUsers(List<UserDTO> users);
	


//	public void updateCredentials(String mail, String password);

}
