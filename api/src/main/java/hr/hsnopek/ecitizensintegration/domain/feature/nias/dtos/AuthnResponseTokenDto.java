package hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos;

public class AuthnResponseTokenDto {

    private String firstName;
    private String lastName;
    private String birthName;
    private String ident;
    private String oib;
    private String dn;
    private String tid;

    private String stateCode;

    private String dateOfBirth;
    private String personIdentifier;
    private String placeOfBirth;
    private String currentAddress;
    private String gender;

    private String navToken;
    private String sessionIndex;
    private String sesijaId;
    private String ips;
    private String izvorReg;
    private String posId;
    private String posNaziv;
    private String posOib;

    private String inResponseTo;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPersonIdentifier() {
        return personIdentifier;
    }

    public void setPersonIdentifier(String personIdentifier) {
        this.personIdentifier = personIdentifier;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getNavToken() {
        return navToken;
    }

    public void setNavToken(String navToken) {
        this.navToken = navToken;
    }

    public String getSessionIndex() {
        return sessionIndex;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getIzvorReg() {
        return izvorReg;
    }

    public void setIzvorReg(String izvorReg) {
        this.izvorReg = izvorReg;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getPosNaziv() {
        return posNaziv;
    }

    public void setPosNaziv(String posNaziv) {
        this.posNaziv = posNaziv;
    }

    public String getPosOib() {
        return posOib;
    }

    public void setPosOib(String posOib) {
        this.posOib = posOib;
    }

    public String getSesijaId() {
        return sesijaId;
    }

    public void setSesijaId(String sesijaId) {
        this.sesijaId = sesijaId;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
    }
}
