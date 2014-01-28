<#macro mainLayout active bodyClass>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>Edit Account - <#nested "title"></title>
    <link rel="icon" href="${template.formsPath}/theme/${template.theme}/img/favicon.ico">

    <!-- Frameworks -->
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/reset.css">
    <link href="${template.formsPath}/lib/bootstrap/css/bootstrap.css" rel="stylesheet" />
    <link href="${template.formsPath}/theme/${template.theme}/css/zocial/zocial.css" rel="stylesheet">

    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/sprites.css">
    <!-- TODO remove external links -->
    <link rel="stylesheet" href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,300italic,400italic,600,600italic,700,700italic,800,800italic'>

    <!-- RCUE styles -->
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/base.css">
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/forms.css">
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/header.css">
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/icons.css">
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/tables.css">

    <!-- Page styles -->
    <link rel="stylesheet" href="${template.formsPath}/theme/${template.theme}/css/admin-console.css">

</head>
<body class="admin-console user ${bodyClass}">

    <#if message?has_content>
    <div class="feedback-aligner">
        <#if message.success>
        <div class="feedback success show"><p><strong>${rb.getString('successHeader')}</strong> ${message.summary}</p></div>
        </#if>
        <#if message.error>
        <div class="feedback error show"><p><strong>${rb.getString('errorHeader')}</strong> ${message.summary}</p></div>
        </#if>
    </div>
    </#if>

<div class="header rcue">
    <div class="navbar utility">
        <div class="navbar-inner clearfix container">
            <h1><a href="#"><strong>Keycloak</strong> Central Login</a></h1>
            <ul class="nav pull-right">
                <li>
                    <a href="${url.logoutUrl}">Sign Out</a>
                </li>
            </ul>
        </div>
    </div>
</div><!-- End .header -->

<div class="container">
    <div class="row">
        <div class="bs-sidebar col-md-3 clearfix">
            <ul>
                <li class="<#if active=='account'>active</#if>"><a href="${url.accountUrl}">Account</a></li>
                <li class="<#if active=='password'>active</#if>"><a href="${url.passwordUrl}">Password</a></li>
                <li class="<#if active=='totp'>active</#if>"><a href="${url.totpUrl}">Authenticator</a></li>
                <#--<li class="<#if active=='social'>active</#if>"><a href="${url.socialUrl}">Social Accounts</a></li>-->
                <#--<li class="<#if active=='access'>active</#if>"><a href="${url.accessUrl}">Authorized Access</a></li>-->
            </ul>
        </div>

        <div id="content-area" class="col-md-9" role="main">
            <div id="content">
                <h2 class="pull-left"><#nested "header"></h2>
                <#nested "content">
            </div>
        </div>
        <div id="container-right-bg"></div>
    </div>
</div>
</body>
</html>
</#macro>