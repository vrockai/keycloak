package org.keycloak.freemarker;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreeMarkerUtil {

    public static String processTemplate(Object data, String templateName, Theme theme) {
        Writer out = new StringWriter();
        Configuration cfg = new Configuration();

        try {
            cfg.setTemplateLoader(new ThemeTemplateLoader(theme));
            Template template = cfg.getTemplate(templateName);

            template.process(data, out);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO Error handling
        } catch (TemplateException e) {
            throw new RuntimeException(e); // TODO Error handling
        }

        return out.toString();
    }

    public static class ThemeTemplateLoader extends URLTemplateLoader {

        private Theme theme;

        public ThemeTemplateLoader(Theme theme) {
            this.theme = theme;
        }

        @Override
        protected URL getURL(String name) {
            try {
                return theme.getTemplate(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
