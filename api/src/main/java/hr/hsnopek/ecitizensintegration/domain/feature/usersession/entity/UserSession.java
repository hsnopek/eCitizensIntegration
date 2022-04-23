package hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity;

import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;

import javax.persistence.*;

@Entity
@Table(name="USER_SESSION")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_session_id", nullable = false)
    private Long userSessionId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name="NAV_TOKEN", nullable = false)
    private String navToken;

    @Column(name="SESSION_INDEX", nullable = false)
    private String sessionIndex;

    @Column(name="SESSION_ID", nullable = true)
    private String sessionId;

    @Column(name="IN_RESPONSE_TO", nullable = true)
    private String inResponseTo;

    public Long getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(Long userSessionId) {
        this.userSessionId = userSessionId;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(String inResponseTo) {
        this.inResponseTo = inResponseTo;
    }
}
