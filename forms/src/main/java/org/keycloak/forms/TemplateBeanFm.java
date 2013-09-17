package org.keycloak.forms;

import java.util.HashMap;
import java.util.Map;


public class TemplateBeanFm {

    private RealmBeanFm realm;

    private String theme = "default";

    private String themeUrl;

    private Map<String, Object> themeConfig;

    private String formsPath;


    public TemplateBeanFm(RealmBeanFm realm) {
        formsPath = "/auth-server/forms";

        // TODO Get theme name from realm
        theme = "default";
        themeUrl = formsPath + "/theme/" + theme;

        themeConfig = new HashMap<String, Object>();

        themeConfig.put("styles", themeUrl + "/styles.css");

        if (realm.isSaas()) {
            themeConfig.put("logo", themeUrl + "/img/red-hat-logo.png");
            themeConfig.put("background", themeUrl + "/img/login-screen-background.jpg");
        } else {
            themeConfig.put("background", themeUrl + "/img/customer-login-screen-bg2.jpg");
            themeConfig.put("displayPoweredBy", true);
        }
    }

    public String getFormsPath() {
        return formsPath;
    }

    public Map<String, Object> getThemeConfig() {
        return themeConfig;
    }

    public String getTheme() {
        return theme;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public RealmBeanFm getRealm() {
        return realm;
    }

    public void setRealm(RealmBeanFm realm) {
        this.realm = realm;
    }

}
