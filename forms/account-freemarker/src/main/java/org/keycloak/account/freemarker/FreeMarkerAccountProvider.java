package org.keycloak.account.freemarker;

import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.account.Account;
import org.keycloak.account.AccountProvider;
import org.keycloak.models.RealmModel;

import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerAccountProvider implements AccountProvider {

    @Override
    public Account createAccount(UriInfo uriInfo) {
        return new FreeMarkerAccount(uriInfo);
    }

}
