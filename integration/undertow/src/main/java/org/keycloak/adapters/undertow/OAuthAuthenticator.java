package org.keycloak.adapters.undertow;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.Headers;
import org.jboss.logging.Logger;
import org.keycloak.RSATokenVerifier;
import org.keycloak.RealmConfiguration;
import org.keycloak.VerificationException;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.SkeletonKeyToken;
import org.keycloak.representations.idm.CredentialRepresentation;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OAuthAuthenticator {
    private static final Logger log = Logger.getLogger(OAuthAuthenticator.class);
    protected RealmConfiguration realmInfo;
    protected int sslRedirectPort;
    protected String tokenString;
    protected SkeletonKeyToken token;
    protected HttpServerExchange exchange;
    protected String redirectUri;
    protected KeycloakChallenge challenge;

    public OAuthAuthenticator(HttpServerExchange exchange, RealmConfiguration realmInfo,  int sslRedirectPort) {
        this.exchange = exchange;
        this.realmInfo = realmInfo;
        this.sslRedirectPort = sslRedirectPort;
    }

    public KeycloakChallenge getChallenge() {
        return challenge;
    }

    public String getTokenString() {
        return tokenString;
    }

    public SkeletonKeyToken getToken() {
        return token;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    protected String getRequestUrl() {
        UriBuilder uriBuilder = UriBuilder.fromUri(exchange.getRequestURI())
                .replaceQuery(exchange.getQueryString());
        if (!exchange.isHostIncludedInRequestURI()) uriBuilder.scheme(exchange.getRequestScheme()).host(exchange.getHostAndPort());
        return uriBuilder.build().toString();
    }

    protected boolean isRequestSecure() {
        return exchange.getProtocol().toString().equalsIgnoreCase("https");
    }

    protected Cookie getCookie(String cookieName) {
        Map<String, Cookie> requestCookies = exchange.getRequestCookies();
        if (requestCookies == null) return null;
        return requestCookies.get(cookieName);
    }

    protected String getCookieValue(String cookieName) {
        Cookie cookie = getCookie(cookieName);
        if (cookie == null) return null;
        return cookie.getValue();
    }

    protected String getQueryParamValue(String paramName) {
        Map<String,Deque<String>> queryParameters = exchange.getQueryParameters();
        if (queryParameters == null) return null;
        Deque<String> strings = queryParameters.get(paramName);
        if (strings == null) return null;
        return strings.getFirst();
    }

    protected String getError() {
        return getQueryParamValue("error");
    }

    protected String getCode() {
        return getQueryParamValue("code");
    }

    protected String getRedirectUri(String state) {
        String url = getRequestUrl();
        log.info("sending redirect uri: " + url);
        if (!isRequestSecure() && realmInfo.isSslRequired()) {
            int port = sslRedirectPort();
            if (port < 0) {
                // disabled?
                return null;
            }
            UriBuilder secureUrl = UriBuilder.fromUri(url).scheme("https").port(-1);
            if (port != 443) secureUrl.port(port);
            url = secureUrl.build().toString();
        }
        return realmInfo.getAuthUrl().clone()
                .queryParam("client_id", realmInfo.getMetadata().getResourceName())
                .queryParam("redirect_uri", url)
                .queryParam("state", state)
                .queryParam("login", "true")
                .build().toString();
    }

    protected int sslRedirectPort() {
        return sslRedirectPort;
    }

    protected static final AtomicLong counter = new AtomicLong();

    protected String getStateCode() {
        return counter.getAndIncrement() + "/" + UUID.randomUUID().toString();
    }

    protected KeycloakChallenge loginRedirect() {
        final String state = getStateCode();
        final String redirect = getRedirectUri(state);
        return new KeycloakChallenge() {
            @Override
            public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
                if (redirect == null) {
                    return new AuthenticationMechanism.ChallengeResult(true, 403);
                }
                CookieImpl cookie = new CookieImpl(realmInfo.getStateCookieName(), state);
                //cookie.setPath(getDefaultCookiePath()); todo I don't think we need to set state cookie path as it will be the same redirect
                cookie.setSecure(realmInfo.isSslRequired());
                exchange.setResponseCookie(cookie);
                exchange.getResponseHeaders().put(Headers.LOCATION, redirect);
                return new AuthenticationMechanism.ChallengeResult(true, 302);
            }
        };
    }

    protected KeycloakChallenge checkStateCookie() {
        Cookie stateCookie = getCookie(realmInfo.getStateCookieName());

        if (stateCookie == null) {
            log.warn("No state cookie");
            return challenge(400);
        }
        // reset the cookie
        log.info("** reseting application state cookie");
        Cookie reset = new CookieImpl(realmInfo.getStateCookieName(), "");
        reset.setPath(stateCookie.getPath());
        reset.setMaxAge(0);
        exchange.setResponseCookie(reset);

        String stateCookieValue = getCookieValue(realmInfo.getStateCookieName());

        String state = getQueryParamValue("state");
        if (state == null) {
            log.warn("state parameter was null");
            return challenge(400);
        }
        if (!state.equals(stateCookieValue)) {
            log.warn("state parameter invalid");
            log.warn("cookie: " + stateCookieValue);
            log.warn("queryParam: " + state);
            return challenge(400);
        }
        return null;

    }

    public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate() {
        String code = getCode();
        if (code == null) {
            log.info("there was no code");
            String error = getError();
            if (error != null) {
                // todo how do we send a response?
                log.warn("There was an error: " + error);
                challenge = challenge(400);
                return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            } else {
                log.info("redirecting to auth server");
                challenge = loginRedirect();
                return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
            }
        } else {
            log.info("there was a code, resolving");
            challenge = resolveCode(code);
            if (challenge != null) {
                return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
            return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
        }

    }

    protected KeycloakChallenge challenge(final int code) {
        return new KeycloakChallenge() {
            @Override
            public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange httpServerExchange, SecurityContext securityContext) {
                return new AuthenticationMechanism.ChallengeResult(true, code);
            }
        };
    }

    /**
     * Start or continue the oauth login process.
     * <p/>
     * if code query parameter is not present, then browser is redirected to authUrl.  The redirect URL will be
     * the URL of the current request.
     * <p/>
     * If code query parameter is present, then an access token is obtained by invoking a secure request to the codeUrl.
     * If the access token is obtained, the browser is again redirected to the current request URL, but any OAuth
     * protocol specific query parameters are removed.
     *
     * @return null if an access token was obtained, otherwise a challenge is returned
     */
    protected KeycloakChallenge resolveCode(String code) {
        // abort if not HTTPS
        if (realmInfo.isSslRequired() && !isRequestSecure()) {
            log.error("SSL is required");
            return challenge(403);
        }

        log.info("checking state cookie for after code");
        KeycloakChallenge challenge = checkStateCookie();
        if (challenge != null) return challenge;

        String client_id = realmInfo.getMetadata().getResourceName();
        String password = realmInfo.getResourceCredentials().asMap().getFirst("password");
        //String authHeader = BasicAuthHelper.createHeader(client_id, password);
        redirectUri = stripOauthParametersFromRedirect();
        Form form = new Form();
        form.param("grant_type", "authorization_code")
                .param("code", code)
                .param("client_id", client_id)
                .param(CredentialRepresentation.PASSWORD, password)
                .param("redirect_uri", redirectUri);

        Response res = realmInfo.getCodeUrl().request()
                .post(Entity.form(form));
        AccessTokenResponse tokenResponse;
        try {
            if (res.getStatus() != 200) {
                log.error("failed to turn code into token");
                log.error("status from server: " + res.getStatus());
                if (res.getStatus() == 400 && res.getMediaType() != null) {
                    log.error("   " + res.readEntity(String.class));
                }
                return challenge(403);
            }
            log.debug("media type: " + res.getMediaType());
            log.debug("Content-Type header: " + res.getHeaderString("Content-Type"));
            tokenResponse = res.readEntity(AccessTokenResponse.class);
        } finally {
            res.close();
        }

        tokenString = tokenResponse.getToken();
        try {
            token = RSATokenVerifier.verifyToken(tokenString, realmInfo.getMetadata());
            log.debug("Token Verification succeeded!");
        } catch (VerificationException e) {
            log.error("failed verification of token");
            return challenge(403);
        }
        log.info("successful authenticated");
        return null;
    }

    /**
     * strip out unwanted query parameters and redirect so bookmarks don't retain oauth protocol bits
     */
    protected String stripOauthParametersFromRedirect() {
        UriBuilder builder = UriBuilder.fromUri(exchange.getRequestURI())
                .replaceQuery(exchange.getQueryString())
                .replaceQueryParam("code", null)
                .replaceQueryParam("state", null);
        return builder.build().toString();
    }


}
