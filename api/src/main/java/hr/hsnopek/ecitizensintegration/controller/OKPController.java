package hr.hsnopek.ecitizensintegration.controller;

import hr.apis_it.umu._2013.types.gsb.SendMessageResponse;
import hr.hsnopek.ecitizensintegration.domain.feature.okp.client.OKPClient;
import okpprovjera.OdgovorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

@RestController
@RequestMapping("/okp")
public class OKPController {

    private final OKPClient okpClient;

    public OKPController(OKPClient okpClient) {
        this.okpClient = okpClient;
    }

    @PostMapping("/send-message/{pinPrimatelja}")
    public ResponseEntity<?> sendMessage(@PathVariable String pinPrimatelja) throws DatatypeConfigurationException, JAXBException {
        SendMessageResponse sendMessageResponse = okpClient.sendTestMessage(pinPrimatelja);
        return ResponseEntity.status(HttpStatus.OK).body(sendMessageResponse);
    }

    @PostMapping("/provjera")
    public ResponseEntity<?> sendProvjeraMessage(@RequestParam String pinPrimatelja, @RequestParam String countryCodeId)
            throws DatatypeConfigurationException, JAXBException, SOAPException, ParserConfigurationException,
            IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SAXException, TransformerException {

        OdgovorType odgovorType = okpClient.sendProvjeraTestMessage(pinPrimatelja, countryCodeId);
        return ResponseEntity.status(HttpStatus.OK).body(odgovorType);
    }
}
