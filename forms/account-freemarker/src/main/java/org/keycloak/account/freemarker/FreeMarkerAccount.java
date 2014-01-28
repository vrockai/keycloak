package org.keycloak.account.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.keycloak.account.Account;
import org.keycloak.account.AccountPages;
import org.keycloak.account.freemarker.model.AccountBean;
import org.keycloak.account.freemarker.model.MessageBean;
import org.keycloak.account.freemarker.model.TotpBean;
import org.keycloak.account.freemarker.model.UrlBean;
import org.keycloak.models.ApplicationModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
public class FreeMarkerAccount implements Account {

    private static final String BUNDLE = "account.theme.default.messages.messages";

    private UserModel user;
    private Response.Status status = Response.Status.OK;
    private RealmModel realm;
    private String referrer;

    public static enum MessageType {SUCCESS, WARNING, ERROR}

    private UriInfo uriInfo;

    private String message;
    private MessageType messageType;

    public FreeMarkerAccount(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response createResponse(AccountPages page) {
        Map<String, Object> attributes = new HashMap<String, Object>();

        String realmName = realm != null ? realm.getName() : null;
        ResourceBundle rb = ResourceBundle.getBundle(BUNDLE);
        URI baseUri = uriInfo.getBaseUri();

        String resourcePath = baseUri.getPath();
        resourcePath = resourcePath.substring(0, resourcePath.length() - 6);
        resourcePath += "/account/theme/default";

        attributes.put("rb", rb);

        if (message != null) {
            attributes.put("message", new MessageBean(rb.containsKey(message) ? rb.getString(message) : message, messageType));
        }

        attributes.put("resourcePath", resourcePath);

        attributes.put("url", new UrlBean(realmName, baseUri, getReferrerUri()));

        switch (page) {
            case ACCOUNT:
                attributes.put("account", new AccountBean(user));
                break;
            case TOTP:
                attributes.put("totp", new TotpBean(user, baseUri));
                break;
        }

        String result = processTemplate(attributes, page);
        return Response.status(status).type(MediaType.TEXT_HTML).entity(result).build();
    }

    private String getReferrerUri() {
        if (referrer != null) {
            for (ApplicationModel a : realm.getApplications()) {
                if (a.getName().equals(referrer)) {
                    return a.getBaseUrl();
                }
            }
        }
        return null;
    }

    private String processTemplate(Object data, AccountPages page) {
        Writer out = new StringWriter();
        Configuration cfg = new Configuration();

        try {
            cfg.setClassForTemplateLoading(FreeMarkerAccount.class, "/account/theme/default");
            Template template = cfg.getTemplate(Templates.getTemplate(page));

            template.process(data, out);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO Error handling
        } catch (TemplateException e) {
            throw new RuntimeException(e); // TODO Error handling
        }

        return out.toString();
    }

    @Override
    public Account setError(String message) {
        this.message = message;
        this.messageType = MessageType.ERROR;
        return this;
    }

    @Override
    public Account setSuccess(String message) {
        this.message = message;
        this.messageType = MessageType.SUCCESS;
        return this;
    }

    @Override
    public Account setWarning(String message) {
        this.message = message;
        this.messageType = MessageType.WARNING;
        return this;
    }

    @Override
    public Account setUser(UserModel user) {
        this.user = user;
        return this;
    }

    @Override
    public Account setRealm(RealmModel realm) {
        this.realm = realm;
        return this;
    }

    @Override
    public Account setStatus(Response.Status status) {
        this.status = status;
        return this;
    }

    @Override
    public Account setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }
}
