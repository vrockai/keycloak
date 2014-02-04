<#import "template.ftl" as layout>
<@layout.registrationLayout bodyClass=""; section>
    <#if section = "title">
    Google Authenticator Setup

    <#elseif section = "header">
    Google Authenticator Setup

    <#elseif section = "form">
    <ol id="kc-totp-settings">
        <li>
            <p><strong>1</strong>Download the <a href="http://code.google.com/p/google-authenticator/" target="_blank">Google Authenticator app</a> in your device.</p>
        </li>
        <li class="clearfix">
            <p><strong>2</strong>Create an account in Google Authenticator and scan the barcode or the provided key below.</p>
            <img src="${totp.totpSecretQrCodeUrl?html}" alt="Figure: Barcode">
            <span class="code">${totp.totpSecretEncoded}</span>
        </li>
        <li class="clearfix">
            <p><strong>3</strong>Enter the one-time-password provided by Google Authenticator below and click Submit to finish the setup.</p>
            <form action="${url.loginUpdateTotpUrl?html}" method="post">
                <div class="field-wrapper">
                    <label for="otp" class="two-lines">One-time-password</label><input type="text" id="totp" name="totp" />
                    <input type="hidden" id="totpSecret" name="totpSecret" value="${totp.totpSecret}" />
                </div>
                <input type="submit" class="btn-primary" value="Submit" />
            </form>
        </li>
    </ol>
    </#if>
</@layout.registrationLayout>