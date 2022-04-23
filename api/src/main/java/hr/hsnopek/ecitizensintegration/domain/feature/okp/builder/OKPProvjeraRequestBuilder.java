package hr.hsnopek.ecitizensintegration.domain.feature.okp.builder;

import hr.apis_it.umu._2013.types.gsb.ContentType;
import hr.apis_it.umu._2013.types.gsb.GsbEnvelopeType;
import hr.apis_it.umu._2013.types.gsb.MessageHeaderType;
import hr.apis_it.umu._2013.types.gsb.SendMessageRequest;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import korisnickipretinacrest.ObjectFactory;
import korisnickipretinacrest.PinCountryCodeIdType;
import korisnickipretinacrest.UpitType;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class OKPProvjeraRequestBuilder {

    private String senderId;
    private String serviceId;
    private String idPosiljatelja;
    private String pinPrimatelja;
    private String countryCodeId;

    public static OKPProvjeraRequestBuilder create(){
        return new OKPProvjeraRequestBuilder();
    }

    public OKPProvjeraRequestBuilder setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public OKPProvjeraRequestBuilder setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public OKPProvjeraRequestBuilder setIdPosiljatelja(String idPosiljatelja) {
        this.idPosiljatelja = idPosiljatelja;
        return this;
    }
    public OKPProvjeraRequestBuilder setPinPrimatelja(String pinPrimatelja) {
        this.pinPrimatelja = pinPrimatelja;
        return this;
    }
    public OKPProvjeraRequestBuilder setCountryCodeId(String countryCodeId) {
        this.countryCodeId = countryCodeId;
        return this;
    }


    public SendMessageRequest build() throws DatatypeConfigurationException {

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        GsbEnvelopeType gsbEnvelopeType = new GsbEnvelopeType();
        ContentType contentType = new ContentType();
        ContentType.Data data = new ContentType.Data();

        // create GsbEnvelope element
        sendMessageRequest.setGsbEnvelope(gsbEnvelopeType);

        // create MessageHeader element inside GsbEnvelope
        MessageHeaderType messageHeaderType = createMessageHeader();
        gsbEnvelopeType.setMessageHeader(messageHeaderType);

        // create Content element inside GsbEnvelope
        gsbEnvelopeType.setContent(contentType);
        contentType.setMimeType("aa5");
        contentType.setData(data);

        data.setEncoding("EMBEDDED");

        UpitType upitType = createUpitType();
        upitType.setPinCountryCodeId(createPinCountryCodeIdType());

        JAXBElement<UpitType> upitTypeJAXBElement = new ObjectFactory().createUpit(upitType);

        data.getContent().add(upitTypeJAXBElement);

        return sendMessageRequest;
    }

    private PinCountryCodeIdType createPinCountryCodeIdType() {
        PinCountryCodeIdType pinCountryCodeId = new PinCountryCodeIdType();
        pinCountryCodeId.setCountryCodeId(this.countryCodeId);
        pinCountryCodeId.setPinPrimatelja(this.pinPrimatelja);
        return pinCountryCodeId;
    }

    private UpitType createUpitType() throws DatatypeConfigurationException {
        UpitType upitType = new UpitType();
        upitType.setIdPosiljatelja(this.idPosiljatelja);
        upitType.setIdUpita(UUID.randomUUID().toString());
        upitType.setDatumVrijemeUpita(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone(ZoneId.of("UTC+01:00")))));
        upitType.setTipPoruke(new BigInteger((ApplicationProperties.OKP_TIP_PORUKE)));
        return upitType;
    }

    private MessageHeaderType createMessageHeader() throws DatatypeConfigurationException {
        MessageHeaderType messageHeaderType = new MessageHeaderType();
        messageHeaderType.setSenderId(this.senderId);
        messageHeaderType.setServiceId(this.serviceId);
        messageHeaderType.setMessageId(UUID.randomUUID().toString());
        messageHeaderType.setSenderTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone(ZoneId.of("UTC+01:00")))));
        return messageHeaderType;
    }
}
