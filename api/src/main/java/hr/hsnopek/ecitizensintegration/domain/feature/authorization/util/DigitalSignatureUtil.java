package hr.hsnopek.ecitizensintegration.domain.feature.authorization.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import hr.hsnopek.ecitizensintegration.domain.feature.authorization.exceptions.RegistrationFormException;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class DigitalSignatureUtil {

    private  static final Logger logger = LoggerFactory.getLogger(DigitalSignatureUtil.class);
    private static final String SCHEMA_PATH = "authorization/RegistrationFormSchema/AuthorizationResponseV3.xsd";
    private static final String SCHEMA_PATH_RO_AUTH = "authorization/eOvlastenjaApiSchema/RoAuthUnionApiSchemaV2.xsd";

    private final X509Certificate appCert;
    private final PrivateKey privateKey;
    private final PublicKey authorizationServicePublicKey;
    private final PublicKey publicKey;
    @Value("classpath:"+SCHEMA_PATH)
    Resource xsdSchemaResource;
    @Value("classpath:"+SCHEMA_PATH_RO_AUTH)
    Resource xsdSchemaResourceRoAuth;

    public DigitalSignatureUtil(X509Certificate appCert, PrivateKey appCertPrivateKey, PublicKey authorizationServicePublicKey, PublicKey appCertPublicKey) {
        this.appCert = appCert;
        this.privateKey = appCertPrivateKey;
        this.authorizationServicePublicKey = authorizationServicePublicKey;
        this.publicKey = appCertPublicKey;
    }

    @PostConstruct
    public void init() {
        Init.init();
    }

    private Document getXmlDocument(InputStream pXMLDocumentStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory tBuilder = DocumentBuilderFactory.newInstance();
        tBuilder.setNamespaceAware(true);
        tBuilder.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsdSchemaResource.getFile()));
        return tBuilder.newDocumentBuilder().parse(pXMLDocumentStream);
    }

    private Document getXmlDocumentRoAuthorization(InputStream pXMLDocumentStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory tBuilder = DocumentBuilderFactory.newInstance();
        tBuilder.setNamespaceAware(true);
        tBuilder.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsdSchemaResourceRoAuth.getFile()));
        return tBuilder.newDocumentBuilder().parse(pXMLDocumentStream);
    }

    public byte[] signFile(String pXMLDocument) {
        Document tDoc = null;
        byte[] tSignedFile = null;
        try (InputStream pXMLStream = new ByteArrayInputStream(pXMLDocument.getBytes(StandardCharsets.UTF_8))) {
            tDoc = getXmlDocument(pXMLStream);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RegistrationFormException("Dohvat dokumenta nije uspio.");
        }
        try {
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");
            XMLSignature tSignature = new XMLSignature(tDoc, null, org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            Transforms tTransforms = new Transforms(tDoc);
            tTransforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            tTransforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            tSignature.addDocument("#" + tDoc.getDocumentElement().getAttribute("Id"), tTransforms, Constants.ALGO_ID_DIGEST_SHA1);
            tSignature.addKeyInfo(appCert);
            tSignature.addKeyInfo(publicKey);
            tSignature.sign(privateKey);
            tDoc.getElementsByTagNameNS("*", "Signatures").item(0).appendChild(tSignature.getElement());
        } catch (XMLSecurityException e) {
            throw new RegistrationFormException("Potpisivanje dokumenta nije uspjelo.");
        } try {
            tSignedFile = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS).canonicalizeSubtree(tDoc);
        } catch (CanonicalizationException | InvalidCanonicalizerException e) {
            throw new RegistrationFormException("Transformacija dokumenta nije uspjela.");
        } try {
            String tSignedDebugString = new String(tSignedFile, "UTF-8");
            logger.debug("Potpisani ServiceResponse XML: {}", tSignedDebugString);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return tSignedFile;
    }


    public void validateSignature(byte[] pXMLDocument) {
        boolean tValidFlag = false;
        Document tDoc = null;
        try (InputStream tXMLDocumentStream = new ByteArrayInputStream(pXMLDocument)) {
            tDoc = getXmlDocument(tXMLDocumentStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RegistrationFormException("Dohvat dokumenta nije uspio.");
        }
        NodeList tNodeList = (((Element) tDoc.getElementsByTagName("Signatures").item(0))
                .getElementsByTagName("Signature"));
        if (tNodeList.getLength() == 0) {
            throw new RegistrationFormException("Digitalni potpis nije pronaden.");
        }
        DOMValidateContext tValContext = new DOMValidateContext(authorizationServicePublicKey, tNodeList.item(0));
        XMLSignatureFactory tSigFactory = XMLSignatureFactory.getInstance("DOM");
        try {
            javax.xml.crypto.dsig.XMLSignature tSignature = tSigFactory.unmarshalXMLSignature(tValContext);
            tValidFlag = tSignature.validate(tValContext);
        } catch (XMLSignatureException | MarshalException e) {
            throw new RegistrationFormException("Validacija ili marshaliranje nisu uspjeli.");
        }
        if (!tValidFlag) {
            throw new RegistrationFormException("Potpis u XML dokumentu nije validan.");
        }
    }

    public void validateSignatureRoAuthorization(byte[] pXMLDocument) throws SAXException, IOException, ParserConfigurationException {
        boolean tValidFlag = false;
        Document tDoc = null;
        try (InputStream tXMLDocumentStream = new ByteArrayInputStream(pXMLDocument)) {
            tDoc = getXmlDocumentRoAuthorization(tXMLDocumentStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RegistrationFormException("Dohvat dokumenta nije uspio.");
        }
        NodeList tNodeList = (((Element) tDoc.getElementsByTagName("Signatures").item(0))
                .getElementsByTagName("Signature"));
        if (tNodeList.getLength() == 0) {
            throw new RegistrationFormException("Digitalni potpis nije pronaden.");
        }
        DOMValidateContext tValContext = new DOMValidateContext(authorizationServicePublicKey, tNodeList.item(0));
        XMLSignatureFactory tSigFactory = XMLSignatureFactory.getInstance("DOM");
        try {
            javax.xml.crypto.dsig.XMLSignature tSignature = tSigFactory.unmarshalXMLSignature(tValContext);
            tValidFlag = tSignature.validate(tValContext);
        } catch (XMLSignatureException | MarshalException e) {
            throw new RegistrationFormException("Validacija ili marshaliranje nisu uspjeli.");
        }
        if (!tValidFlag) {
            throw new RegistrationFormException("Potpis u XML dokumentu nije validan.");
        }
    }

}
