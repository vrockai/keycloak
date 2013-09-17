package org.keycloak.forms;

public class ErrorBeanFm {

    private String summary;

    public ErrorBeanFm(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

}