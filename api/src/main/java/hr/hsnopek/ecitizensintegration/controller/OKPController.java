package hr.hsnopek.ecitizensintegration.controller;

import hr.apis_it.umu._2013.types.gsb.SendMessageResponse;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.client.OKPClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

@RestController
@RequestMapping("/okp")
public class OKPController {

    private final OKPClient okpClient;

    public OKPController(OKPClient okpClient) {
        this.okpClient = okpClient;
    }

    @PostMapping(value = "/send-message/{pinPrimatelja}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> sendMessage(@PathVariable String pinPrimatelja) throws DatatypeConfigurationException, JAXBException {
        SendMessageResponse sendMessageResponse = okpClient.sendTestMessage(pinPrimatelja);
        return ResponseEntity.status(HttpStatus.OK).body(sendMessageResponse);
    }

    @PostMapping(value = "/provjera", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> sendProvjeraMessage(@RequestParam String pinPrimatelja, @RequestParam String countryCodeId)
            throws DatatypeConfigurationException, JAXBException {

        SendMessageResponse sendMessageResponse = okpClient.sendProvjeraTestMessage(pinPrimatelja, countryCodeId);
        return ResponseEntity.status(HttpStatus.OK).body(sendMessageResponse);
    }
}
