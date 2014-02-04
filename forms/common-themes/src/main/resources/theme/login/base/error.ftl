<#-- TODO: Only a placeholder, implementation needed -->
<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass=""; section>
    <#if section = "title">
    We're sorry...

    <#elseif section = "header">
    We're <strong>sorry</strong> ...

    <#elseif section = "form">
    <div id="kc-error-message">
        <p class="instruction">Something happened and we could not process your request.</p>
        <p id="error-summary" class="instruction second">${message.summary}</p>
    </div>
    </#if>
</@layout.registrationLayout>