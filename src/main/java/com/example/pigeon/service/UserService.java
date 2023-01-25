package com.example.pigeon.service;

import com.example.pigeon.web.dto.UserDto;
import com.example.pigeon.web.exception.UserAlreadyExistException;
import com.example.pigeon.model.*;
import com.example.pigeon.repository.*;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private NewLocationTokenRepository newLocationTokenRepository;

    @Autowired
    private Environment environment;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Pigeon";

    @Override
    public User registerNewUserAccount(final UserDto accountDto) {

        if(emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
        }
        final User user = new User();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setCompanyName(accountDto.getCompanyName());

        // TODO:
        user.setCompanyType(CompanyType.PERSONAL);

        user.setEmail(accountDto.getEmail());
        user.setPassword(accountDto.getPassword());
        user.setPhoneNumber(accountDto.getPhoneNumber());
        user.setReferenceName(accountDto.getReferenceName());
        user.setPostalCode(accountDto.getPostalCode());
        user.setStreetAddress(accountDto.getStreetAddress());
        user.setState(accountDto.getState());
        user.setCity(accountDto.getCity());
        user.setCountry(accountDto.getCountry());
        user.setUsing2FA(accountDto.isUsing2FA());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        return userRepository.save(user);
    }

    @Override
    public User getUser(final String verificationToken) {

        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if(token != null) {
            return token.getUser();
        }
        return null;
    }

    private boolean emailExists(final String email) { return userRepository.findByEmail(email) != null; }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(final User user) {

        userRepository.save(user);
    }

    @Override
    public void deleteUser(final User user) {

        final VerificationToken verificationToken = tokenRepository.findByUser(user);

        if(verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user);

        if(passwordResetToken != null) {
            passwordResetTokenRepository.delete(passwordResetToken);
        }

        userRepository.delete(user);
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken mToken = new VerificationToken(token, user);
        tokenRepository.save(mToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String token) {

        VerificationToken vToken = tokenRepository.findByToken(token);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {

        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {

        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(final String token) {

        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public Optional<User> getUserByID(final long id) {

        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(final User user, final String password) {

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(final User user, final String password) {

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {

        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if(verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {

            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        userRepository.save(user);

        return TOKEN_VALID;
    }

    @Override
    public String generateQRUrl(User user) throws UnsupportedEncodingException {

        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, user.getEmail(), user.getSecret(), APP_NAME), "UTF-8");
    }

    @Override
    public User updateUser2FA(boolean use2FA) {

        final Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) curAuth.getPrincipal();
        currentUser.setUsing2FA(use2FA);
        currentUser = userRepository.save(currentUser);
        final Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return currentUser;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {

        return sessionRegistry.getAllPrincipals()
                .stream().filter((u) -> !sessionRegistry.getAllSessions(u, false)
                        .isEmpty()).map(o -> {
                            if (o instanceof User) {
                                return ((User) o).getEmail();
                            } else {
                                return o.toString();
                            }
                        }).collect(Collectors.toList());
    }

    @Override
    public NewLocationToken isNewLoginLocation(String username, String ip) {
        
        if(!isGeoIpLibEnabled()) {
            return null;
        }

        try {
            final InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress).getCountry().getName();
            System.out.println(country + "====****");
            final User user = userRepository.findByEmail(username);
            final UserLocation loc = userLocationRepository.findByCountryAndUser(country, user);
            if((loc == null) || !loc.isEnabled()) {
                return createNewLocationToken(country, user);
            }
        } catch (final Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public String isValidNewLocationToken(String token) {

        final NewLocationToken locationToken = newLocationTokenRepository.findByToken(token);
        if(locationToken == null) {
            return null;
        }
        UserLocation userLocation = locationToken.getUserLocation();
        userLocation.setEnabled(true);
        userLocation = userLocationRepository.save(userLocation);
        newLocationTokenRepository.delete(locationToken);

        return userLocation.getCountry();
    }

    @Override
    public void addUserLocation(User user, String ip) {

        if(!isGeoIpLibEnabled()) {
            return;
        }

        try {
            final InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress).getCountry().getName();
            UserLocation loc = new UserLocation(country, user);
            loc.setEnabled(true);
            userLocationRepository.save(loc);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isGeoIpLibEnabled() {

        return Boolean.parseBoolean(environment.getProperty("geo.ip.lib.enabled"));
    }


    private NewLocationToken createNewLocationToken(String country, User user) {

        UserLocation location = new UserLocation(country, user);
        location = userLocationRepository.save(location);

        final NewLocationToken token = new NewLocationToken(UUID.randomUUID().toString(), location);

        return newLocationTokenRepository.save(token);
    }
}
