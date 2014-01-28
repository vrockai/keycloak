package org.keycloak.account.freemarker;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.account.Account;
import org.keycloak.account.AccountLoader;
import org.keycloak.account.AccountPages;
import org.keycloak.models.ApplicationModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerAccountTest {

    private Account account;
    private UserModel user;
    private ApplicationModel app;
    private RealmModel realm;
    private UriInfo uriInfo;

    @Before
    public void before() throws URISyntaxException {
        uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getBaseUri()).andReturn(new URI("http://localhost:8080/auth/rest/")).anyTimes();

        user = EasyMock.createMock(UserModel.class);
        EasyMock.expect(user.getLoginName()).andReturn("login-name").anyTimes();
        EasyMock.expect(user.getFirstName()).andReturn("First").anyTimes();
        EasyMock.expect(user.getLastName()).andReturn("Last").anyTimes();
        EasyMock.expect(user.getEmail()).andReturn("email@address.com").anyTimes();
        EasyMock.expect(user.isTotp()).andReturn(false).anyTimes();

        app = EasyMock.createMock(ApplicationModel.class);
        EasyMock.expect(app.getName()).andReturn("myapp").anyTimes();
        EasyMock.expect(app.getBaseUrl()).andReturn("http://localhost/myapp").anyTimes();

        realm = EasyMock.createMock(RealmModel.class);
        EasyMock.expect(realm.getName()).andReturn("myrealm").anyTimes();
        EasyMock.expect(realm.getApplications()).andReturn(Collections.singletonList(app)).anyTimes();

        account = AccountLoader.load().createAccount(uriInfo).setRealm(realm).setUser(user);
    }

    private void replay() {
        EasyMock.replay(uriInfo, realm, app, user);
    }

    @Test
    public void testAccount() {
        replay();

        Response response = account.createResponse(AccountPages.ACCOUNT);
        assertEquals(200, response.getStatus());

        String page = (String) response.getEntity();

        assertCommon(page);

        assertThat(page, containsString("<label for=\"username\">Username</label>"));
        assertThat(page, containsString("<input type=\"text\" id=\"username\" name=\"username\" disabled=\"disabled\" value=\"login-name\"/>"));

        assertThat(page, containsString("<label for=\"email\">Email</label>"));
        assertThat(page, containsString("<input type=\"text\" id=\"email\" name=\"email\" autofocus value=\"email@address.com\"/>"));

        assertThat(page, containsString("<label for=\"lastName\">Last name</label>"));
        assertThat(page, containsString("<input type=\"text\" id=\"lastName\" name=\"lastName\" value=\"Last\"/>"));

        assertThat(page, containsString("<label for=\"firstName\">First name</label>"));
        assertThat(page, containsString("<input type=\"text\" id=\"firstName\" name=\"firstName\" value=\"First\"/>"));
    }

    @Test
    public void testPassword() {
        replay();

        Response response = account.createResponse(AccountPages.PASSWORD);
        assertEquals(200, response.getStatus());

        String page = (String) response.getEntity();

        assertCommon(page);

        assertThat(page, containsString("<label for=\"password\">Password</label>"));
        assertThat(page, containsString("<label for=\"password-new\">New Password</label>"));
        assertThat(page, containsString("<label for=\"password-confirm\" class=\"two-lines\">Password confirmation</label>"));
    }

    @Test
    public void testTotpNotConfigured() {
        replay();

        Response response = account.createResponse(AccountPages.TOTP);
        assertEquals(200, response.getStatus());

        String page = (String) response.getEntity();

        assertCommon(page);

        assertThat(page, containsString("Google Authenticator Setup"));
        assertThat(page, containsString("<label for=\"totp\">One-time-password</label>"));
        assertThat(page, containsString("<input type=\"text\" id=\"totp\" name=\"totp\" />"));
        assertThat(page, containsString("<input type=\"hidden\" id=\"totpSecret\" name=\"totpSecret\" value=\""));

        assertThat(page, not(containsString("You have the following authenticators set up")));
    }

    @Test
    public void testTotpConfigured() {
        EasyMock.reset(user);
        EasyMock.expect(user.isTotp()).andReturn(true).anyTimes();

        replay();

        Response response = account.createResponse(AccountPages.TOTP);
        assertEquals(200, response.getStatus());

        String page = (String) response.getEntity();

        assertCommon(page);

        assertThat(page, containsString("You have the following authenticators set up"));
        assertThat(page, containsString("Google"));
        assertThat(page, containsString("Remove Google"));

        assertThat(page, not(containsString("Google Authenticator Setup")));
    }

    @Test
    public void testWithReferrerURI() {
        replay();

        for (AccountPages p : AccountPages.values()) {
            Response response = account.setReferrer("myapp").createResponse(p);
            assertEquals(200, response.getStatus());

            String page = (String) response.getEntity();

            assertThat(page, containsString("<a href=\"http://localhost/myapp\">Back to application</a>"));
        }
    }

    private void assertCommon(String page) {
        assertThat(page, containsString("href=\"/auth/account/theme/default/css/base.css\""));
        assertThat(page, not(containsString("Back to application")));
    }

}
