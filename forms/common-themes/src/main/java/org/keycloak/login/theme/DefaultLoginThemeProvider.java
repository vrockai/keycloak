package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;
import org.keycloak.freemarker.ThemeProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DefaultLoginThemeProvider implements ThemeProvider {

    private static Set<String> defaultThemes = new HashSet<String>();

    static {
        defaultThemes.add("rcue");
        defaultThemes.add("keycloak");
    }

    @Override
    public Theme createTheme(String name, Theme.Type type) {
        if (hasTheme(name, type)) {
            String parentName = "keycloak".equals(name) ? "rcue" : null;
            return new ClassLoaderTheme(name, parentName, type);
        } else {
            return null;
        }
    }

    @Override
    public Set<String> nameSet(Theme.Type type) {
        if (type == Theme.Type.LOGIN) {
            return defaultThemes;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasTheme(String name, Theme.Type type) {
        return nameSet(type).contains(name);
    }

}
