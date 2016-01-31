package com.resto.image;

/**
 * 
 */

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 * This class is used to send Email to the intended Recipients, Does not need email client authentication
 * @author kkanaparthi
 * 
 */
public class RHEMailClientUnAuth {

	private static final Logger log = Logger
			.getLogger(RHEMailClientUnAuth.class.getName());
	private static Properties props = new Properties();


	/**
	 * This method sends the email, with the specified subject, and content.
	 * @param subject emailSubjet
	 * @param emailContent emailContent
	 */
	public static void sendEmailMessage(String subject, String emailContent) {
		props.put("mail.smtp.host",
				ImageCrawlerPropertyUtil.getProperty("emailHost"));

		Session session = Session.getDefaultInstance(props);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(ImageCrawlerPropertyUtil
					.getProperty("fromEmail")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress
					.parse(ImageCrawlerPropertyUtil.getProperty("toEmail")));
			message.setSubject(subject);
			message.setText(emailContent);
			Transport.send(message);
			log.info("Sent Email Successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	public RHEMailClientUnAuth() {
	}

	/**
	 * main method for Unit Testing.
	 * @param args
	 */
	public static void main(String[] args) {
		sendEmailMessage("ALERT! - Images Missing Alert",
				"There are Some Images Missing in the Domain, which needes Your attention !");
	}

}
