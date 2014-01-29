package org.keycloak.freemarker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public interface Theme {

    public enum Type { LOGIN, ACCOUNT };

    public String getName();

    public String getParentName();

    public Type getType();

    public URL getTemplate(String name) throws IOException;

    public InputStream getTemplateAsStream(String name) throws IOException;

    public URL getResource(String path) throws IOException;

    public InputStream getResourceAsStream(String path) throws IOException;

}
