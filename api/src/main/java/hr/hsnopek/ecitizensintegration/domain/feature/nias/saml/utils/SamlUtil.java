package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils;

import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class SamlUtil<T>{

    public static <T> T buildSAMLObjectWithDefaultName(final Class<T> clazz) throws NoSuchFieldException, IllegalAccessException {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

        QName defaultElementName = (QName)clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);

        return (T)builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
    }

    public static String generateSAMLRequestRedirectBidingUrl(PrivateKey privateKey, String ssoURL, String issuerDn, String returnUrl)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParserConfigurationException, TransformerException {

        String preparedAuthenticationRequest =
                URLEncoder.encode(
                        Base64.encode(
                                deflateSamlRequest(
                                        createAuthenticationRequest(ssoURL, issuerDn, returnUrl)
                                )
                        ), StandardCharsets.UTF_8);

        String preparedRelayState = URLEncoder.encode("", StandardCharsets.UTF_8);
        String preparedSigAlg = URLEncoder.encode("http://www.w3.org/2000/09/xmldsig#rsa-sha1", StandardCharsets.UTF_8);

        String parameterString = String.format(
                "SAMLRequest=%s&RelayState=%s&SigAlg=%s",
                preparedAuthenticationRequest,
                preparedRelayState,
                preparedSigAlg);

        String preparedSignature = URLEncoder.encode(Base64.encode(signParameterString(privateKey, parameterString)), StandardCharsets.UTF_8);

        return String.format("%s?%s&Signature=%s", ssoURL,  parameterString, preparedSignature);
    }

    private static byte[] signParameterString(PrivateKey privateKey, String parameterString) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(privateKey);
        sig.update(parameterString.getBytes(StandardCharsets.UTF_8));

        return sig.sign();
    }

    private static String createAuthenticationRequest(String ssoURL, String issuerDn, String returnUrl) throws TransformerException, ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("AuthnRequest");
        rootElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:protocol");
        rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("ID", "_"+ UUID.randomUUID());
        rootElement.setAttribute("Version", "2.0");
        rootElement.setAttribute("IssueInstant", DateTime.now(DateTimeZone.UTC).toString());
        rootElement.setAttribute("Destination", ssoURL);
        rootElement.setAttribute("ProtocolBinding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        rootElement.setAttribute("AssertionConsumerServiceURL", returnUrl);

        doc.appendChild(rootElement);

        Element issuerElement = doc.createElement("Issuer");
        issuerElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:assertion");
        issuerElement.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        issuerElement.setTextContent(issuerDn);

        rootElement.appendChild(issuerElement);

        Element nameIdPolicyElement = doc.createElement("NameIDPolicy");
        nameIdPolicyElement.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity");

        rootElement.appendChild(nameIdPolicyElement);

        Element conditionsElement = doc.createElement("Conditions");
        conditionsElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:assertion");
        conditionsElement.setAttribute("NotBefore", DateTime.now(DateTimeZone.UTC).toString());
        conditionsElement.setAttribute("NotOnOrAfter", DateTime.now(DateTimeZone.UTC).plusMinutes(5).toString());

        Element conditionElement = doc.createElement("Condition");
        conditionElement.setAttribute("xmlns:q1", "http://nias.eid.com.hr/2012/07/saml20Extension");
        conditionElement.setAttribute("xsi:type", "q1:NiasConditionType");
        conditionElement.setAttribute("MinAuthenticationSecurityLevel", "1");

        Element oneTimeUseElement = doc.createElement("OneTimeUse");

        conditionsElement.appendChild(oneTimeUseElement);
        conditionsElement.appendChild(conditionElement);

        rootElement.appendChild(conditionsElement);

        return marshalAuthnRequest(doc);
    }

    public static String createLogoutRequest(String ssOutURL, String issuerDn, String TID, String sessionIndex) throws TransformerException, ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("LogoutRequest");
        rootElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:protocol");
        rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("ID", "_"+ UUID.randomUUID());
        rootElement.setAttribute("Version", "2.0");
        rootElement.setAttribute("IssueInstant", DateTime.now(DateTimeZone.UTC).toString());
        rootElement.setAttribute("Destination", ssOutURL);
        rootElement.setAttribute("Reason", "urn:oasis:names:tc:SAML:2.0:logout:user");
        rootElement.setAttribute("NotOnOrAfter", DateTime.now(DateTimeZone.UTC).plusMinutes(5).toString());

        doc.appendChild(rootElement);

        Element issuerElement = doc.createElement("Issuer");
        issuerElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:assertion");
        issuerElement.setAttribute("Format", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
        issuerElement.setTextContent(issuerDn);

        rootElement.appendChild(issuerElement);

        Element nameIdElement = doc.createElement("NameID");
        nameIdElement.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        nameIdElement.setAttribute("xmlns", "urn:oasis:names:tc:SAML:2.0:assertion");
        nameIdElement.setTextContent(TID);

        rootElement.appendChild(nameIdElement);

        Element sessionIndexElement = doc.createElement("SessionIndex");
        sessionIndexElement.setTextContent(sessionIndex);

        rootElement.appendChild(sessionIndexElement);

        return marshalAuthnRequest(doc);
    }


    private static String marshalAuthnRequest(Document doc) throws TransformerException {
        try (StringWriter writer = new StringWriter()) {
            DOMSource domSource = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] deflateSamlRequest(String samlRequest) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Deflater deflater = new Deflater( Deflater.DEFAULT_COMPRESSION, true );
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(os, deflater);
        deflaterOutputStream.write( samlRequest.getBytes(StandardCharsets.UTF_8) );
        deflaterOutputStream.close();
        os.close();
        return os.toByteArray();
    }

}
