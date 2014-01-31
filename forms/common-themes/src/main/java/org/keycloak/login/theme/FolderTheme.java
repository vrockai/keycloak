package org.keycloak.login.theme;

import org.keycloak.freemarker.Theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FolderTheme implements Theme {

    private String parentName;
    private File themeDir;
    private Type type;

    public FolderTheme(File themeDir, Type type) throws IOException {
        this.themeDir = themeDir;
        this.type = type;

        File propertiesFile = new File(themeDir, "theme.properties");
        if (propertiesFile .isFile()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            parentName = properties.getProperty("parent");
        }
    }

    @Override
    public String getName() {
        return themeDir.getName();
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
    public URL getMessages() throws IOException {
        File file = new File(themeDir, "messages" + File.separator + "messages.properties");
        return file.isFile() ? file.toURI().toURL() : null;
    }

    @Override
    public InputStream getMessagesAsStream() throws IOException {
        URL url = getMessages();
        return url != null ? url.openStream() : null;
    }

    @Override
    public URL getTemplate(String name) throws IOException {
        File file = new File(themeDir, name);
        return file.isFile() ? file.toURI().toURL() : null;
    }

    @Override
    public InputStream getTemplateAsStream(String name) throws IOException {
        URL url = getTemplate(name);
        return url != null ? url.openStream() : null;
    }

    @Override
    public URL getResource(String path) throws IOException {
        if (File.separatorChar != '/') {
            path = path.replace('/', File.separatorChar);
        }
        File file = new File(themeDir, "/resources/" + path);
        return file.isFile() ? file.toURI().toURL() : null;
    }

    @Override
    public InputStream getResourceAsStream(String path) throws IOException {
        URL url = getResource(path);
        return url != null ? url.openStream() : null;
    }
}
