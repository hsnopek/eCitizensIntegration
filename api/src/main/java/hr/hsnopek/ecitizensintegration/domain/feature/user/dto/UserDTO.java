package hr.hsnopek.ecitizensintegration.domain.feature.user.dto;

public class UserDTO {

    private String firstName;
    private String lastName;
    private String ident;
    private String tid;
    private String deviceId;
    private String state;
    private String navToken;
    private String sessionIndex;
    private String sessionId;
    private String inResponseTo;

    private Boolean isLocalUser;

    private String roles;

    public UserDTO() {
    }

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

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getLocalUser() {
        return isLocalUser;
    }

    public void setLocalUser(Boolean localUser) {
        isLocalUser = localUser;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
    }
}
