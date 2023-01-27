package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages;

import java.security.cert.X509Certificate;
import java.util.UUID;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.CheckMethods;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.CertificateHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.OneTimeUse;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.OneTimeUseBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractRequestToken;

public class AuthnRequestToken extends AbstractRequestToken {

	public AuthnRequest authnRequest;

	@Override
	public RequestAbstractType getRequest() {
		return authnRequest;
	}

	public AuthnRequestToken(MetadataInfo metadata) {
		this.metadata = metadata;

		final String destination = metadata.getSSOn(metadata.niasEntityDescriptor, SAMLConstants.SAML2_REDIRECT_BINDING_URI).getLocation();
		final String assertionConsumerServiceUrl = metadata.getAssertionConsumer(metadata.serviceEntityDescriptor, SAMLConstants.SAML2_POST_BINDING_URI).getLocation();
		final X509Certificate issuerCertificate = metadata.serviceCredentials.getEntityCertificate();

		this.authnRequest = generateRequest(destination, assertionConsumerServiceUrl, issuerCertificate);
	}

	public boolean IsValid() {
		return CheckMethods.checkAuthnRequest(this);
	}

	public static AuthnRequest generateRequest(String destination, String assertionConsumerSerivceURL, X509Certificate issuerCertificate) {
		AuthnRequest request = new AuthnRequestBuilder().buildObject();
		// set base data
		request.setID("_"+UUID.randomUUID().toString());
		request.setVersion(SAMLVersion.VERSION_20);
		request.setIssueInstant(DateTime.now(DateTimeZone.UTC));
		request.setDestination(destination);
		request.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		request.setAssertionConsumerServiceURL(assertionConsumerSerivceURL);
		// set issuer
		Issuer requestIssuer = new IssuerBuilder().buildObject();
		requestIssuer.setFormat(NameID.ENTITY);
		requestIssuer.setValue(CertificateHelper.getSubjectName(issuerCertificate));
		request.setIssuer(requestIssuer);
		// set NameId policy
		NameIDPolicy requestNameIdPolicy = new NameIDPolicyBuilder().buildObject();
		requestNameIdPolicy.setFormat(NameID.ENTITY);
		request.setNameIDPolicy(requestNameIdPolicy);
		// set conditions element
		Conditions requestConditions = new ConditionsBuilder().buildObject();
		requestConditions.setNotBefore(DateTime.now(DateTimeZone.UTC).minusSeconds(30));
		requestConditions.setNotOnOrAfter(DateTime.now(DateTimeZone.UTC).plusMinutes(5));
		//set one time use
		OneTimeUse oneUse = new OneTimeUseBuilder().buildObject();
		requestConditions.getConditions().add(oneUse);		
		request.setConditions(requestConditions);

		return request;
	}
}
