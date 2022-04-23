package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.decoding.BaseSAML2MessageDecoder;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.binding.decoding.HTTPRedirectDeflateDecoder;
import org.opensaml.saml2.binding.decoding.HTTPSOAP11Decoder;
import org.opensaml.saml2.binding.encoding.BaseSAML2MessageEncoder;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.binding.encoding.HTTPSOAP11Encoder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.opensaml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.xml.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.AuthnResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutRequestToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages.LogoutResponseToken;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils.VelocityEngineHelper;

public class SamlTokenManager {
	private static final Logger log = LoggerFactory.getLogger(SamlTokenManager.class);

	private MetadataInfo metadata;

	public SamlTokenManager(MetadataInfo metadata) {
		this.metadata = metadata;
	}

	/**
	 * Method for sending AuthnRequest to NIAS
	 * 
	 * metadata uses
	 * 		metadata.service = Issuer
	 * 		metadata.nias = Destination
	 */
	public void sendAuthnRequest(AuthnRequestToken request, HTTPOutTransport httpOut) throws MessageEncodingException {
		// setup service where to send AuthnRequest
		SingleSignOnService endpoint = new SingleSignOnServiceBuilder().buildObject();
		endpoint.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		endpoint.setLocation(request.getRequest().getDestination());

		// init message context
		BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> mc = new BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject>();
		mc.setOutboundSAMLMessage(request.authnRequest);
		mc.setOutboundMessageTransport(httpOut);
		mc.setPeerEntityEndpoint(endpoint);		
		mc.setOutboundSAMLMessageSigningCredential(request.metadata.serviceCredentials);

		//encode message (sending is done in encode method)
		BaseSAML2MessageEncoder encoder = getEncoder(endpoint.getBinding());
		encoder.encode(mc);
	}

	public AuthnResponseToken receiveAuthnResponse(HTTPInTransport httpIn) throws MessageDecodingException, SecurityException {
		// init message context
		BasicSAMLMessageContext<Response, SAMLObject, SAMLObject> mc = new BasicSAMLMessageContext<Response, SAMLObject, SAMLObject>();
		mc.setInboundMessageTransport(httpIn);

		// decode the message
		BaseSAML2MessageDecoder decoder = getDecoder(httpIn);
		decoder.decode(mc);

		// get decoded message from message context
		Response r = mc.getInboundSAMLMessage();
		AuthnResponseToken token = new AuthnResponseToken(r, metadata);
		token.httpIn = httpIn;
		return token;
	}
	
	public void sendLogoutRequest(LogoutRequestToken request, String binding, HTTPOutTransport httpOut) throws MessageEncodingException{
		// setup single logout service to send LogoutRequest
		SingleLogoutService slo = new SingleLogoutServiceBuilder().buildObject();
		slo.setBinding(binding);
		slo.setLocation(request.logoutRequest.getDestination());
		
		// init message context
		BasicSAMLMessageContext<SAMLObject, LogoutRequest, SAMLObject> mc = new BasicSAMLMessageContext<SAMLObject, LogoutRequest, SAMLObject>();		
		mc.setOutboundSAMLMessage(request.logoutRequest);
		mc.setOutboundMessageTransport(httpOut);
		mc.setPeerEntityEndpoint(slo);
		mc.setOutboundSAMLMessageSigningCredential(request.metadata.serviceCredentials);
		
		// encode message (sending is done in encode method)
		BaseSAML2MessageEncoder encoder = getEncoder(slo.getBinding());
		encoder.encode(mc);
	}
	
	public LogoutRequestToken receiveLogoutRequest(HTTPInTransport httpIn) throws MessageDecodingException, SecurityException{
		// init message context
		BasicSAMLMessageContext<LogoutRequest, SAMLObject, SAMLObject> mc = new BasicSAMLMessageContext<LogoutRequest, SAMLObject, SAMLObject>();
		mc.setInboundMessageTransport(httpIn);
		
		// decode the message
		BaseSAML2MessageDecoder decoder = getDecoder(httpIn);
		decoder.decode(mc);
		
		// read decoded message from context
		LogoutRequest request = mc.getInboundSAMLMessage();
		LogoutRequestToken token = new LogoutRequestToken(request, metadata);
		token.httpIn = httpIn;
		return token;
	}
	
	public void sendLogoutResponse(LogoutResponseToken response, HTTPOutTransport httpOut) throws MessageEncodingException{
		// setup logoutresponse service
		SingleLogoutService slo = new SingleLogoutServiceBuilder().buildObject();
		slo.setBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);
		slo.setLocation(response.logoutResponse.getDestination());
		
		// init message context
		BasicSAMLMessageContext<SAMLObject, LogoutResponse, SAMLObject> mc = new BasicSAMLMessageContext<SAMLObject, LogoutResponse, SAMLObject>();
		mc.setOutboundSAMLMessage(response.logoutResponse);
		mc.setOutboundMessageTransport(httpOut);
		mc.setPeerEntityEndpoint(slo);
		mc.setOutboundSAMLMessageSigningCredential(response.metadata.serviceCredentials);
		
		// encode message (sending is done in encode method)
		BaseSAML2MessageEncoder encoder = getEncoder(slo.getBinding());
		encoder.encode(mc);
	}
	
	public LogoutResponseToken receiveLogoutResponse(HTTPInTransport httpIn) throws MessageDecodingException, SecurityException{
		// init message context
		BasicSAMLMessageContext<LogoutResponse, SAMLObject, SAMLObject> mc = new BasicSAMLMessageContext<LogoutResponse, SAMLObject, SAMLObject>();
		mc.setInboundMessageTransport(httpIn);
		
		// decode the message
		BaseSAML2MessageDecoder decoder = getDecoder(httpIn);
		decoder.decode(mc);
		
		// read the decoded message from context
		LogoutResponse response = mc.getInboundSAMLMessage();
		LogoutResponseToken token = new LogoutResponseToken(response, metadata);
		token.httpIn = httpIn;
		return token;
	}

	/**
	 * Method for choosing the right encoder for given binding
	 */
	public static BaseSAML2MessageEncoder getEncoder(String binding) {
		if (binding.equals(SAMLConstants.SAML2_REDIRECT_BINDING_URI))
			return new HTTPRedirectDeflateEncoder();
		else if (binding.equals(SAMLConstants.SAML2_POST_BINDING_URI)) {
			return new HTTPPostEncoder(VelocityEngineHelper.getInstance(), "/templates/saml2-post-binding.vm");
		} else if (binding.equals(SAMLConstants.SAML2_SOAP11_BINDING_URI)) {
			return new HTTPSOAP11Encoder();
		} else {
			log.error("binding not supported " + binding);
			return null;
		}
	}

	/**
	 * Method for choosing the right decoder for given HTTP request
	 */
	public static BaseSAML2MessageDecoder getDecoder(HTTPInTransport httpIn) {
		if (httpIn.getHTTPMethod().equals("GET")) {
			log.info("RedirectDeflateDecoder chosen");
			return new HTTPRedirectDeflateDecoder();
		} else if (httpIn.getHTTPMethod().equals("POST")) {
			if (httpIn.getHeaderValue("SOAPAction") != null) {
				log.info("HTTPSOAP11Decoder chosen");
				return new HTTPSOAP11Decoder();
			} else {
				log.info("HTTPPostDecoder chosen");
				return new HTTPPostDecoder();
			}
		}
		return null;
	}

}
