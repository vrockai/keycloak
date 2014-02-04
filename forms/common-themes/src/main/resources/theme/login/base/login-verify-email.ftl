<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass="email"; section>
    <#if section = "title">
    Email verification

    <#elseif section = "header">
    Email verification

    <#elseif section = "form">
    <div id="kc-verify-email" class="app-form">
        <p class="instruction">
            Your account is not enabled. An email with instructions to verify your email address has been sent to you.
        </p>
        <p class="instruction">Haven't received a verification code in your email?
            <a href="${url.loginEmailVerificationUrl?html}">Click here</a> to re-send the email.
        </p>
    </div>
    </#if>
</@layout.registrationLayout>