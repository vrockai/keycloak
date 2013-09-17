<#macro mainLayout>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:h="http://java.sun.com/jsf/html">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Keycloak Account Management</title>
	<link href="${template.formsPath}/lib/bootstrap/css/bootstrap.css" rel="stylesheet" />
	<style>
body {
	padding-top: 50px;
}
</style>
</head>

<body>

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="${url.accountUrl}">Account</a></li>
					<li><a href="${url.passwordUrl}">Password</a></li>
					<li><a href="${url.totpUrl}">Authenticator</a></li>
					<li><a href="${url.socialUrl}">Social Accounts</a></li>
					<li><a href="${url.accessUrl}">Authorized Access</a></li>
				</ul>
			</div>
		</div>
	</div>

	<div class="container">
		<h1>
            <#nested "header">
		</h1>

        <#if error?has_content>
			<div class="alert alert-danger">${rb.getString(error.summary)}</div>
        </#if>

        <#nested "content">
	</div>

</body>
</html>
</#macro>