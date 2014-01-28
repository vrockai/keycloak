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
package org.keycloak.account.freemarker.model;

import org.keycloak.account.freemarker.FreeMarkerAccount;

import java.util.ResourceBundle;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class MessageBean {

    private String summary;

    private FreeMarkerAccount.MessageType type;

    public MessageBean(String message, FreeMarkerAccount.MessageType type) {
        this.summary = message;
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public String getType() {
        return this.type.toString().toLowerCase();
    }

    public boolean isSuccess() {
        return FreeMarkerAccount.MessageType.SUCCESS.equals(this.type);
    }

    public boolean isWarning() {
        return FreeMarkerAccount.MessageType.WARNING.equals(this.type);
    }

    public boolean isError() {
        return FreeMarkerAccount.MessageType.ERROR.equals(this.type);
    }

}