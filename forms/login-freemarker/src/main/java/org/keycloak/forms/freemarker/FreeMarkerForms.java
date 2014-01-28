package org.keycloak.forms.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.forms.Forms;
import org.keycloak.forms.FormsPages;
import org.keycloak.forms.freemarker.model.RegisterBean;
import org.keycloak.forms.freemarker.model.SocialBean;
import org.keycloak.forms.freemarker.model.TotpBean;
import org.keycloak.forms.freemarker.model.LoginBean;
import org.keycloak.forms.freemarker.model.RealmBean;
import org.keycloak.forms.freemarker.model.MessageBean;
import org.keycloak.forms.freemarker.model.UrlBean;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.email.EmailException;
import org.keycloak.services.email.EmailSender;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerForms implements Forms {

    private static final String BUNDLE = "login.theme.default.messages.messages";

    private String message;
    private String accessCodeId;
    private String accessCode;
    private Response.Status status = Response.Status.OK;

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
        switch (action) {
            case CONFIGURE_TOTP:
                return setWarning(Messages.ACTION_WARN_TOTP).createResponse(FormsPages.LOGIN_CONFIG_TOTP);
            case UPDATE_PROFILE:
                return setWarning(Messages.ACTION_WARN_PROFILE).createResponse(FormsPages.LOGIN_UPDATE_PROFILE);
            case UPDATE_PASSWORD:
                return setWarning(Messages.ACTION_WARN_PASSWD).createResponse(FormsPages.LOGIN_UPDATE_PASSWORD);
            case VERIFY_EMAIL:
                try {
                    new EmailSender(realm.getSmtpConfig()).sendEmailVerification(user, realm, accessCodeId, uriInfo);
                } catch (EmailException e) {
                    return setError("emailSendError").createErrorPage();
                }
                return setWarning(Messages.ACTION_WARN_EMAIL).createResponse(FormsPages.LOGIN_VERIFY_EMAIL);
            default:
                return Response.serverError().build();
        }
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

        String resourcePath = uriInfo.getBaseUri().getPath();
        resourcePath = resourcePath.substring(0, resourcePath.length() - 6);
        resourcePath += "/login/theme/default";

        attributes.put("rb", rb);

        if (message != null) {
            attributes.put("message", new MessageBean(rb.containsKey(message) ? rb.getString(message) : message, messageType));
        }

        attributes.put("resourcePath", resourcePath);

        URI baseUri = uriBuilder.build();

        if (realm != null) {
            attributes.put("realm", new RealmBean(realm));
            attributes.put("social", new SocialBean(realm, baseUri));
            attributes.put("url", new UrlBean(realm.getName(), baseUri));
        }

        attributes.put("login", new LoginBean(formData));

        switch (page) {
            case LOGIN_CONFIG_TOTP:
                attributes.put("totp", new TotpBean(user, baseUri));
                break;
            case REGISTER:
                attributes.put("register", new RegisterBean(formData));
                break;
            case OAUTH_GRANT:
                break;
        }
        // TODO Data for pages



        String result = processTemplate(attributes, page);
        return Response.status(status).type(MediaType.TEXT_HTML).entity(result).build();
    }

    private String processTemplate(Object data, FormsPages page) {
        Writer out = new StringWriter();
        Configuration cfg = new Configuration();

        try {
            cfg.setClassForTemplateLoading(FreeMarkerForms.class, "/login/theme/default");
            Template template = cfg.getTemplate(Templates.getTemplate(page));

            template.process(data, out);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO Error handling
        } catch (TemplateException e) {
            throw new RuntimeException(e); // TODO Error handling
        }

        return out.toString();
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
        // TODO Pass missing oauth grant details
//        FormServiceDataBean formDataBean = new FormServiceDataBean(realm, user, formData, null, message);
//
//        formDataBean.setOAuthRealmRolesRequested((List<RoleModel>) request.getAttribute("realmRolesRequested"));
//        formDataBean.setOAuthResourceRolesRequested((MultivaluedMap<String, RoleModel>) request.getAttribute("resourceRolesRequested"));
//        formDataBean.setOAuthCode(accessCode);
//        formDataBean.setOAuthAction((String) request.getAttribute("action"));

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
    public Forms setStatus(Response.Status status) {
        this.status = status;
        return this;
    }

}
