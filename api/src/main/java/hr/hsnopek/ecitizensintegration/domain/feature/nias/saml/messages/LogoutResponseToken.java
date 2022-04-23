package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages;

import java.security.cert.X509Certificate;
import java.util.UUID;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.CheckMethods;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.CertificateHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusResponseType;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutResponseBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractResponseToken;

public class LogoutResponseToken extends AbstractResponseToken {
	public LogoutResponse logoutResponse;
	
	@Override
	public StatusResponseType getResponse() {
		return logoutResponse;
	}
	
	public LogoutResponseToken(MetadataInfo metadata, String destination, String inResponseTo, String codeStatus){
		this.metadata = metadata;
		this.logoutResponse = generateLogoutResponse(inResponseTo, destination, metadata.serviceCertificate, codeStatus);
	}
	
	public LogoutResponseToken(LogoutResponse response, MetadataInfo metadata){
		this.logoutResponse = response;
		this.metadata = metadata;
	}
	
	public boolean IsValid(){
		return CheckMethods.checkLogoutResponse(this);
	}
	
	public static LogoutResponse generateLogoutResponse(String inResponseTo, String destination, X509Certificate serviceCertificate, String code_status){
		LogoutResponse response = new LogoutResponseBuilder().buildObject();
		response.setID("_"+UUID.randomUUID().toString());
		response.setInResponseTo(inResponseTo);
		response.setIssueInstant(DateTime.now(DateTimeZone.UTC));
		response.setVersion(SAMLVersion.VERSION_20);
		response.setDestination(destination);
		
		Issuer issuer = new IssuerBuilder().buildObject();
		issuer.setFormat(NameID.ENTITY);
		issuer.setValue(CertificateHelper.getSubjectName(serviceCertificate));
		response.setIssuer(issuer);
		
		Status status = new StatusBuilder().buildObject();
		StatusCode code = new StatusCodeBuilder().buildObject();
		code.setValue(code_status);
		status.setStatusCode(code);
		response.setStatus(status);
		
		return response;
	}
}
