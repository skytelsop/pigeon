package com.example.pigeon.config;

import com.example.pigeon.model.CompanyType;
import com.example.pigeon.model.Privilege;
import com.example.pigeon.model.Role;
import com.example.pigeon.model.User;
import com.example.pigeon.repository.PrivilegeRepository;
import com.example.pigeon.repository.RoleRepository;
import com.example.pigeon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if(alreadySetup) { return; }

        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);


        createUserIfNotFound("test@test.com",
                "Test",
                "Test",
                "test",
                CompanyType.PERSONAL,
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                new ArrayList<>(Arrays.asList(adminRole)));

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if(privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }

        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if(role == null) {
            role = new Role(name);
        }

        role.setPrivileges(privileges);
        role = roleRepository.save(role);

        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email,
                              final String firstName,
                              final String lastName,
                              final String companyName,
                              final CompanyType companyType,
                              final String phoneNumber,
                              final String password,
                              final String referenceName,
                              final String postalCode,
                              final String streetAddress,
                              final String state,
                              final String city,
                              final String country,
                              final Collection<Role> roles) {

        User user = userRepository.findByEmail(email);

        if(user == null) {

            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCompanyName(companyName);
            user.setCompanyType(companyType);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhoneNumber(phoneNumber);
            user.setReferenceName(referenceName);
            user.setPostalCode(postalCode);
            user.setStreetAddress(streetAddress);
            user.setState(state);
            user.setCity(city);
            user.setCountry(country);
            user.setEnabled(true);
        }

        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
}
