package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Base64;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutResponseToken;

public class CheckMethods {

	private static final Logger log = LoggerFactory.getLogger(CheckMethods.class);
	
	private static EntityDescriptor getIssuerEntity(MetadataInfo metadata) {
		//check methods only validate NIAS as issuer entity
		return metadata.niasEntityDescriptor;
	}

	private static Certificate getIssuerCertificate(MetadataInfo metadata) {
		//check methods only validate NIAS as issuer entity
		return metadata.niasCertificate;
	}
	
	/**
	 * Checks XML signature of given element by PublicKey
	 */
	public static boolean checkXMLSignature(PublicKey publicKey, Signature signatureElement){
		// check signature
		try {
			SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
			profileValidator.validate(signatureElement);

			// setup credential to verify signatureElement
			BasicCredential issuerPublicKey = new BasicCredential();
			issuerPublicKey.setPublicKey(publicKey);

			SignatureValidator sigValidator = new SignatureValidator(issuerPublicKey);
			sigValidator.validate(signatureElement);
		} catch (ValidationException e) {
			log.error("greska u potpisu", e);
			return false;
		}
		return true;
	}

	/**
	 * Check XML signature of give element by designated ISSUER public key
	 */
	private static boolean checkXMLSignature(MetadataInfo metadata, Signature signatureElement) {
		// check signature		
		return checkXMLSignature(getIssuerCertificate(metadata).getPublicKey(), signatureElement);
	}

	/**
	 * Checks signature of HTTP-REDIRECT protocol by designated ISSUER public key
	 */
	private static boolean checkRedirectDeflateSignature(MetadataInfo metadata, HTTPInTransport httpIn) {
		try {
			StringBuilder str = new StringBuilder();
			
			//regenerate query string that was used to sign the data
			String sigAlg = URLEncoder.encode(httpIn.getParameterValue("SigAlg"), "UTF-8");
			String signature = httpIn.getParameterValue("Signature");
			if (httpIn.getParameterValue("SAMLRequest") != null) {
				str.append("SAMLRequest=" + URLEncoder.encode(httpIn.getParameterValue("SAMLRequest"), "UTF-8"));
			} else if (httpIn.getParameterValue("SAMLResponse") != null) {
				str.append("SAMLResponse=" + URLEncoder.encode(httpIn.getParameterValue("SAMLResponse"), "UTF-8"));
			} else {
				return false;
			}
			if (httpIn.getParameterValue("RelayState") != null) {
				str.append("&RelayState=" + URLEncoder.encode(httpIn.getParameterValue("RelayState"), "UTF-8"));
			}
			str.append("&SigAlg=" + sigAlg);

			//get byte[] of sent signature
			Charset utf8 = Charset.forName("UTF-8");
			byte[] dataSignature = Base64.getDecoder().decode(signature);

			//get byte[] of query string used to make the signature
			String strSignature = str.toString();
			ByteBuffer buffer = utf8.encode(strSignature);
			byte[] dataInput = new byte[buffer.remaining()];
			buffer.get(dataInput);

			try {
				//init signature element
				java.security.Signature sig = java.security.Signature.getInstance("SHA1withRSA");
				sig.initVerify(getIssuerCertificate(metadata));
				sig.update(dataInput);
				boolean isValid = sig.verify(dataSignature);
				if (!isValid)
					return false;
			} catch (NoSuchAlgorithmException | InvalidKeyException e) {
				throw new RuntimeException(e);
			} catch (SignatureException e) {
				log.info("signature verification failed", e);
				return false;
			}
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		}
		return true;
	}

	/**
	 * Checks base request data
	 */
	private static boolean checkRequest(AbstractRequestToken request) {
		// check issuer
		if (!getIssuerEntity(request.metadata).getEntityID().equals(request.getRequest().getIssuer().getValue())) {
			log.info("poruka nije izdana od {]", getIssuerEntity(request.metadata).getEntityID());
			return false;
		}

		// check SAML version
		if (!request.getRequest().getVersion().equals(SAMLVersion.VERSION_20)) {
			log.debug("hr.hsnopek.niasintegration.domain.feature.user.nias.saml poruka nije verzije 2.0");
			return false;
		}

		// check if signature exists
		if (request.getRequest().getSignature() != null) {
			// found XML signature, check signature
			if (!checkXMLSignature(request.metadata, request.getRequest().getSignature()))
				return false;
		} else {
			// must be HTTP-REDIRECT signature
			if (!checkRedirectDeflateSignature(request.metadata, request.httpIn)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks base response data
	 */
	private static boolean checkResponse(AbstractResponseToken response) {
		// check issuer
		if (!getIssuerEntity(response.metadata).getEntityID().equals(response.getResponse().getIssuer().getValue())) {
			log.debug("Poruka nije izdana od {}", getIssuerEntity(response.metadata).getEntityID());
			return false;
		}

		// check SAML version
		if (!response.getResponse().getVersion().equals(SAMLVersion.VERSION_20)) {
			log.debug("Poruka nije verzije 2.0");
			return false;
		}

		// check if signature exists
		if (response.getResponse().getSignature() != null) {
			// found XML signature, check signature
			if (!checkXMLSignature(response.metadata, response.getResponse().getSignature()))
				return false;
		} else {
			// must be HTTP-REDIRECT signature
			if (!checkRedirectDeflateSignature(response.metadata, response.httpIn)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks AuthnRequest message
	 */
	public static boolean checkAuthnRequest(AuthnRequestToken requestToken) {
		if (!checkRequest(requestToken)) {
			return false;
		}

		// check message date validity
		if (requestToken.authnRequest.getConditions() != null) {
			if (requestToken.authnRequest.getConditions().getNotBefore().isAfter(DateTime.now(DateTimeZone.UTC))) {
				log.debug("poruka je istekla - NotBefore");
				return false;
			}
			if (requestToken.authnRequest.getConditions().getNotOnOrAfter().isBefore(DateTime.now(DateTimeZone.UTC))) {
				log.debug("poruka je istekla - NotOnOrAfter");
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks AuthnResponse message
	 */
	public static boolean checkAuthnResponse(AuthnResponseToken response) {
		if (!checkResponse(response))
			return false;
		
		//must be only one assertion
		if(response.authnResponse.getAssertions().size() != 1){
			log.debug("number of assertions != 1");
			return false;
		}
		
		//must be only one authn statement
		if(response.authnResponse.getAssertions().get(0).getAuthnStatements().size() != 1){
			log.debug("number of AuthnStatements != 1");
			return false;
		}
		
		//must be only one attribute statement
		if(response.authnResponse.getAssertions().get(0).getAttributeStatements().size()!=1){
			log.debug("number of attribute statements != 1");
			return false;
		}

		// check message date validity (all assertions must be valid)
		for (Assertion assertion : response.authnResponse.getAssertions()) {
			if (assertion.getConditions() != null) {
				if (assertion.getConditions().getNotBefore().isAfter(DateTime.now(DateTimeZone.UTC))) {
					log.debug("poruka je istekla - Assertion.NotBefore");
					return false;
				}
				if (assertion.getConditions().getNotOnOrAfter().isBefore(DateTime.now(DateTimeZone.UTC))) {
					log.debug("poruka je istekla - Assertion.NotOnOrAfter");
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks LogoutRequest
	 */
	public static boolean checklogoutrequest(LogoutRequestToken token) {
		if (!checkRequest(token)) {
			return false;
		}

		return true;
	}

	/**
	 * Checks LogoutResponse
	 */
	public static boolean checkLogoutResponse(LogoutResponseToken response) {
		if (!checkResponse(response)){
			return false;
		}

		return true;
	}
}
