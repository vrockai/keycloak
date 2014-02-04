<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass=""; section>
    <#if section = "title">
    ${rb.emailForgotHeader}

    <#elseif section = "header">
    ${rb.emailForgotHeader}

    <#elseif section = "form">
    <div id="kc-reset-password">
        <p class="instruction">${rb.emailInstruction}</p>
        <form action="${url.loginPasswordResetUrl?html}" method="post">
            <div class="field-wrapper">
                <label for="email">${rb.email}</label><input type="text" id="email" name="email" />
            </div>
            <input class="btn-primary" type="submit" value="Submit" />
        </form>
    </div>

    <#elseif section = "info" >
    <p><a href="${url.loginUrl?html}">&laquo; Back to Login</a></p>
    </#if>
</@layout.registrationLayout>