package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.messages;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.CheckMethods;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts.AbstractResponseToken;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.schema.XSString;


public class AuthnResponseToken extends AbstractResponseToken {
    public Response authnResponse;


    @Override
    public StatusResponseType getResponse() {
        return authnResponse;
    }

    public AuthnResponseToken(Response authnResponse, MetadataInfo metadata) {
        this.authnResponse = authnResponse;
        this.metadata = metadata;
    }

    public boolean IsValid() {
        return CheckMethods.checkAuthnResponse(this);
    }

    private String tryGetAttribute(String attributeName) {
        if (this.authnResponse != null && this.authnResponse.getAssertions().size() == 1 && this.authnResponse.getAssertions().get(0).getAttributeStatements().size() == 1) {
            for (Assertion assertion : this.authnResponse.getAssertions()) {
                for (AttributeStatement statement : assertion.getAttributeStatements()) {
                    for (Attribute attr : statement.getAttributes()) {
                        if (attr.getName().equals(attributeName)) {
                            System.out.println("atr: " + attr.getName() + ", value: " + ((XSString) attr.getAttributeValues().get(0)).getValue());
                            return ((XSString) attr.getAttributeValues().get(0)).getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getIme() {
        return tryGetAttribute("ime");
    }

    public String getPrezime() {
        return tryGetAttribute("prezime");
    }

    public String getTID() {
        return tryGetAttribute("tid");
    }

    public String getDN() {
        return tryGetAttribute("dn");
    }

    public String getPosId() {
        return tryGetAttribute("pos_id");
    }

    public String getPosNaziv() {
        return tryGetAttribute("pos_naziv");
    }

    public String getOznakaDrzaveEid() {
        return tryGetAttribute("oznaka_drzave_eid");
    }

    public String getNavToken() {
        return tryGetAttribute("nav_token");
    }

    public String getOIB() {
        return tryGetAttribute("oib");
    }

    public String getIps() {
        return tryGetAttribute("ips");
    }

    public String getIzvorReg() {
        return tryGetAttribute("izvor_reg");
    }

    public String getDatRod() {
        return tryGetAttribute("dat_rod");
    }

    public String getSesijaId() {
        return tryGetAttribute("sesija_id");
    }

    public String getCurrentFamilyName() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
    }

    public String getCurrentGivenName() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName");
    }

    public String getDateOfBirth() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/DateOfBirth");
    }

    public String getPersonIdentifier() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
    }

    public String getBirthName() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/BirthName");
    }

    public String getPlaceOfBirth() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/PlaceOfBirth");
    }

    public String getCurrentAddress() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/CurrentAddress");
    }

    public String getGender() {
        return tryGetAttribute("http://eidas.europa.eu/attributes/naturalperson/Gender");
    }

    public String getSessionIndex() {
        return this.authnResponse.getAssertions().get(0).getAuthnStatements().get(0).getSessionIndex();
    }

    public String getInResponseTo(){
        return this.authnResponse.getInResponseTo();
    }
}
