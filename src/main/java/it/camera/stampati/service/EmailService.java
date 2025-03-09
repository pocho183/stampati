package it.camera.stampati.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.camera.stampati.model.EmailModel;
import it.camera.stampati.model.SenderModel;

@Service
public class EmailService {
	
	private final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private Environment env;
	
	public void send(EmailModel email) throws MessagingException {
		sendMailMessage(prepareMessage(email), email.getSender());
	}
	
	public SenderModel getSenderEmail() {
		SenderModel sender = new SenderModel();
		sender.setEmail(env.getProperty("mail.sender"));
		sender.setUsername(env.getProperty("mail.username"));
		sender.setPassword(env.getProperty("mail.password"));
		return sender;
	}
	
	private void sendMailMessage(MimeMessage message, SenderModel sender) throws MessagingException {
		Transport transport = null;
		try {
			URLName url = new URLName(env.getProperty("mail.protocol"), env.getProperty("mail.smtp.host"), Integer.parseInt(env.getProperty("mail.port")), null, sender.getUsername(), sender.getPassword());
			PasswordAuthentication auth = new PasswordAuthentication(sender.getUsername(), sender.getPassword());
			message.getSession().setPasswordAuthentication(url, auth);
			transport = message.getSession().getTransport(url);
			transport.connect();
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
		} catch (NumberFormatException | MessagingException e) {
			 logger.error("Email sending failed: " + e.getMessage(), e);
			 throw new MessagingException("Failed to send email", e);		    
		} finally {
			if(transport != null)
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
		}
	}
	
	private MimeMessage prepareMessage(EmailModel email) {
		Session session = createMailSession();
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(email.getSender().getEmail()));
			message.addHeader("Disposition-Notification-To", email.getSender().getEmail());
			handleRecipients(message, email.getRecipient(), Message.RecipientType.TO);
			handleRecipients(message, email.getRecipientCC(), Message.RecipientType.CC);
			handleRecipients(message, email.getRecipientCCn(), Message.RecipientType.BCC);
			message.setSubject(email.getSubject());
			message.setText(email.getBody(), "UTF-8", "html");
			message.setSentDate(new Date());
		} catch (MessagingException e) {
			logger.error(e.getMessage());;
		}
		return message;
	}
	
	private Session createMailSession() {
		Properties props = new Properties();
		props.put("mail.smtp.host", env.getProperty("mail.smtp.host"));
		props.put("mail.smtp.port", env.getProperty("mail.port"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", env.getProperty("mail.smtp.host"));
		return Session.getDefaultInstance(props);
	}
	
	private void handleRecipients(MimeMessage message, List<String> recipients, Message.RecipientType type) throws MessagingException {
		if(recipients != null) {
			checkDomainsExist(recipients);
			for(String recipient : recipients) {
				message.addRecipients(type, recipient);
			}
		}
	}
	public List<String> checkDomainsExist(List<String> recipients) {
		List<String> deleted = new ArrayList<>();
		if(recipients != null) {
			for(String recipient: recipients) {
				try {
					doLookup(recipient);
				} catch(Exception e) {
					deleted.add(recipient);
				}
			}
			recipients.removeAll(deleted);
		}
		return recipients;
	}
	
	static int doLookup(String email) {
		int size = 0;
		Hashtable<String, String> env = new Hashtable<>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		try {
			DirContext ictx = new InitialDirContext(env);
			String hostName = email.substring(email.indexOf("@") +1, email.length());
			Attributes attrs;
			attrs = ictx.getAttributes(hostName, new String[] { "MX" });
			Attribute attr = attrs.get( "MX" );
			if(attr == null) return(0);
			size = attr.size();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return size;
	}
}

