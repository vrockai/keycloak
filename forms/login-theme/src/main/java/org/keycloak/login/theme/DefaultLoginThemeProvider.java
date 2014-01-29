package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;
import org.keycloak.freemarker.ThemeProvider;

import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DefaultLoginThemeProvider implements ThemeProvider {

    @Override
    public Theme createTheme(String name, Theme.Type type) {
        if (type == Theme.Type.LOGIN && "default".equals(name)) {
              return new DefaultLoginTheme();
        }
        return null;
    }

    @Override
    public Set<String> nameSet(Theme.Type type) {
        if (type == Theme.Type.LOGIN) {
            return Collections.singleton("default");
        }
        return Collections.emptySet();
    }

    @Override
    public boolean hasTheme(String name, Theme.Type type) {
        return type == Theme.Type.LOGIN && "default".equals(name);
    }

}
