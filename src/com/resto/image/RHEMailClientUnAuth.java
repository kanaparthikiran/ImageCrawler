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
 * @author kkanaparthi
 * 
 */
public class RHEMailClientUnAuth {

	private static final Logger log = Logger
			.getLogger(RHEMailClientUnAuth.class.getName());
	private static Properties props = new Properties();

	/**
	 * 
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
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		sendEmailMessage("ALERT! - Images Missing Alert",
				"There are Some Images Missing in the Domain, which needes Your attention !");
	}

}
