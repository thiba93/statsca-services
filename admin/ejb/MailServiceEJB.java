package com.carrus.statsca.admin.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.MailService;
import com.carrus.statsca.admin.StoreAdmin;
import com.carrus.statsca.admin.dto.ContactMailDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.mail.EnumLanguage;
import com.carrus.statsca.admin.mail.EnumTypeMail;


@Stateless
public class MailServiceEJB implements MailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceEJB.class);

	private static final String MAIL_CONTENT_EMPTY_EXCEPTION = "Mail Content is empty";

	private static final String MAIL_PATH = "mail/";
	
	private static final String IMAGE_PATH = MAIL_PATH + "image/";
	
	private static final String MAIL_CONTENT_TYPE = MediaType.TEXT_HTML + "; charset=" + StandardCharsets.UTF_8.displayName();

	private static final String LANG_FILE = MAIL_PATH +  "/language/language_";
	private static final String PROPERTIES_FILE_TYPE = ".properties";
	
	private static final String RIGHTS_PROPERTIES = "rights";
	private static final String RIGHTS_PLACEHOLDER = "%RIGHTS%";

	@Resource(name = "java:jboss/mail/Default")
	private javax.mail.Session session;

	private Message initMail(String address, String subject) throws MessagingException {
		Message message = new MimeMessage(session);
		String reply = StoreAdmin.getInstance().retrieveEmailReply();
		message.setRecipients(RecipientType.TO, InternetAddress.parse(address));
		message.setFrom(InternetAddress.parse(reply)[0]);
		message.setHeader("Content-Type", MAIL_CONTENT_TYPE);
		message.setSubject(subject);
		message.setSentDate(new Date());

		LOGGER.debug("initMail() : destinataire = [{}] expéditeur = [{}] content-type = [{}] subject = [{}]", 
				address, reply, MAIL_CONTENT_TYPE, subject);
		
		return message;
	}
	
	private Map<String, String> generateDefaultMapImages() {
		Map<String, String> mapImages = new HashMap<>();
		
		mapImages.put("carruslogo1", IMAGE_PATH + "logo_carrus.png");
		
		return mapImages;
	}

	private void addImageParts(Map<String, String> mapImages, Multipart multipart) throws MessagingException {
		if(mapImages != null && mapImages.size() > 0) {
			Set<String> imageIds = mapImages.keySet();
			
			for(String imageId : imageIds) {
				/* Creating MimeBodyPart for each images */
				MimeBodyPart imagePart = new MimeBodyPart();
				imagePart.setHeader("Content-ID", imageId);
				imagePart.setDisposition(Part.INLINE);
				
				/* retrieve file path from the map */
				String imageFilePath = mapImages.get(imageId);
				/* retrieve inputStream of the file in ressources */ 
				try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(imageFilePath)){
					ByteArrayDataSource bads = new ByteArrayDataSource(is, "image/jpg");				
					/* set image in MimeBodyPart */
					imagePart.setDataHandler(new DataHandler(bads));		
					/* add image to multipart */
					multipart.addBodyPart(imagePart);
				}catch(Exception e) {
					LOGGER.error("addImageParts() - Error while reading InputStream : {}", e.getMessage());
				}			
			}
		}		
	}

	

	@Override
	public void sendMail(String subject, String content, String address) throws MessagingException {
		// Throw an Exception if unable to send email
		Message message = initMail(address, subject);
		
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(content, MAIL_CONTENT_TYPE);		
		
		/* create multipart */
		Multipart multipart = new MimeMultipart();
		/* add body part containing the message */
		multipart.addBodyPart(bodyPart);
		/* prepare default image to be shown */
		Map<String, String> mapImages = generateDefaultMapImages();
		/* add body parts containing images in multipart */
		addImageParts(mapImages, multipart);

		message.setContent(multipart);

		sendMail(message);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("sendMail() - An email was sent to : {}", address);
		}
	}
	
	private void sendMail (Message message) throws MessagingException {
		Transport.send(message);
	}
	
	
	private String convertStreamToString(InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			return s.useDelimiter("\\A").hasNext() ? s.next() : "";
		}
	}
	
	private String getRessource(EnumTypeMail enumTypeMail) {
		if (enumTypeMail == null || enumTypeMail.getFileName() == null || enumTypeMail.getFileName().isBlank() || enumTypeMail.getFileName().isEmpty()) {
			throw new NullPointerException();
		}
		return convertStreamToString(this.getClass().getClassLoader().getResourceAsStream(MAIL_PATH + enumTypeMail.getFileName()));
	}

	private Properties initLanguageProperties(EnumLanguage language) throws NullPointerException, IOException  {
		String file = LANG_FILE + language.getLang() + PROPERTIES_FILE_TYPE;
		//retrieve file from ressources
		try (InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file), StandardCharsets.UTF_8)) {
			Properties prop = new Properties();
			//load properties from file
			prop.load(isr);			
			return prop;
		} catch (NullPointerException e) {
			LOGGER.error("initLanguageProperties() - NullPointerException : {}", e.getMessage());	
			throw new NullPointerException("NullPointerException while executing initLanguageProperties");
		} catch(IOException e) {
			LOGGER.error("initLanguageProperties() - IOException occured : {}", e.getMessage());	
			throw new IOException("IOException while executing initLanguageProperties");
		}
	}

	private String createContactContent(UserDTO user, ContactMailDTO dto, Properties langProperties) throws IllegalArgumentException {
		String mailContent = getRessource(EnumTypeMail.CONTACT);
		if(mailContent.isEmpty())
			throw new IllegalArgumentException(MAIL_CONTENT_EMPTY_EXCEPTION);

		String title = langProperties.getProperty("contact.subject");
		String lastName = new StringBuilder(langProperties.getProperty("contact.sender.last.name")).append(" ").append(user.getLastName()).toString();
		String firstName = new StringBuilder(langProperties.getProperty("contact.sender.first.name")).append(" ").append(user.getFirstName()).toString();
		String email = new StringBuilder(langProperties.getProperty("contact.sender.email")).append(" ").append(user.getEmail()).toString();
		LOGGER.debug("createContactContent() : title = [{}] content = [{}] lastName = [{}] firstName = [{}] email = [{}]", 
				title, dto.getContent(), lastName, firstName, email);
		
		mailContent = mailContent.replace("%CONTACT_TITLE%", title);
		mailContent = mailContent.replace("%CONTACT_CONTENT%", dto.getContent());
		mailContent = mailContent.replace("%CONTACT_LAST_NAME%", lastName);
		mailContent = mailContent.replace("%CONTACT_FIRST_NAME%", firstName);
		mailContent = mailContent.replace("%CONTACT_EMAIL%", email);
		mailContent = mailContent.replace(RIGHTS_PLACEHOLDER, langProperties.getProperty(RIGHTS_PROPERTIES));
		
		final String result = mailContent;
		Stream.of("%CONTACT_TITLE%","%CONTACT_CONTENT%","%CONTACT_LAST_NAME%","%CONTACT_FIRST_NAME%","%CONTACT_EMAIL%",RIGHTS_PLACEHOLDER).forEach(motif -> {
			if (result.contains(motif)) {
				LOGGER.error("Le motif [{}] n'a pas été remplacé !!", motif);
			}
		});
		

		return mailContent;
	}

	@Override
	public boolean sendContactMail(UserDTO user, ContactMailDTO dto, EnumLanguage language) throws NullPointerException, IOException, MessagingException {
			//retrieve translation from propreties file
			Properties langProperties = initLanguageProperties(language);
			//init the subject of the mail with utf8 encoding
			String subject = langProperties.getProperty("contact.subject");		
			//init the content of the mail
			String mailContent = createContactContent(user, dto, langProperties);	
			//send the mail with the reports as attachment
			sendMail(subject, mailContent, StoreAdmin.getInstance().retrieveContactEmail());
			
			return true;
	}
	
}
