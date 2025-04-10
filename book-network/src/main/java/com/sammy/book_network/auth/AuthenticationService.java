package com.sammy.book_network.auth;

import com.sammy.book_network.email.EmailService;
import com.sammy.book_network.email.EmailTemplate;
import com.sammy.book_network.role.RoleRepository;
import com.sammy.book_network.security.JwtService;
import com.sammy.book_network.user.Token;
import com.sammy.book_network.user.TokenRepository;
import com.sammy.book_network.user.User;
import com.sammy.book_network.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}") // check the imports from the yaml file
    private String activationUrl;


    public void register(RegistrationRequest request) throws MessagingException {

        // assign a role to the user
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("Role User was not Initialized/Found"));

        // create a user Object and save it
        var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).accountLocked(false).enabled(false).roles(Arrays.asList(userRole)).build();

        userRepository.save(user);
        // send a validation email to the user (implement and email sender service and also an email template to send to the user)
        sendValidationEmail(user);

    }
    // after the register above send the user a validation email to activate their account
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        // send Email
        emailService.sendEmail(user.getEmail(), // get the actual email being used
                user.fullName(), // getFullName for the user
                EmailTemplate.ACTIVATE_ACCOUNT, // template being used
                activationUrl,// activation url
                newToken,// authentication token for the user
                "Account activation");
    }

    // generate token and save to the database
    private String generateAndSaveActivationToken(User user) {
        // generate Token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder().token(generatedToken).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(30)).user(user).build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789"; // the range of code generation from 0-9
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();// initialize secure random(generate random secure codes)
        // generate a six(length) digit code
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    // activate account
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid Token"));
        // todo: exception has to be defined
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent ");
        }
        // check if user exist before enabling the account.
        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // if user is found
        user.setEnabled(true);
        userRepository.save(user);
        // set the time the token was validated
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
