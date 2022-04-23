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
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutRequestBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractRequestToken;

public class LogoutRequestToken extends AbstractRequestToken {
	public LogoutRequest logoutRequest;
	
	@Override
	public RequestAbstractType getRequest() {
		return logoutRequest;
	}
	
	public LogoutRequestToken(MetadataInfo metadata, String subjectId, String sessionIndex){
		this.metadata = metadata;
		String destination = metadata.getSSOut(
				metadata.niasEntityDescriptor, SAMLConstants.SAML2_REDIRECT_BINDING_URI).getLocation();
		this.logoutRequest = generateLogoutRequest(destination, metadata.serviceCertificate, subjectId, sessionIndex);
	}
	
	public LogoutRequestToken(LogoutRequest request, MetadataInfo metadata){
		this.logoutRequest = request;
		this.metadata = metadata;
	}
	
	public boolean IsValid(){
		return CheckMethods.checklogoutrequest(this);
	}
	
	public static LogoutRequest generateLogoutRequest(String paramDestination, X509Certificate serviceCertificate, String subjectId, String sessionIndex){
		LogoutRequest request = new LogoutRequestBuilder().buildObject();
		request.setID("_"+ UUID.randomUUID());
		request.setVersion(SAMLVersion.VERSION_20);
		request.setIssueInstant(DateTime.now(DateTimeZone.UTC));
		request.setDestination(paramDestination);
		request.setNotOnOrAfter(DateTime.now(DateTimeZone.UTC).plusMinutes(5));
		request.setReason(LogoutRequest.USER_REASON);

		Issuer issuer = new IssuerBuilder().buildObject();
		issuer.setFormat(NameID.ENTITY);
		issuer.setValue(CertificateHelper.getSubjectName(serviceCertificate));
		request.setIssuer(issuer);
		
		NameID nameId = new NameIDBuilder().buildObject();
		nameId.setFormat(NameID.ENTITY);
		nameId.setValue(subjectId);
		request.setNameID(nameId);
	
		SessionIndex si = new SessionIndexBuilder().buildObject();
		si.setSessionIndex(sessionIndex);
		request.getSessionIndexes().add(si);
		
		return request;
	}
}
