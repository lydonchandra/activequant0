/****

    activequant - activestocks.eu

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

	
	contact  : contact@activestocks.eu
    homepage : http://www.activestocks.eu

****/
package org.activequant.util.messaging.email;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.activequant.util.messaging.MessengerBase;
import org.activequant.util.messaging.Protocol;


/**
 * This helper class sends an email through an SMTP host. Authentication is not
 * supported. This class is very helpful when you want to send automated
 * evaluations to your inbox or to a mailing list. Only text messages are
 * supported (no multiparts for now)<br>
 * <br>
 * <b>History:</b><br>
 *  - [21.07.2007] Based on code of Ulrich (Ulrich Staudinger)<br>
 *
 *  @author Ulrich Staudinger
 * 
 */
public class EMailMessenger extends MessengerBase {

	private String emailHost;

	private int emailPort;

	private String emailFrom;

	private String hostUsername = null;

	private String hostPassword = null;

	public EMailMessenger(String emailHost, int emailPort, String emailFrom) {
		this.emailHost = emailHost;
		this.emailPort = emailPort;
		this.emailFrom = emailFrom;
	}

	public EMailMessenger(String emailHost, int emailPort, String emailFrom,
			String hostUsername, String hostPassword) {
		this.emailHost = emailHost;
		this.emailPort = emailPort;
		this.emailFrom = emailFrom;
		this.hostUsername = hostUsername;
		this.hostPassword = hostPassword;
	}

	/**
	 * private class for mail authentication. <br>
	 * <br>
	 * <b>History:</b><br>
	 *  - [21.07.2007] Created (Ulrich Staudinger)<br>
	 *
	 *  @author Ulrich Staudinger
	 */
	class MailAuthenticator extends Authenticator {
		private final String user;

		private final String password;

		public MailAuthenticator(String user, String password) {
			this.user = user;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.user, this.password);
		}
	}

	@Override
	public void sendMessage(String to, String subject, String msg) throws Exception {

		// Create a mail session
		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", emailHost);
		props.put("mail.smtp.port", "" + emailPort);
		MailAuthenticator auth = null; 
		
		if(hostUsername!=null){
			// ok, host requires authentication. 
			auth = new MailAuthenticator(hostUsername, hostPassword);
			props.put("mail.smtp.auth", "true");
		}
		Session session = Session.getDefaultInstance(props, auth);

		// Construct the message
		Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(this.emailFrom));
		mail.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mail.setSubject(subject);
		mail.setText(msg);

		// Send the message
		Transport.send(mail);

	}

	public void connect() throws Exception {
	}

	public void disconnect() {
		}

	public void reconnect() throws Exception {
	}

	public String getId() {
		return "EMAIL";
	}

	public void setId(String id) throws Exception {
	}

	public Protocol getProtocol() {
		return Protocol.MAIL;
	}

	public boolean getOnlineStatus(String id) throws Exception {
		return false;
	}
}