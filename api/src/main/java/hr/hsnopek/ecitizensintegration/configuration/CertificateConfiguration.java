package hr.hsnopek.ecitizensintegration.configuration;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.CertificateHelper;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.ConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

@Configuration
@DependsOn({"applicationProperties"})
public class CertificateConfiguration {

    @Bean
    public X509Certificate appCert()
            throws UnrecoverableKeyException, ConfigurationException, MetadataProviderException, SAMLException,
            CertificateException, KeyStoreException, NoSuchAlgorithmException {
        return Objects.requireNonNull(CertificateHelper.getBasicX509CredentialFromJKS(
                appKeyStore(),
                ApplicationProperties.APPLICATION_CERTIFICATE_ALIAS,
                ApplicationProperties.APPLICATION_CERTIFICATE_PASSWORD)).getEntityCertificate();
    }

    @Bean(name = "appCertPublicKey")
    public PublicKey appCertPublicKey()
            throws UnrecoverableKeyException, ConfigurationException, MetadataProviderException, SAMLException,
            CertificateException, KeyStoreException, NoSuchAlgorithmException {
        return appCert().getPublicKey();
    }

    @Bean(name = "appCertPrivateKey")
    public PrivateKey appCertPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return (PrivateKey) appKeyStore().getKey(ApplicationProperties.APPLICATION_CERTIFICATE_ALIAS,
                ApplicationProperties.APPLICATION_CERTIFICATE_PASSWORD.toCharArray());
    }
    @Bean(name = "appKeyStore")
    public KeyStore appKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
        try(InputStream is = new ClassPathResource(ApplicationProperties.APPLICATION_KEYSTORE_PATH).getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(is, ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD.toCharArray());
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (CertificateException | NoSuchAlgorithmException e) {
            throw e;
        }
    }
    @Bean(name = "appTrustStore")
    public KeyStore appTrustStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
        try(InputStream is = new ClassPathResource(ApplicationProperties.APPLICATION_TRUSTSTORE_PATH).getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(is, ApplicationProperties.APPLICATION_TRUSTSTORE_PASSWORD.toCharArray());
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (CertificateException | NoSuchAlgorithmException e) {
            throw e;
        }
    }

    @Bean(name = "authorizationServicePublicKey")
    public PublicKey authorizationServicePublicKey() throws CertificateException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException {
        return CertificateHelper.getBasicX509CredentialFromJKS(
                appTrustStore(),
                ApplicationProperties.AUTHORIZATION_SERVICE_PUBLIC_KEY_ALIAS,
                ApplicationProperties.APPLICATION_TRUSTSTORE_PASSWORD).getPublicKey();
    }
}
