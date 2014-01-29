package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;

import java.io.InputStream;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DefaultLoginTheme implements Theme {

    private static final String ROOT = "login/theme/default/";

    private static final String RESOURCE_ROOT = ROOT + "resources/";

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public Theme.Type getType() {
        return Type.LOGIN;
    }

    @Override
    public InputStream getTemplate(String name) {
        return getClass().getClassLoader().getResourceAsStream(ROOT + name);
    }

    @Override
    public InputStream getResource(String path) {
        return getClass().getClassLoader().getResourceAsStream(RESOURCE_ROOT + path);
    }

}
