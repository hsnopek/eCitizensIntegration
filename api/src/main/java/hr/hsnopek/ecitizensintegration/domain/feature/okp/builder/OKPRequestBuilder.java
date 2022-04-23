package hr.hsnopek.ecitizensintegration.domain.feature.okp.builder;

import hr.apis_it.umu._2013.types.gsb.ContentType;
import hr.apis_it.umu._2013.types.gsb.GsbEnvelopeType;
import hr.apis_it.umu._2013.types.gsb.MessageHeaderType;
import hr.apis_it.umu._2013.types.gsb.SendMessageRequest;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.validation.OKPValidationService;
import korisnickipretinac.*;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class OKPRequestBuilder {

    private String senderId;
    private String serviceId;
    private String idPosiljatelja;
    private String razinaSigurnosti;
    private PorukaType poruka;
    private PriviciType privici;


    public static OKPRequestBuilder create(){
        return new OKPRequestBuilder();
    }

    public OKPRequestBuilder setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public OKPRequestBuilder setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public OKPRequestBuilder setIdPosiljatelja(String idPosiljatelja) {
        this.idPosiljatelja = idPosiljatelja;
        return this;
    }

    public OKPRequestBuilder setRazinaSigurnosti(String razinaSigurnosti) {
        this.razinaSigurnosti = razinaSigurnosti;
        return this;
    }

    public OKPRequestBuilder setPoruka(PorukaType poruka) {
        this.poruka = poruka;
        return this;
    }

    public OKPRequestBuilder setPrivici(List<PrivitakType> listaPrivitaka) {
        if(listaPrivitaka == null || listaPrivitaka.isEmpty())
            throw new RuntimeException("Element Privici ne smije biti prazan!");

        this.privici = new PriviciType();
        this.privici.getPrivitak().addAll(listaPrivitaka);
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
        contentType.setMimeType("application/xml");
        contentType.setData(data);

        // create KorisnickiPretinacPoruka element inside Content element
        data.setEncoding("EMBEDDED");
        JAXBElement<KorisnickiPretinacPorukaType> korisnickiPretinacPorukaType = createKorisnickiPretinacPoruka();
        data.getContent().add(korisnickiPretinacPorukaType);

        OKPValidationService.validateZaglavlje(messageHeaderType);
        OKPValidationService.validateContent(contentType);
        OKPValidationService.validatePoruka(korisnickiPretinacPorukaType.getValue());

        return sendMessageRequest;
    }


    private JAXBElement<KorisnickiPretinacPorukaType> createKorisnickiPretinacPoruka() throws DatatypeConfigurationException {
        KorisnickiPretinacPorukaType korisnickiPretinacPorukaType = new KorisnickiPretinacPorukaType();
        korisnickiPretinacPorukaType.setZaglavlje(createZaglavlje());
        korisnickiPretinacPorukaType.setPoruka(this.poruka);
        korisnickiPretinacPorukaType.setPrivici(this.privici);
        return new ObjectFactory().createKorisnickiPretinacPoruka(korisnickiPretinacPorukaType);
    }


    private ZaglavljeType createZaglavlje() throws DatatypeConfigurationException {
        ZaglavljeType zaglavljeType = new ZaglavljeType();
        zaglavljeType.setIdPoruke(UUID.randomUUID().toString());
        zaglavljeType.setTipPoruke(new BigInteger(ApplicationProperties.OKP_TIP_PORUKE));
        zaglavljeType.setDatumVrijemeSlanja(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone(ZoneId.of("UTC+01:00")))));
        zaglavljeType.setIdPosiljatelja(this.idPosiljatelja);
        zaglavljeType.setRazinaSigurnosti(this.razinaSigurnosti);
        return zaglavljeType;
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
