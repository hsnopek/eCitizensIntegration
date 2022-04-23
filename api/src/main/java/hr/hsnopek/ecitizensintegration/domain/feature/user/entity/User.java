package hr.hsnopek.ecitizensintegration.domain.feature.user.entity;

import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.role.entity.Role;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="USER")
public class User {

    public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    // NAME_ID - certificate type
    @Column(name="IDENT", nullable = false)
    private String ident;

    // DN - logged in via personal certificate or ePass
    @Column(name="OIB", nullable = false)
    private String oib;

    // OIB - logged in via business certificate
    @Column(name="DN", nullable = true)
    private String dn;


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "user")
    private List<UserSession> userSessions;

    @Column(name="ACTIVE", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    @Column(name = "TSTAMP")
    @CreationTimestamp
    private LocalDateTime tstamp;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "USER_ROLE",
            joinColumns = {
                    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")})
    private Set<Role> roles = new HashSet<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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

    public List<UserSession> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(List<UserSession> userSessions) {
        this.userSessions = userSessions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getTstamp() {
        return tstamp;
    }

    public void setTstamp(LocalDateTime tstamp) {
        this.tstamp = tstamp;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
