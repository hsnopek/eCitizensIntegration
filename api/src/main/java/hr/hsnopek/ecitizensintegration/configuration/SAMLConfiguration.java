package hr.hsnopek.ecitizensintegration.configuration;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.InMemoryMetadataProvider;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.SamlTokenManager;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.CertificateHelper;
import org.apache.commons.httpclient.HttpClient;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Timer;

@Configuration
@DependsOn("applicationProperties")
public class SAMLConfiguration {
    private static boolean isBootStrapped = false;

    @PostConstruct
    public void samlBootstrap() throws ConfigurationException {
        if (!isBootStrapped) {
            try {
                DefaultBootstrap.bootstrap();
                isBootStrapped = true;
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public SamlTokenManager samlTokenManager(KeyStore appKeyStore, KeyStore appTrustStore, X509Certificate appCert, PrivateKey appCertPrivateKey)
            throws UnrecoverableKeyException, ConfigurationException, MetadataProviderException, SAMLException, KeyStoreException {
        return new SamlTokenManager(metadataInfo(appKeyStore, appTrustStore));
    }

    @Bean
    public MetadataInfo metadataInfo(KeyStore appKeyStore, KeyStore appTrustStore)
            throws UnrecoverableKeyException, ConfigurationException, MetadataProviderException, SAMLException, KeyStoreException {

        BasicX509Credential serviceCredentials = CertificateHelper.getBasicX509CredentialFromJKS(
                appKeyStore,
                ApplicationProperties.APPLICATION_CERTIFICATE_ALIAS,
                ApplicationProperties.APPLICATION_CERTIFICATE_PASSWORD);

        if(serviceCredentials == null)
            throw new RuntimeException("ServiceCredentials can't be null!");

        InMemoryMetadataProvider spMetadataProvider =
                new InMemoryMetadataProvider(
                        serviceCredentials,
                        ApplicationProperties.APPLICATION_URL + ApplicationProperties.NIAS_LOGIN_RESPONSE_URL);
        spMetadataProvider.setParserPool(new BasicParserPool());
        spMetadataProvider.setRequireValidMetadata(true);
        spMetadataProvider.initialize();

        HTTPMetadataProvider niasMetadataProvider = new HTTPMetadataProvider(new Timer(), new HttpClient(), ApplicationProperties.NIAS_METADATA_URL);
        niasMetadataProvider.setParserPool(new BasicParserPool());
        niasMetadataProvider.setRequireValidMetadata(true);
        niasMetadataProvider.initialize();

        X509Certificate niasCertificate = (X509Certificate) appTrustStore.getCertificate(ApplicationProperties.NIAS_PUBLIC_KEY_ALIAS);

        String niasSubjectName = CertificateHelper.getSubjectName(Objects.requireNonNull(niasCertificate));

        EntityDescriptor niasEntityDescriptor = niasMetadataProvider.getEntityDescriptor(niasSubjectName);
        EntityDescriptor serviceEntityDescriptor = spMetadataProvider.getEntityDescriptor();

        return new MetadataInfo(serviceCredentials, niasCertificate, serviceEntityDescriptor, niasEntityDescriptor);
    }
}
