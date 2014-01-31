package org.keycloak.login.freemarker;

import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.freemarker.FreeMarkerUtil;
import org.keycloak.freemarker.Theme;
import org.keycloak.freemarker.ThemeLoader;
import org.keycloak.login.Forms;
import org.keycloak.login.FormsPages;
import org.keycloak.login.freemarker.model.LoginBean;
import org.keycloak.login.freemarker.model.MessageBean;
import org.keycloak.login.freemarker.model.OAuthGrantBean;
import org.keycloak.login.freemarker.model.ProfileBean;
import org.keycloak.login.freemarker.model.RealmBean;
import org.keycloak.login.freemarker.model.RegisterBean;
import org.keycloak.login.freemarker.model.SocialBean;
import org.keycloak.login.freemarker.model.TotpBean;
import org.keycloak.login.freemarker.model.UrlBean;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.email.EmailException;
import org.keycloak.services.email.EmailSender;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerForms implements Forms {

    private static final String BUNDLE = "theme.login.base.messages.messages";

    private String message;
    private String accessCodeId;
    private String accessCode;
    private Response.Status status = Response.Status.OK;
    private List<RoleModel> realmRolesRequested;
    private MultivaluedMap<String, RoleModel> resourceRolesRequested;

    public static enum MessageType {SUCCESS, WARNING, ERROR}

    private MessageType messageType = MessageType.ERROR;

    private MultivaluedMap<String, String> formData;

    private RealmModel realm;

    // TODO Remove
    private HttpRequest request;

    private UserModel user;

    private UserModel client;

    private UriInfo uriInfo;

    FreeMarkerForms(RealmModel realm, org.jboss.resteasy.spi.HttpRequest request, UriInfo uriInfo) {
        this.realm = realm;
        this.request = request;
        this.uriInfo = uriInfo;
    }

    public Response createResponse(UserModel.RequiredAction action) {
        String actionMessage;
        FormsPages page;

        switch (action) {
            case CONFIGURE_TOTP:
                actionMessage = Messages.ACTION_WARN_TOTP;
                page = FormsPages.LOGIN_CONFIG_TOTP;
                break;
            case UPDATE_PROFILE:
                actionMessage = Messages.ACTION_WARN_PROFILE;
                page = FormsPages.LOGIN_UPDATE_PROFILE;
                break;
            case UPDATE_PASSWORD:
                actionMessage = Messages.ACTION_WARN_PASSWD;
                page = FormsPages.LOGIN_UPDATE_PASSWORD;
                break;
            case VERIFY_EMAIL:
                try {
                    new EmailSender(realm.getSmtpConfig()).sendEmailVerification(user, realm, accessCodeId, uriInfo);
                } catch (EmailException e) {
                    return setError("emailSendError").createErrorPage();
                }

                actionMessage = Messages.ACTION_WARN_EMAIL;
                page = FormsPages.LOGIN_VERIFY_EMAIL;
                break;
            default:
                return Response.serverError().build();
        }

        if (message == null) {
            setWarning(actionMessage);
        }

        return createResponse(page);
    }

    private Response createResponse(FormsPages page) {
        MultivaluedMap<String, String> queryParameterMap = uriInfo.getQueryParameters();

        String requestURI = uriInfo.getBaseUri().getPath();
        UriBuilder uriBuilder = UriBuilder.fromUri(requestURI);

        for (String k : queryParameterMap.keySet()) {
            uriBuilder.replaceQueryParam(k, queryParameterMap.get(k).toArray());
        }

        if (accessCode != null) {
            uriBuilder.replaceQueryParam("code", accessCode);
        }

        Map<String, Object> attributes = new HashMap<String, Object>();

        ResourceBundle rb = ResourceBundle.getBundle(BUNDLE);

        attributes.put("rb", rb);

        if (message != null) {
            attributes.put("message", new MessageBean(rb.containsKey(message) ? rb.getString(message) : message, messageType));
        }

        Theme theme = ThemeLoader.createTheme(realm.getLoginTheme(), Theme.Type.LOGIN);

        URI baseUri = uriBuilder.build();

        if (realm != null) {
            attributes.put("realm", new RealmBean(realm));
            attributes.put("social", new SocialBean(realm, baseUri));
            attributes.put("url", new UrlBean(realm, theme, baseUri));
        }

        attributes.put("login", new LoginBean(formData));

        switch (page) {
            case LOGIN_CONFIG_TOTP:
                attributes.put("totp", new TotpBean(user, baseUri));
                break;
            case LOGIN_UPDATE_PROFILE:
                attributes.put("user", new ProfileBean(user));
                break;
            case REGISTER:
                attributes.put("register", new RegisterBean(formData));
                break;
            case OAUTH_GRANT:
                attributes.put("oauth", new OAuthGrantBean(accessCode, client, realmRolesRequested, resourceRolesRequested));
                break;
        }


        String result = FreeMarkerUtil.processTemplate(attributes, Templates.getTemplate(page), theme);
        return Response.status(status).type(MediaType.TEXT_HTML).entity(result).build();
    }

    public Response createLogin() {
        return createResponse(FormsPages.LOGIN);
    }

    public Response createPasswordReset() {
        return createResponse(FormsPages.LOGIN_RESET_PASSWORD);
    }

    public Response createUsernameReminder() {
        return createResponse(FormsPages.LOGIN_USERNAME_REMINDER);
    }

    public Response createLoginTotp() {
        return createResponse(FormsPages.LOGIN_TOTP);
    }

    public Response createRegistration() {
        return createResponse(FormsPages.REGISTER);
    }

    public Response createErrorPage() {
        setStatus(Response.Status.INTERNAL_SERVER_ERROR);
        return createResponse(FormsPages.ERROR);
    }

    public Response createOAuthGrant() {
        return createResponse(FormsPages.OAUTH_GRANT);
    }

    public FreeMarkerForms setError(String message) {
        this.message = message;
        this.messageType = MessageType.ERROR;
        return this;
    }

    public FreeMarkerForms setSuccess(String message) {
        this.message = message;
        this.messageType = MessageType.SUCCESS;
        return this;
    }

    public FreeMarkerForms setWarning(String message) {
        this.message = message;
        this.messageType = MessageType.WARNING;
        return this;
    }

    public FreeMarkerForms setUser(UserModel user) {
        this.user = user;
        return this;
    }

    public FreeMarkerForms setClient(UserModel client) {
        this.client = client;
        return this;
    }

    public FreeMarkerForms setFormData(MultivaluedMap<String, String> formData) {
        this.formData = formData;
        return this;
    }

    @Override
    public Forms setAccessCode(String accessCodeId, String accessCode) {
        this.accessCodeId = accessCodeId;
        this.accessCode = accessCode;
        return this;
    }

    @Override
    public Forms setAccessRequest(List<RoleModel> realmRolesRequested, MultivaluedMap<String, RoleModel> resourceRolesRequested) {
        this.realmRolesRequested = realmRolesRequested;
        this.resourceRolesRequested = resourceRolesRequested;
        return this;
    }

    @Override
    public Forms setStatus(Response.Status status) {
        this.status = status;
        return this;
    }

}
