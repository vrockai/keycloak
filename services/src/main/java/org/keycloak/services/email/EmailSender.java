/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.keycloak.services.email;

import org.jboss.resteasy.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AccessCodeEntry;
import org.keycloak.services.resources.flows.Urls;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class EmailSender {

    private static final Logger log = Logger.getLogger(EmailSender.class);

    private Map<String, String> config;

    public EmailSender(Map<String, String> config) {
        this.config = config;
    }

    public void send(String address, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", config.get("host"));

        boolean auth = "true".equals(config.get("auth"));
        boolean ssl = "true".equals(config.get("ssl"));
        boolean starttls = "true".equals(config.get("starttls"));

        if (config.containsKey("port")) {
            props.setProperty("mail.smtp.port", config.get("port"));
        }

        if (auth) {
            props.put("mail.smtp.auth", "true");
        }

        if (ssl) {
            props.put("mail.smtp.socketFactory.port", config.get("port"));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        if (starttls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        String from = config.get("from");

        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSubject(subject);
        msg.setText(body);
        msg.saveChanges();

        Transport transport = session.getTransport("smtp");
        if (auth) {
            transport.connect(config.get("user"), config.get("password"));
        } else {
            transport.connect();
        }
        transport.sendMessage(msg, new InternetAddress[] { new InternetAddress(address) });
    }

    public void sendEmailVerification(UserModel user, RealmModel realm, AccessCodeEntry accessCode, UriInfo uriInfo) {
        UriBuilder builder = Urls.loginActionEmailVerificationBuilder(uriInfo.getBaseUri());
        builder.queryParam("key", accessCode.getId());

        URI uri = builder.build(realm.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("Hi ").append(user.getFirstName()).append(",\n\n");
        sb.append("Someone has created a Keycloak account with this email address. ");
        sb.append("If this was you, click the link below to verify your email address:\n");
        sb.append(uri.toString());
        sb.append("\n\nThis link will expire within ").append(TimeUnit.SECONDS.toMinutes(realm.getAccessCodeLifespanUserAction()));
        sb.append(" minutes.\n\n");
        sb.append("If you didn't create this account, just ignore this message.\n\n");
        sb.append("Thanks,\n");
        sb.append("The Keycloak Team");

        try {
            send(user.getEmail(), "Verify email", sb.toString());
        } catch (Exception e1) {
            log.warn("Failed to send email verification");
        }
    }

    public void sendPasswordReset(UserModel user, RealmModel realm, AccessCodeEntry accessCode, UriInfo uriInfo) {
        UriBuilder builder = Urls.loginPasswordResetBuilder(uriInfo.getBaseUri());
        builder.queryParam("key", accessCode.getId());

        URI uri = builder.build(realm.getId());

        StringBuilder sb = new StringBuilder();

        sb.append("Hi ").append(user.getFirstName()).append(",\n\n");
        sb.append("Someone just requested to change your Keycloak account's password. ");
        sb.append("If this was you, click on the link below to set a new password:\n");
        sb.append(uri.toString());
        sb.append("\n\nThis link will expire within ").append(TimeUnit.SECONDS.toMinutes(realm.getAccessCodeLifespanUserAction()));
        sb.append(" minutes.\n\n");
        sb.append("If you don't want to reset your password, just ignore this message and nothing will be changed.\n\n");
        sb.append("Thanks,\n");
        sb.append("The Keycloak Team");

        try {
            send(user.getEmail(), "Reset password link", sb.toString());
        } catch (Exception e) {
            log.warn("Failed to send reset password link", e);
        }
    }

}
