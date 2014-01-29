package org.keycloak.freemarker;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ThemeLoader {

    public static Theme createTheme(String name, Theme.Type type) {
        ServiceLoader<ThemeProvider> providers = ServiceLoader.load(ThemeProvider.class);

        Theme theme = findTheme(providers, name, type);
        if (theme.getParentName() != null) {
            List<Theme> themes = new LinkedList<Theme>();
            themes.add(theme);

            for (String parentName = theme.getParentName(); parentName != null; parentName = theme.getParentName()) {
                theme = findTheme(providers, parentName, type);
                themes.add(theme);
            }

            return new ExtendingTheme(themes);
        } else {
            return theme;
        }
    }

    private static Theme findTheme(ServiceLoader<ThemeProvider> providers, String name, Theme.Type type) {
        for (ThemeProvider p : providers) {
            if (p.hasTheme(name, type)) {
                return p.createTheme(name, type);
            }
        }

        throw new RuntimeException("Theme " + name + " (type: " + type + ") not found");
    }

    public static class ExtendingTheme implements Theme {

        private List<Theme> themes;

        public ExtendingTheme(List<Theme> themes) {
            this.themes = themes;
        }

        @Override
        public String getName() {
            return themes.get(0).getName();
        }

        @Override
        public String getParentName() {
            return themes.get(0).getParentName();
        }

        @Override
        public Type getType() {
            return themes.get(0).getType();
        }

        @Override
        public InputStream getTemplate(String name) {
            for (Theme t : themes) {
                InputStream template = t.getTemplate(name);
                if (template != null) {
                    return template;
                }
            }
            return null;
        }

        @Override
        public InputStream getResource(String path) {
            for (Theme t : themes) {
                InputStream resource = t.getResource(path);
                if (resource != null) {
                    return resource;
                }
            }
            return null;
        }
    }

}
