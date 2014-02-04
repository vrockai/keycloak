<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass=""; section>
    <#if section = "title">
    ${rb.emailUsernameForgotHeader}

    <#elseif section = "header">
    ${rb.emailUsernameForgotHeader}

    <#elseif section = "form">
    <form id="kc-username-reminder-form" action="${url.loginUsernameReminderUrl?html}" method="post">
        <p class="instruction">${rb.emailUsernameInstruction}</p>
        <div class="field-wrapper">
            <label for="email">${rb.email}</label><input type="text" id="email" name="email" />
        </div>
        <input class="btn-primary" type="submit" value="Submit" />
    </form>
    <#elseif section = "info" >
    <p><a href="${url.loginUrl?html}">&laquo; Back to Login</a></p>
    </#if>
</@layout.registrationLayout>