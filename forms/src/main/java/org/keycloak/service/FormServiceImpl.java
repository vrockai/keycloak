package org.keycloak.service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.keycloak.forms.ErrorBeanFm;
import org.keycloak.forms.LoginBeanFm;
import org.keycloak.forms.RealmBeanFm;
import org.keycloak.forms.RegisterBeanFm;
import org.keycloak.forms.TemplateBeanFm;
import org.keycloak.forms.UrlBeanFm;
import org.keycloak.forms.UserBeanFm;
import org.keycloak.services.FormService;
import org.keycloak.services.resources.flows.Pages;

public class FormServiceImpl implements FormService {

    private static final String BUNDLE = "org.keycloak.forms.messages";

    public String getId(){
        return "FormServiceId";
    }

    public String process(String pageId, FormServiceDataBean dataBean){

        Map<String, Object> attributes = new HashMap<String, Object>();

        ResourceBundle rb = ResourceBundle.getBundle(BUNDLE);
        attributes.put("rb", rb);

        if (pageId.equals(Pages.LOGIN)) {
            processLogin(attributes, dataBean);
        } else if (pageId.equals(Pages.REGISTER)) {
            processRegister(attributes, dataBean);
        } else if (pageId.equals(Pages.ACCOUNT)) {
            processAccount(attributes, dataBean);
        }

        return processFmTemplate(pageId, attributes);
    }

    private void processAccount(Map<String, Object> attributes, FormServiceDataBean dataBean){

        RealmBeanFm realm = new RealmBeanFm(dataBean.getRealm());

        attributes.put("realm", realm);
        attributes.put("url", new UrlBeanFm(realm, dataBean.getBaseURI()));
        attributes.put("user", new UserBeanFm(dataBean.getUserModel()));
        attributes.put("login", new LoginBeanFm(realm, dataBean.getFormData()));
        attributes.put("template", new TemplateBeanFm(realm));
    }

    private void processLogin(Map<String, Object> attributes, FormServiceDataBean dataBean){

        if (dataBean.getError() != null){
            attributes.put("error", new ErrorBeanFm(dataBean.getError()));
        }

        RealmBeanFm realm = new RealmBeanFm(dataBean.getRealm());

        attributes.put("realm", realm);
        attributes.put("url", new UrlBeanFm(realm, dataBean.getBaseURI()));
        attributes.put("user", new UserBeanFm(dataBean.getUserModel()));
        attributes.put("login", new LoginBeanFm(realm, dataBean.getFormData()));
        attributes.put("template", new TemplateBeanFm(realm));
    }

    private void processRegister(Map<String, Object> attributes, FormServiceDataBean dataBean){

        if (dataBean.getError() != null){
            attributes.put("error", new ErrorBeanFm(dataBean.getError()));
        }

        RealmBeanFm realm = new RealmBeanFm(dataBean.getRealm());

        attributes.put("realm", realm);
        attributes.put("url", new UrlBeanFm(realm, dataBean.getBaseURI()));
        attributes.put("user", new UserBeanFm(dataBean.getUserModel()));
        attributes.put("login", new LoginBeanFm(realm, dataBean.getFormData()));
        attributes.put("register", new RegisterBeanFm(dataBean.getFormData()));
        attributes.put("template", new TemplateBeanFm(realm));
    }

    private static String processFmTemplate(String temp, Map<String, Object> input) {

        Writer out = new StringWriter();
        Configuration cfg = new Configuration();

        try {

            cfg.setClassForTemplateLoading(FormServiceImpl.class,"/META-INF/resources");

            Template template = cfg.getTemplate(temp);

            template.process(input, out);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toString();
    }

}
