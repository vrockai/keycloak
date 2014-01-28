package org.keycloak.login;

import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.models.RealmModel;

import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public interface FormsProvider {

    public Forms createForms(RealmModel realm, HttpRequest request, UriInfo uriInfo);

}
