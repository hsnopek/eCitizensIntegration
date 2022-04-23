package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml;

import java.security.cert.X509Certificate;

import org.opensaml.common.SAMLException;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for providing Metadata context throughout SAML example
 */
public class MetadataInfo {
	private static final Logger log = LoggerFactory.getLogger(MetadataInfo.class);

	// Metadata/EntityDescriptor for Service
	public EntityDescriptor serviceEntityDescriptor;
	// Metadata/EntityDescriptor for NIAS
	public EntityDescriptor niasEntityDescriptor;

	public BasicX509Credential serviceCredentials;
	public X509Certificate niasCertificate;
	public X509Certificate serviceCertificate;

	/**
	 * MetadataInfo constructor
	 * @param credentials					ServiceProvider credentials for signing messages (get through SecurityHelper methods)
	 * @param niasCertificate				NIAS certificate for checking signed messages coming from NIAS
	 * @param niasEntityDescriptor
	 * @param serviceEntityDescriptor
	 */
	public MetadataInfo(BasicX509Credential credentials, X509Certificate niasCertificate,
						EntityDescriptor serviceEntityDescriptor,EntityDescriptor niasEntityDescriptor)
			throws MetadataProviderException, SAMLException {

		this.serviceCredentials = credentials;
		this.serviceCertificate = credentials.getEntityCertificate();
		this.niasCertificate = niasCertificate;

		this.niasEntityDescriptor = niasEntityDescriptor;
		this.serviceEntityDescriptor = serviceEntityDescriptor;
	}

	public AssertionConsumerService getAssertionConsumer(EntityDescriptor entity, String binding) {
		for (AssertionConsumerService acService : entity.getSPSSODescriptor(SAMLConstants.SAML20P_NS).getAssertionConsumerServices()) {
			if (acService.getBinding().equals(binding)) {
				return acService;
			}
		}
		log.debug("did not find {} binding for AssertionConsumer in SERVICE metadata", binding);
		return null;
	}

	public SingleSignOnService getSSOn(EntityDescriptor entity, String binding) {
		for (SingleSignOnService sson : entity.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getSingleSignOnServices()) {
			if (sson.getBinding().equals(binding))
				return sson;
		}
		log.debug("did not find {} binding for SSOn in NIAS metadata", binding);
		return null;
	}

	public SingleLogoutService getSSOut(EntityDescriptor entity, String binding) {
		for (SingleLogoutService slo : entity.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getSingleLogoutServices()) {
			if (slo.getBinding().equals(binding))
				return slo;
		}
		log.debug("did not find {} binding for SSOut in NIAS metadata", binding);
		return null;
	}
}
