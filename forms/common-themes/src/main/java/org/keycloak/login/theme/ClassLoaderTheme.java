package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;

import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ClassLoaderTheme implements Theme {

    private final String name;

    private final String parentName;

    private final Type type;

    private final String templateRoot;

    private final String resourceRoot;

    public ClassLoaderTheme(String name, String parentName, Type type) {
        this.name = name;
        this.parentName = parentName;
        this.type = type;
        this.templateRoot = "theme/" + type.toString().toLowerCase() + "/" + name + "/";
        this.resourceRoot = "theme/" + type.toString().toLowerCase() + "/" + name + "/resources/";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParentName() {
        return parentName;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public URL getTemplate(String name) {
        return getClass().getClassLoader().getResource(templateRoot + name);
    }

    @Override
    public InputStream getTemplateAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(templateRoot + name);
    }

    @Override
    public URL getResource(String path) {
        return getClass().getClassLoader().getResource(resourceRoot + path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(resourceRoot + path);
    }

}
