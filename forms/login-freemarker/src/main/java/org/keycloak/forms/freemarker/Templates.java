package org.keycloak.forms.freemarker;

import org.keycloak.forms.FormsPages;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class Templates {

    public static String getTemplate(FormsPages page) {
        switch (page) {
            case LOGIN:
                return "login.ftl";
            case LOGIN_TOTP:
                return "login-totp.ftl";
            case LOGIN_CONFIG_TOTP:
                return "login-config-totp.ftl";
            case LOGIN_VERIFY_EMAIL:
                return "login-verify-email.ftl";
            case OAUTH_GRANT:
                return "login-oauth-grant.ftl";
            case LOGIN_RESET_PASSWORD:
                return "login-reset-password.ftl";
            case LOGIN_UPDATE_PASSWORD:
                return "login-update-password.ftl";
            case LOGIN_USERNAME_REMINDER:
                return "login-username-reminder.ftl";
            case REGISTER:
                return "register.ftl";
            case ERROR:
                return "error.ftl";
            case LOGIN_UPDATE_PROFILE:
                return "login-update-profile.ftl";
            default:
                throw new IllegalArgumentException();
        }
    }

}
