package org.keycloak.freemarker;

import java.io.InputStream;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public interface Theme {

    public enum Type { LOGIN, ACCOUNT };

    public String getName();

    public String getParentName();

    public Type getType();

    public InputStream getTemplate(String name);

    public InputStream getResource(String path);

}
