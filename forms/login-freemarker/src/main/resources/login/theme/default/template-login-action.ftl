<#macro registrationLayout bodyClass isErrorPage=false isSeparator=false forceSeparator=false>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>
        <#nested "title">
    </title>
    <link rel="icon" href="${resourcePath}/img/favicon.ico">
    <link href="${resourcePath}/css/styles.css" rel="stylesheet" />
    <style type="text/css">
        body.rcue-login-register {
            background-image: url("${resourcePath}/img/login-screen-background.jpg");
        }
    </style>
</head>

<body class="rcue-login-register ${bodyClass}">
    <div class="feedback-aligner">
        <#if message?has_content && message.warning>
        <div class="feedback warning show">
            <p><strong>${rb.getString('actionWarningHeader')} ${message.summary}</strong><br/>${rb.getString('actionFollow')}</p>
        </div>
        </#if>
    </div>
    <h1>
        <a href="${url.loginUrl}" title="Go to the login page"><img src="${resourcePath}/img/keycloak-logo.png" alt="Keycloak" /></a>
    </h1>

    <div class="content">
        <h2>
            <#nested "header">
        </h2>

        <div class="background-area">
            <#if !forceSeparator && realm?has_content>
                <#assign drawSeparator = realm.registrationAllowed>
            <#else>
                <#assign drawSeparator = isSeparator>
            </#if>
            <div class="form-area clearfix ${(drawSeparator)?string('separator','')}">
                <div class="section app-form">
                    <#if !isErrorPage && message?has_content>
                        <#if message.error>
                            <div class="feedback error bottom-left show">
                                <p>
                                    <strong id="loginError">${message.summary}</strong><br/>${rb.getString('emailErrorInfo')}
                                </p>
                            </div>
                        <#elseif message.success>
                            <div class="feedback success bottom-left show">
                                <p>
                                    <strong>${rb.getString('successHeader')}</strong> ${message.summary}
                                </p>
                            </div>
                        </#if>
                    </#if>

                    <h3>Application login area</h3>
                    <#nested "form">
                </div>

                <div class="section info-area">
                    <h3>Info area</h3>
                    <#nested "info">
                </div>
            </div>
        </div>

        <p class="powered">
            <a href="#">${rb.getString('poweredByKeycloak')}</a>
        </p>
    </div>

    <#nested "content">

</body>
</html>
</#macro>