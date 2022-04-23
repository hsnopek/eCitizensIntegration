package hr.hsnopek.ecitizensintegration.domain.feature.okp.builder;

import hr.hsnopek.ecitizensintegration.domain.feature.okp.validation.OKPValidationService;
import korisnickipretinac.PrivitakType;

public class PrivitakBuilder {

    private String naziv;
    private String mimeType;
    private String opis;
    private String data;


    public static PrivitakBuilder create(){
        return new PrivitakBuilder();
    }
    public PrivitakBuilder setNaziv(String naziv) {
        this.naziv = naziv;
        return this;
    }

    public PrivitakBuilder setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public PrivitakBuilder setOpis(String opis) {
        this.opis = opis;
        return this;
    }

    public PrivitakBuilder setData(String data) {
        this.data = data;
        return this;
    }

    public PrivitakType build(){
        PrivitakType privitakType = new PrivitakType();
        privitakType.setNaziv(this.naziv);
        privitakType.setMimeType(this.mimeType);
        privitakType.setOpis(this.opis);

        PrivitakType.Data data = new PrivitakType.Data();
        data.setEncoding("BASE64");
        data.getContent().add(this.data);
        privitakType.setData(data);

        OKPValidationService.validatePrimitak(privitakType);
        return privitakType;

    }

}
