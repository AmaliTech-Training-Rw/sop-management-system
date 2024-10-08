package com.team.authentication_service.models;

import com.team.authentication_service.enums.Position;
import com.team.authentication_service.utils.ArrayToStringConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public int id;

    @Column(nullable = false)
    public String name;

    public String password;

    @Column(unique = true, nullable = false)
    public String email;

    @OneToOne
    @JoinColumn(name = "id")
    public Department departmentId;

    public Position position;

    public boolean isVerified;

    public String imageUrl;

    @Convert(converter = ArrayToStringConverter.class)
    public String[] roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return "";
    }
}
