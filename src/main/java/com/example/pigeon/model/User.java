package com.example.pigeon.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

import org.jboss.aerogear.security.otp.api.Base32;

@Getter
@Setter
@Entity
@Table(name = "user_account")
public class User {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String companyName;
    private CompanyType companyType;
    private String email;
    @Column(length = 60)
    private String password;
    private String phoneNumber;
    private String referenceName;
    private String postalCode;
    private String streetAddress;
    private String state;
    private String city;
    private String country;
    private boolean enabled = false;
    private boolean isUsing2FA = false;
    private String secret;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!getEmail().equals(user.getEmail())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=")
                .append(id)
                .append(", firstName=").append(firstName)
                .append(", lastName=").append(lastName)
                .append(", companyName=").append(companyName)
                .append(", companyType=").append(companyType)
                .append(", email=").append(email)
                .append(", phoneNumber=").append(phoneNumber)
                .append(", referenceName=").append(referenceName)
                .append(", postalCode=").append(postalCode)
                .append(", streetAddress=").append(streetAddress)
                .append(", state=").append(state)
                .append(", city=").append(city)
                .append(", country=").append(country)
                .append(", enabled=").append(enabled)
                .append(", isUsing2FA=").append(isUsing2FA)
                .append(", secret=").append(secret)
                .append(", roles=").append(roles)
                .append("]");
        return builder.toString();
    }
}