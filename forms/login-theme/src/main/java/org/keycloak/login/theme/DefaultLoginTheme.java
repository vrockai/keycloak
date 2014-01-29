package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;

import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DefaultLoginTheme implements Theme {

    public static final String NAME = "default";

    private static final String ROOT = "theme/login/default/";

    private static final String RESOURCE_ROOT = ROOT + "resources/";
    @Override
    public String getName() {
        return NAME;
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
    public URL getTemplate(String name) {
        return getClass().getClassLoader().getResource(ROOT + name);
    }

    @Override
    public InputStream getTemplateAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(ROOT + name);
    }

    @Override
    public URL getResource(String path) {
        return getClass().getClassLoader().getResource(RESOURCE_ROOT + path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(RESOURCE_ROOT + path);
    }

}
