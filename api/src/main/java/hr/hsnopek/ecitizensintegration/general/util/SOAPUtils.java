package hr.hsnopek.ecitizensintegration.general.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class SOAPUtils<T> {

    public static String marshall(SOAPMessage soapMessage){
        final StringWriter sw = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(soapMessage.getSOAPPart()),
                    new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static SOAPMessage unmarshall(String message) throws SOAPException, IOException {
        InputStream is = new ByteArrayInputStream(message.getBytes());
        return MessageFactory.newInstance().createMessage(null, is);
    }

    public static SOAPMessage addJaxbElementToSoapMessageBody(JAXBElement<?> jaxbElement)
            throws SOAPException, ParserConfigurationException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPBody soapBody = soapMessage.getSOAPBody();

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXB.marshal(jaxbElement, new DOMResult(document));
        Element element = document.getDocumentElement();
        soapBody.addDocument(element.getOwnerDocument());
        return soapMessage;
    }

    public static Document toDocument(SOAPMessage soapMsg)
            throws TransformerException, SOAPException {
        Source src = soapMsg.getSOAPPart().getContent();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(src, result);
        return (Document)result.getNode();
    }

    public static <T> T stripSoapEnvelope(SOAPMessage soapMessage, Class<T> clazz, String tagName)
            throws SOAPException, JAXBException, TransformerException {
        JAXBContext jbc = JAXBContext.newInstance(clazz);
        Unmarshaller um = jbc.createUnmarshaller();

        Document document = toDocument(soapMessage);

        if(tagName != null){
            Node node =  document.getElementsByTagName(tagName).item(0);
            return (T) unmarshalNode(node, clazz);
        } else {
            return (T) um.unmarshal(document, clazz);
        }
    }

    private static <T> T unmarshalNode(Node node, Class<T> clazz) throws JAXBException {
        Source xmlSource = new DOMSource(node);
        Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        return unmarshaller.unmarshal(xmlSource, clazz).getValue();
    }



}
