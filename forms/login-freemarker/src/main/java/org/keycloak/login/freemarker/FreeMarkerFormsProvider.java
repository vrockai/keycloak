package org.keycloak.login.freemarker;

import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.login.Forms;
import org.keycloak.login.FormsProvider;
import org.keycloak.models.RealmModel;

import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerFormsProvider implements FormsProvider {

    @Override
    public Forms createForms(RealmModel realm, HttpRequest request, UriInfo uriInfo) {
        return new FreeMarkerForms(realm, request, uriInfo);
    }

}
