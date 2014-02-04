<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass=""; section>
    <#if section = "title">
    ${rb.emailUpdateHeader}

    <#elseif section = "header">
    ${rb.emailUpdateHeader}

    <#elseif section = "form">
    <form id="kc-passwd-update-form" action="${url.loginUpdatePasswordUrl?html}" method="post">
        <div class="field-wrapper">
            <label for="password-new">${rb.passwordNew}</label><input type="password" id="password-new" name="password-new" />
        </div>
        <div class="field-wrapper">
            <label for="password-confirm" class="two-lines">${rb.passwordConfirm}</label><input type="password" id="password-confirm" name="password-confirm" />
        </div>

        <input class="btn-primary" type="submit" value="Submit" />
    </form>
    </#if>
</@layout.registrationLayout>