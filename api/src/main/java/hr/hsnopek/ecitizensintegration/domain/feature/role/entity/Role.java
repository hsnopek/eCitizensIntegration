package hr.hsnopek.ecitizensintegration.domain.feature.role.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.hsnopek.ecitizensintegration.domain.feature.role.converters.RoleNameConverter;
import hr.hsnopek.ecitizensintegration.domain.feature.role.enumeration.RoleNameEnum;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "ROLE")
@Table(name = "ROLE")
public class Role {

    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_NAME")
    @Convert(converter = RoleNameConverter.class)
    @NaturalId
    private RoleNameEnum roleName;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> userList = new HashSet<>();

    public Role() {

    }

    public Role(RoleNameEnum roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleNameEnum getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleNameEnum roleName) {
        this.roleName = roleName;
    }

    public Set<User> getUserList() {
        return userList;
    }

    public void setUserList(Set<User> userList) {
        this.userList = userList;
    }



}
