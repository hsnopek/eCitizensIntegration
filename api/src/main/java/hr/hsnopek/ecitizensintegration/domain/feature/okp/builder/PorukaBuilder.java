package hr.hsnopek.ecitizensintegration.domain.feature.okp.builder;

import hr.hsnopek.ecitizensintegration.domain.feature.okp.validation.OKPValidationService;
import korisnickipretinac.PorukaType;

public class PorukaBuilder {

    private String pinPrimatelja;
    private String oznakaDrzave;
    private String predmet;
    private String sadrzaj;

    public static PorukaBuilder create(){
        return new PorukaBuilder();
    }

    public PorukaBuilder setPinPrimatelja(String pinPrimatelja) {
        this.pinPrimatelja = pinPrimatelja;
        return this;
    }

    public PorukaBuilder setOznakaDrzave(String oznakaDrzave) {
        this.oznakaDrzave = oznakaDrzave;
        return this;
    }

    public PorukaBuilder setPredmet(String predmet) {
        this.predmet = predmet;
        return this;
    }

    public PorukaBuilder setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
        return this;
    }

    public PorukaType build(){
        PorukaType porukaType = new PorukaType();
        porukaType.setPinPrimatelja(this.pinPrimatelja);
        porukaType.setOznakaDrzave(this.oznakaDrzave);
        porukaType.setPredmet(this.predmet);
        porukaType.setSadrzaj(this.sadrzaj);

        OKPValidationService.validatePoruka(porukaType);

        return porukaType;

    }
}
