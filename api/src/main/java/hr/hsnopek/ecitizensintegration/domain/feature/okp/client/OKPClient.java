package hr.hsnopek.ecitizensintegration.domain.feature.okp.client;

import hr.apis_it.umu._2013.services.gsbservice.GSBServicePortType;
import hr.apis_it.umu._2013.types.gsb.SendMessageRequest;
import hr.apis_it.umu._2013.types.gsb.SendMessageResponse;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.OKPProvjeraRequestBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.OKPRequestBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.PorukaBuilder;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.builder.PrivitakBuilder;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.List;

@Service
public class OKPClient {

    private final GSBServicePortType gsbService;

    public OKPClient(GSBServicePortType gsbService) {
        this.gsbService = gsbService;
    }

    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
        return this.gsbService.sendMessage(sendMessageRequest);
    }

    public SendMessageResponse sendTestMessage(String pinPrimatelja) throws DatatypeConfigurationException {
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

    public SendMessageResponse sendProvjeraTestMessage(String pinPrimatelja, String countryCodeID)
            throws DatatypeConfigurationException {

        SendMessageRequest sendMessageRequest = OKPProvjeraRequestBuilder.create()
                .setSenderId(ApplicationProperties.OKP_SENDER_ID)
                .setServiceId("124")
                .setIdPosiljatelja(ApplicationProperties.OKP_ID_POSILJATELJA)
                .setPinPrimatelja(pinPrimatelja)
                .setCountryCodeId(countryCodeID)
                .build();

        return sendMessage(sendMessageRequest);
    }
}
