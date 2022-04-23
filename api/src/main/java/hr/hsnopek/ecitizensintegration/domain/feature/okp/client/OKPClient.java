package hr.hsnopek.ecitizensintegration.domain.feature.okp.client;

import hr.apis_it.umu._2013.services.gsbservice.GSBServicePortType;
import hr.apis_it.umu._2013.types.gsb.*;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.OKPProvjeraRequestBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.OKPRequestBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.PorukaBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.PrivitakBuilder;
import hr.hsnopek.ecitizensintegration.general.util.GenericHttpClient;
import hr.hsnopek.ecitizensintegration.general.util.SOAPUtils;
import okpprovjera.ObjectFactory;
import okpprovjera.OdgovorType;
import okpprovjera.SendMessageRequestType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.*;
import java.util.List;

@Service
public class OKPClient {

    private final GSBServicePortType gsbService;
    private final KeyStore appKeyStore;
    private final KeyStore appTrustStore;

    public OKPClient(GSBServicePortType gsbService, KeyStore appKeyStore, KeyStore appTrustStore) {
        this.gsbService = gsbService;
        this.appKeyStore = appKeyStore;
        this.appTrustStore = appTrustStore;
    }

    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest) throws DatatypeConfigurationException, JAXBException {
        return this.gsbService.sendMessage(sendMessageRequest);
    }

    public SendMessageResponse sendTestMessage(String pinPrimatelja) throws DatatypeConfigurationException, JAXBException {
        SendMessageRequest sendMessageRequest = OKPRequestBuilder.create()
                .setSenderId(ApplicationProperties.OKP_SENDER_ID)
                .setServiceId(ApplicationProperties.OKP_SERVICE_ID)
                .setIdPosiljatelja(ApplicationProperties.OKP_ID_POSILJATELJA)
                .setRazinaSigurnosti(ApplicationProperties.OKP_SIGURNOSNA_RAZINA_PORUKE)
                .setPoruka(PorukaBuilder.create()
                        .setPinPrimatelja(pinPrimatelja)
                        .setOznakaDrzave("HR")
                        .setPredmet("Neka testna poruka")
                        .setSadrzaj("U2FkcsW+YWogbmVrZSB0ZXN0bmUgcG9ydWtl")
                        .build())
                .setPrivici(List.of(
                        PrivitakBuilder.create()
                                .setNaziv("TestniPdf")
                                .setMimeType("application/pdf")
                                .setOpis("Ovo je testni pdf file.")
                                .setData("bmVraSB0ZWtzdA==")
                                .build()))
                .build();

        return sendMessage(sendMessageRequest);
    }

    public OdgovorType sendProvjeraTestMessage(String pinPrimatelja, String countryCodeID)
            throws DatatypeConfigurationException, JAXBException, ParserConfigurationException, SOAPException, IOException,
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SAXException, TransformerException {

        SendMessageRequestType sendMessageRequest = OKPProvjeraRequestBuilder.create()
                .setSenderId(ApplicationProperties.OKP_SENDER_ID)
                .setServiceId("124")
                .setIdPosiljatelja(ApplicationProperties.OKP_ID_POSILJATELJA)
                .setPinPrimatelja(pinPrimatelja)
                .setCountryCodeId(countryCodeID)
                .build();

        JAXBElement<SendMessageRequestType> sendMessageRequestTypeJAXBElement =
                new ObjectFactory().createSendMessageRequest(sendMessageRequest);

        SOAPMessage soapMessage = SOAPUtils.addJaxbElementToSoapMessageBody(sendMessageRequestTypeJAXBElement);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "https://cistest.apis-it.hr:8450/GSBServiceTest");
        soapMessage.saveChanges();

        SOAPMessage response = callSoapWebService(soapMessage, ApplicationProperties.OKP_SERVICE_URL);

        return SOAPUtils.stripSoapEnvelope(
                response,
                OdgovorType.class,
                "Odgovor");
    }

    private SOAPMessage callSoapWebService(SOAPMessage soapMessage, String soapEndpointUrl) throws UnrecoverableKeyException, SOAPException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, ParserConfigurationException, SAXException {
        try{
            HttpPost httpPost = new HttpPost(soapEndpointUrl);
            if(soapMessage != null){
                String soapMessageStr = SOAPUtils.marshall(soapMessage);
                StringEntity stringEntity = new StringEntity(soapMessageStr);
                httpPost.setEntity(stringEntity);
            }
            GenericHttpClient genericHttpClient = new GenericHttpClient();
            CloseableHttpClient httpClient = genericHttpClient.getHttpClient(
                    appKeyStore,
                    ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD,
                    appTrustStore,
                    String.valueOf(ContentType.APPLICATION_XML)
            );
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
            HttpEntity entity = closeableHttpResponse.getEntity();
            if(entity != null){
                return SOAPUtils.unmarshall(EntityUtils.toString(entity));
            }
        } catch(Exception e){
            throw e;
        }
        return null;
    }



}
