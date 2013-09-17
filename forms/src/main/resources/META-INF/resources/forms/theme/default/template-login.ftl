<#macro registrationLayout bodyClass>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:h="http://java.sun.com/jsf/html">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${rb.getString('logInTo')} ${realm.name}</title>
    <link href="${template.themeConfig.styles}" rel="stylesheet" />
    <style>
        body {
            background-image: url("${template.themeConfig.background}");
        }
    </style>
</head>

<body class="rcue-login-register ${bodyClass}">
    <#if (template.themeConfig.logo)?has_content>
        <h1>
            <a href="#" title="Go to the home page"><img src="${template.themeConfig.logo}" alt="Logo" /></a>
        </h1>
    </#if>

    <div class="content">
        <h2>
            <#nested "header">
        </h2>

        <div class="background-area">
            <div class="form-area ${(realm.social)?string('social','')} clearfix">
                <section class="app-form">
                    <h3>Application login area</h3>
                    <#nested "form">
                </section>

                <#if error?has_content>
                    <div class="feedback error bottom-left show">
                        <p>
                            <strong>${rb.getString(error.summary)}</strong>
                        </p>
                    </div>
                </#if>

                <#if realm.social>
                    <section class="social-login"> <span>or</span>
                        <h3>Social login area</h3>
                        <p>${rb.getString('logInWith')}</p>
                        <ul>
                            <#list forms.providers as p>
                                <li><a href="${p.loginUrl}" class="zocial ${p.id}"> <span class="text">${p.name}</span></a></li>
                            </#list>
                        </ul>
                    </section>
                </#if>

                <section class="info-area">
                    <h3>Info area</h3>
                    <#nested "info">
                </section>
            </div>
        </div>

        <#if template.themeConfig['displayPoweredBy']>
            <p class="powered">
                <a href="#">${rb.getString('poweredByKeycloak')}</a>
            </p>
        </#if>
    </div>

    <#nested "content">

</body>
</html>
</#macro>