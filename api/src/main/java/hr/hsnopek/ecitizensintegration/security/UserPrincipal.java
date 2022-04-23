package hr.hsnopek.ecitizensintegration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    String username;
    List<SimpleGrantedAuthority> authorities;

    String jwt;

    public UserPrincipal(String username, List<SimpleGrantedAuthority> authorities){
        this.username = username;
        this.authorities = authorities;
    }

    public UserPrincipal(String username, List<SimpleGrantedAuthority> authorities, String jwt){
        this.username = username;
        this.authorities = authorities;
        this.jwt = jwt;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getToken(){
        return this.jwt;
    }
}
