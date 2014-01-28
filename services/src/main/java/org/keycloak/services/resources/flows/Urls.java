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
package org.keycloak.services.resources.flows;

import org.keycloak.services.resources.AccountService;
import org.keycloak.services.resources.admin.AdminService;
import org.keycloak.services.resources.RealmsResource;
import org.keycloak.services.resources.RequiredActionsService;
import org.keycloak.services.resources.SocialResource;
import org.keycloak.services.resources.TokenService;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class Urls {

    public static URI accountAccessPage(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "accessPage").build(realmId);
    }

    public static UriBuilder accountBase(URI baseUri) {
        return realmBase(baseUri).path(RealmsResource.class, "getAccountService");
    }

    public static URI accountPage(URI baseUri, String realmId) {
        return accountPageBuilder(baseUri).build(realmId);
    }

    public static UriBuilder accountPageBuilder(URI baseUri) {
        return accountBase(baseUri).path(AccountService.class, "accountPage");
    }

    public static URI accountPasswordPage(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "passwordPage").build(realmId);
    }

    public static URI accountSocialPage(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "socialPage").build(realmId);
    }

    public static URI accountTotpPage(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "totpPage").build(realmId);
    }

    public static URI accountTotpRemove(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "processTotpRemove").build(realmId);
    }

    public static URI accountLogout(URI baseUri, String realmId) {
        return accountBase(baseUri).path(AccountService.class, "logout").build(realmId);
    }

    public static URI loginActionUpdatePassword(URI baseUri, String realmId) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "updatePassword").build(realmId);
    }

    public static URI loginActionUpdateTotp(URI baseUri, String realmId) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "updateTotp").build(realmId);
    }

    public static URI loginActionUpdateProfile(URI baseUri, String realmId) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "updateProfile").build(realmId);
    }

    public static URI loginActionEmailVerification(URI baseUri, String realmId) {
        return loginActionEmailVerificationBuilder(baseUri).build(realmId);
    }

    public static UriBuilder loginActionEmailVerificationBuilder(URI baseUri) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "emailVerification");
    }

    public static URI loginPasswordReset(URI baseUri, String realmId) {
        return loginPasswordResetBuilder(baseUri).build(realmId);
    }

    public static UriBuilder loginPasswordResetBuilder(URI baseUri) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "passwordReset");
    }

    public static URI loginUsernameReminder(URI baseUri, String realmId) {
        return loginUsernameReminderBuilder(baseUri).build(realmId);
    }

    public static UriBuilder loginUsernameReminderBuilder(URI baseUri) {
        return requiredActionsBase(baseUri).path(RequiredActionsService.class, "usernameReminder");
    }

    private static UriBuilder realmBase(URI baseUri) {
        return UriBuilder.fromUri(baseUri).path(RealmsResource.class);
    }

    public static URI realmLoginAction(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "processLogin").build(realmId);
    }

    public static URI realmLoginPage(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "loginPage").build(realmId);
    }

    public static URI realmRegisterAction(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "processRegister").build(realmId);
    }

    public static URI realmRegisterPage(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "registerPage").build(realmId);
    }

    public static URI realmOauthAction(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "processOAuth").build(realmId);
    }

    public static URI realmCode(URI baseUri, String realmId) {
        return tokenBase(baseUri).path(TokenService.class, "accessCodeToToken").build(realmId);
    }

    private static UriBuilder saasBase(URI baseUri) {
        return UriBuilder.fromUri(baseUri).path(AdminService.class);
    }

    public static URI saasLoginAction(URI baseUri) {
        return saasBase(baseUri).path(AdminService.class, "processLogin").build();
    }

    public static URI saasLoginPage(URI baseUri) {
        return saasBase(baseUri).path(AdminService.class, "loginPage").build();
    }

    public static URI saasRegisterAction(URI baseUri) {
        return saasBase(baseUri).path(AdminService.class, "processRegister").build();
    }

    public static URI saasRegisterPage(URI baseUri) {
        return saasBase(baseUri).path(AdminService.class, "registerPage").build();
    }

    public static UriBuilder socialBase(URI baseUri) {
        return UriBuilder.fromUri(baseUri).path(SocialResource.class);
    }

    public static URI socialCallback(URI baseUri) {
        return socialBase(baseUri).path(SocialResource.class, "callback").build();
    }

    public static URI socialRedirectToProviderAuth(URI baseUri, String realmId) {
        return socialBase(baseUri).path(SocialResource.class, "redirectToProviderAuth")
                .build(realmId);
    }

    private static UriBuilder requiredActionsBase(URI baseUri) {
        return tokenBase(baseUri).path(TokenService.class, "getRequiredActionsService");
    }

    private static UriBuilder tokenBase(URI baseUri) {
        return realmBase(baseUri).path(RealmsResource.class, "getTokenService");
    }
}
