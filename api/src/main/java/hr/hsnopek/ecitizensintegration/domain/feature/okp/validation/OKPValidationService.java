package hr.hsnopek.ecitizensintegration.domain.feature.okp.validation;

import hr.apis_it.umu._2013.types.gsb.ContentType;
import hr.apis_it.umu._2013.types.gsb.MessageHeaderType;
import hr.hsnopek.ecitizensintegration.general.util.OibValidation;
import korisnickipretinac.KorisnickiPretinacPorukaType;
import korisnickipretinac.PorukaType;
import korisnickipretinac.PriviciType;
import korisnickipretinac.PrivitakType;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

public class OKPValidationService {

    public static void validateZaglavlje(MessageHeaderType messageHeaderType){
        if(
                messageHeaderType.getSenderId() == null || messageHeaderType.getServiceId() == null ||
                messageHeaderType.getMessageId() == null || messageHeaderType.getSenderTimeStamp() == null
        ) {
            throw new RuntimeException("Message header is invalid. Fields senderId, serviceId, messageId and senderTimestamp are required.");
        }
    }

    public static void validateContent(ContentType contentType){
        if(contentType.getMimeType() == null || contentType.getData() == null){
            throw new RuntimeException("Fields mimeType and data are required.");
        }
    }

    public static void validatePoruka(PorukaType porukaType){
        if(porukaType.getPinPrimatelja() == null || porukaType.getOznakaDrzave() == null
                || porukaType.getPredmet() == null ||porukaType.getSadrzaj() == null)
            throw new RuntimeException("Fields pinPrimatelja, oznakaDrzave, predmet and sadrzaj are required.");

        if(OibValidation.checkOIB(porukaType.getPinPrimatelja()) != OibValidation.NO_ERROR){
            throw new RuntimeException("OIB is invalid.");
        }
    }

    public static void validatePrimitak(PrivitakType privitakType){
        if(privitakType.getNaziv() == null || privitakType.getMimeType() == null || privitakType.getData() == null){
            throw new RuntimeException("Fields naziv, mimeType and data are required.");
        }
    }

    public static void validatePoruka(KorisnickiPretinacPorukaType korisnickiPretinacPorukaType){
        byte[] message = korisnickiPretinacPorukaType.getPoruka().getSadrzaj().getBytes(StandardCharsets.UTF_8);
        boolean messageBase64encoded = Base64.isBase64(message);

        if(!messageBase64encoded){
            throw new RuntimeException("Message must be base64 encoded!");
        } else {
            if(message.length > 2000)
                throw new RuntimeException("Max. message size is 2000 characters");
        }
    }
}
