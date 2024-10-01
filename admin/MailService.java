package com.carrus.statsca.admin;

import java.io.IOException;

import javax.ejb.Local;
import javax.mail.MessagingException;

import com.carrus.statsca.admin.dto.ContactMailDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.mail.EnumLanguage;

@Local
public interface MailService {

	public void sendMail(String subject, String content, String address) throws MessagingException;
	
	public boolean sendContactMail(UserDTO user, ContactMailDTO dto, EnumLanguage language) throws NullPointerException, IOException, MessagingException;
}
