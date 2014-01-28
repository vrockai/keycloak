package org.keycloak.forms;

import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public interface Forms {

    public Response createResponse(UserModel.RequiredAction action);

    public Response createLogin();

    public Response createPasswordReset();

    public Response createUsernameReminder();

    public Response createLoginTotp();

    public Response createRegistration();

    public Response createErrorPage();

    public Response createOAuthGrant();

    public Forms setAccessCode(String accessCodeId, String accessCode);

    public Forms setError(String message);

    public Forms setSuccess(String message);

    public Forms setWarning(String message);

    public Forms setUser(UserModel user);

    public Forms setClient(UserModel client);

    public Forms setFormData(MultivaluedMap<String, String> formData);

    public Forms setStatus(Response.Status status);
    
}
