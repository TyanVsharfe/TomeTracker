package com.tometracker.db.model;

import com.tometracker.data_template.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    @Setter
    private String username;
    @Setter
    private String password;
    @Setter
    private String email;
    @Setter
    private Enums.subscription subscription;
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public User() {}
    public User(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.subscription = Enums.subscription.Free;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
