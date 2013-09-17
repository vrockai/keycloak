package org.keycloak.forms;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class RegisterBeanFm {

    private Map<String, String> formData = new HashMap<String, String>();

    public RegisterBeanFm(MultivaluedMap<String, String> formData) {
        if (formData != null) {
            for (String k : formData.keySet()) {
                this.formData.put(k, formData.getFirst(k));
            }
        }
    }

    public Map<String, String> getFormData() {
        return formData;
    }

}
