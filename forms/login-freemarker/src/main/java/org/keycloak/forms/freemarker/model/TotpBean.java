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
package org.keycloak.forms.freemarker.model;

import org.keycloak.models.UserModel;
import org.keycloak.models.utils.Base32;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Random;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class TotpBean {

    private String totpSecret;
    private String totpSecretEncoded;
    private boolean enabled;
    private String contextUrl;

    public TotpBean(UserModel user, URI baseUri) {
        this.enabled = user.isTotp();
        this.contextUrl = baseUri.getPath();

        totpSecret = randomString(20);
        totpSecretEncoded = Base32.encode(totpSecret.getBytes());
    }

    private static String randomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW1234567890";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars.charAt(r.nextInt(chars.length()));
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public String getTotpSecretEncoded() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totpSecretEncoded.length(); i += 4) {
            sb.append(totpSecretEncoded.substring(i, i + 4 < totpSecretEncoded.length() ? i + 4 : totpSecretEncoded.length()));
            if (i + 4 < totpSecretEncoded.length()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getTotpSecretQrCodeUrl() throws UnsupportedEncodingException {
        String contents = URLEncoder.encode("otpauth://totp/keycloak?secret=" + totpSecretEncoded, "utf-8");
        return contextUrl + "qrcode" + "?size=246x246&contents=" + contents;
    }

}

