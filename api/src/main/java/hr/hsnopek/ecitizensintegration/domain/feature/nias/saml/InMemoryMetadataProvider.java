package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.SamlUtil;
import org.joda.time.DateTime;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.*;
import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;

import java.util.Date;

public class InMemoryMetadataProvider extends AbstractMetadataProvider {

    /**
     * Preconfigured descriptor
     */
    private final EntityDescriptor descriptor;
    private final BasicX509Credential basicX509Credential;
    private final String entityID;
    private final Date validUntil;
    private final String assertionConsumerServiceLocation;

    public InMemoryMetadataProvider(BasicX509Credential basicX509Credential, String assertionServiceConsumerLocation) {
        this.entityID = basicX509Credential.getEntityCertificate().getSubjectDN().toString();
        this.basicX509Credential = basicX509Credential;
        this.validUntil = basicX509Credential.getEntityCertificate().getNotAfter();
        this.assertionConsumerServiceLocation = assertionServiceConsumerLocation;
        this.descriptor = getEntityDescriptor();
    }

    /**
     * @return preconfigured entity descriptor
     */
    public XMLObject getMetadata() {
        return descriptor;
    }

    @Override
    protected XMLObject doGetMetadata() throws MetadataProviderException {
        return descriptor;
    }

    public EntityDescriptor getEntityDescriptor(){
        EntityDescriptor entityDescriptor = null;
        try {
            entityDescriptor = SamlUtil.buildSAMLObjectWithDefaultName(EntityDescriptor.class);
            entityDescriptor.setEntityID(entityID);
            entityDescriptor.setValidUntil(new DateTime(validUntil));

            SPSSODescriptor spSSODescriptor = SamlUtil.buildSAMLObjectWithDefaultName(SPSSODescriptor.class);
            spSSODescriptor.setWantAssertionsSigned(true);
            spSSODescriptor.setAuthnRequestsSigned(true);

            // Setting up KeyInfoGeneratorFactory

            X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
            keyInfoGeneratorFactory.setEmitEntityCertificate(true);
            KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

            // Element will contain the public key. The key is used by the IDP to encrypt data

            KeyDescriptor encKeyDescriptor = SamlUtil.buildSAMLObjectWithDefaultName(KeyDescriptor.class);
            encKeyDescriptor.setUse(UsageType.ENCRYPTION); //Set usage
            encKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(basicX509Credential));

            // Element will contain the public key. The key is used to by the IDP to verify signatures

            KeyDescriptor signKeyDescriptor = SamlUtil.buildSAMLObjectWithDefaultName(KeyDescriptor.class);
            signKeyDescriptor.setUse(UsageType.SIGNING);
            signKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(basicX509Credential));

            // Setting key descriptors

            spSSODescriptor.getKeyDescriptors().add(encKeyDescriptor);
            spSSODescriptor.getKeyDescriptors().add(signKeyDescriptor);

            NameIDFormat nameIDFormat = SamlUtil.buildSAMLObjectWithDefaultName(NameIDFormat.class);
            nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
            spSSODescriptor.getNameIDFormats().add(nameIDFormat);

            AssertionConsumerService assertionConsumerService = SamlUtil.buildSAMLObjectWithDefaultName(AssertionConsumerService.class);
            assertionConsumerService.setIndex(0);
            assertionConsumerService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
            assertionConsumerService.setLocation(assertionConsumerServiceLocation);

            spSSODescriptor.getAssertionConsumerServices().add(assertionConsumerService);
            spSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

            entityDescriptor.getRoleDescriptors().add(spSSODescriptor);

        } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
        return entityDescriptor;
    }
}
