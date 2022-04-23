package hr.hsnopek.ecitizensintegration.domain.feature.person.entity;

import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="PERSON")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long personId;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "person")
    private List<User> users;

    @Column(name="TID", nullable = false)
    private String tid;
    @Column(name="STATE_CODE", nullable = false)
    private String stateCode;
    @Column(name="FIRST_NAME", nullable = false)
    private String firstName;
    @Column(name="LAST_NAME", nullable = false)
    private String lastName;
    @Column(name="DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;
    @Column(name="NAME_AT_BIRTH", nullable = true)
    private String firstNameAndFamilyNameAtBirth;
    @Column(name="PLACE_OF_BIRTH", nullable = true)
    private String placeOfBirth;
    @Column(name="CURRENT_ADDRESS", nullable = true)
    private String currentAddress;
    @Column(name="GENDER", nullable = true)
    private String gender;

    @Column(name = "TSTAMP")
    @CreationTimestamp
    private LocalDateTime tstamp;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long id) {
        this.personId = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstNameAndFamilyNameAtBirth() {
        return firstNameAndFamilyNameAtBirth;
    }

    public void setFirstNameAndFamilyNameAtBirth(String firstNameAndFamilyNameAtBirth) {
        this.firstNameAndFamilyNameAtBirth = firstNameAndFamilyNameAtBirth;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public LocalDateTime getTstamp() {
        return tstamp;
    }

    public void setTstamp(LocalDateTime tstamp) {
        this.tstamp = tstamp;
    }
}
