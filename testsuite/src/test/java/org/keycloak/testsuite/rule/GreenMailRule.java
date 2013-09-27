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
package org.keycloak.testsuite.rule;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.junit.rules.ExternalResource;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class GreenMailRule extends ExternalResource {

    private GreenMail greenMail;

    private Properties originalProperties = new Properties();

    @Override
    protected void before() throws Throwable {
        Iterator<Entry<Object, Object>> itr = System.getProperties().entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Object, Object> e = itr.next();
            if (((String) e.getKey()).startsWith("keycloak.mail")) {
                originalProperties.put(e.getKey(), e.getValue());
                itr.remove();
            }
        }
        
        System.setProperty("keycloak.mail.smtp.from", "auto@keycloak.org");
        System.setProperty("keycloak.mail.smtp.host", "localhost");
        System.setProperty("keycloak.mail.smtp.port", "3025");
        
        ServerSetup setup = new ServerSetup(3025, "localhost", "smtp");

        greenMail = new GreenMail(setup);
        greenMail.start();
    }

    @Override
    protected void after() {
        if (greenMail != null) {
            // Suppress error from GreenMail on shutdown
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    if (!(e.getCause() instanceof SocketException && t.getClass().getName()
                            .equals("com.icegreen.greenmail.smtp.SmtpHandler"))) {
                        System.err.print("Exception in thread \"" + t.getName() + "\" ");
                        e.printStackTrace(System.err);
                    }
                }
            });

            greenMail.stop();
        }

        System.getProperties().remove("keycloak.mail.smtp.from");
        System.getProperties().remove("keycloak.mail.smtp.host");
        System.getProperties().remove("keycloak.mail.smtp.port");
        System.getProperties().putAll(originalProperties);
    }

    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

}
