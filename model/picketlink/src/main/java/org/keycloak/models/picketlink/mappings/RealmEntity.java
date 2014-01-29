package org.keycloak.models.picketlink.mappings;

import org.picketlink.idm.jpa.annotations.AttributeValue;
import org.picketlink.idm.jpa.annotations.OwnerReference;
import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;
import org.picketlink.idm.jpa.model.sample.simple.PartitionTypeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@IdentityManaged(RealmData.class)
@Entity
public class RealmEntity implements Serializable {
    @OneToOne
    @Id
    @OwnerReference
    private PartitionTypeEntity partitionTypeEntity;


    @AttributeValue
    private String realmName;
    @AttributeValue
    private boolean enabled;
    @AttributeValue
    private boolean sslNotRequired;
    @AttributeValue
    private boolean registrationAllowed;
    @AttributeValue
    private boolean verifyEmail;
    @AttributeValue
    private boolean resetPasswordAllowed;
    @AttributeValue
    private boolean social;
    @AttributeValue
    private boolean updateProfileOnInitialSocialLogin;
    @AttributeValue
    private int tokenLifespan;
    @AttributeValue
    private int accessCodeLifespan;
    @AttributeValue
    private int accessCodeLifespanUserAction;
    @AttributeValue
    @Column(length = 2048)
    private String publicKeyPem;
    @AttributeValue
    @Column(length = 2048)
    private String privateKeyPem;
    @AttributeValue
    private String[] defaultRoles;
    @AttributeValue
    @Lob
    private HashMap<String, String> smtpConfig;
    @AttributeValue
    @Lob
    private HashMap<String, String> socialConfig;
    @AttributeValue
    private String theme;


    public PartitionTypeEntity getPartitionTypeEntity() {
        return partitionTypeEntity;
    }

    public void setPartitionTypeEntity(PartitionTypeEntity partitionTypeEntity) {
        this.partitionTypeEntity = partitionTypeEntity;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSslNotRequired() {
        return sslNotRequired;
    }

    public void setSslNotRequired(boolean sslNotRequired) {
        this.sslNotRequired = sslNotRequired;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
    }

    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        this.verifyEmail = verifyEmail;
    }

    public boolean isResetPasswordAllowed() {
        return resetPasswordAllowed;
    }

    public void setResetPasswordAllowed(boolean resetPassword) {
        this.resetPasswordAllowed = resetPassword;
    }

    public boolean isSocial() {
        return social;
    }

    public void setSocial(boolean social) {
        this.social = social;
    }

    public boolean isUpdateProfileOnInitialSocialLogin() {
        return updateProfileOnInitialSocialLogin;
    }

    public void setUpdateProfileOnInitialSocialLogin(boolean updateProfileOnInitialSocialLogin) {
        this.updateProfileOnInitialSocialLogin = updateProfileOnInitialSocialLogin;
    }

    public int getTokenLifespan() {
        return tokenLifespan;
    }

    public void setTokenLifespan(int tokenLifespan) {
        this.tokenLifespan = tokenLifespan;
    }

    public int getAccessCodeLifespan() {
        return accessCodeLifespan;
    }

    public void setAccessCodeLifespan(int accessCodeLifespan) {
        this.accessCodeLifespan = accessCodeLifespan;
    }

    public int getAccessCodeLifespanUserAction() {
        return accessCodeLifespanUserAction;
    }

    public void setAccessCodeLifespanUserAction(int accessCodeLifespanUserAction) {
        this.accessCodeLifespanUserAction = accessCodeLifespanUserAction;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
    }

    public String getPrivateKeyPem() {
        return privateKeyPem;
    }

    public void setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
    }

    public HashMap<String, String> getSmtpConfig() {
        return smtpConfig;
    }

    public void setSmtpConfig(HashMap<String, String> smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    public HashMap<String, String> getSocialConfig() {
        return socialConfig;
    }

    public void setSocialConfig(HashMap<String, String> socialConfig) {
        this.socialConfig = socialConfig;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
