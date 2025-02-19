package com.ambrygen.adx.controllers.security;

import com.ambrygen.adx.dto.ForgotPasswordRequestDTO;
import com.ambrygen.adx.dto.ADXResponseDTO;
import com.ambrygen.adx.dto.security.*;
import com.ambrygen.adx.services.security.*;
import com.ambrygen.adx.errors.ResourceNotFoundException;
import com.ambrygen.adx.errors.TokenRefreshException;
import com.ambrygen.adx.errors.UserAccountNotVerifiedException;
import com.ambrygen.adx.events.EventPublisher;
import com.ambrygen.adx.models.security.RefreshToken;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.VerificationToken;
import com.ambrygen.adx.repositories.security.UserRepository;
import com.ambrygen.adx.services.UserVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EventPublisher eventPublisher;
    private final UserVehicleService userVehicleService;

    private static String MSG_CHECK_EMAIL_VERIFICATION_CODE =
            " Check your email, copy and paste the verification code on this screen to activate your account" +
                    " to start using ADX!";
    private final UserRepository userRepository;

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtTokenFromUserName(user.getEmailAddress());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(String.format("Refresh token %s is not in database!", requestRefreshToken)));
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDTO> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.emailAddress(), loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        boolean isCredentialsExpired = userDetails.getUser().isCredentialsExpired();
        boolean isEnabled = userDetails.getUser().isEnabled();
        boolean isExpired = userDetails.getUser().isExpired();
        boolean isLocked = userDetails.getUser().isLocked();

        if (!isEnabled) {
            throw new UserAccountNotVerifiedException("Account is not yet activated." + MSG_CHECK_EMAIL_VERIFICATION_CODE);
        }

        if (isCredentialsExpired || isExpired || isLocked) {
            //
        }

        String jwt = jwtUtils.generateJwtToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        JwtResponse jwtResponse = new JwtResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getUser().getFirstName(),
                userDetails.getUser().getFamilyName(),
                roles);
        return ResponseEntity.ok(new SigninResponseDTO(false, "", jwtResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        User registeredUser = userService.registerUser(signUpRequest);

        String verificationToken = verificationTokenService.sendVerificationEmail(registeredUser);
        String message = String.format("You are now signed up! Account verification email is sent to %s. " +
                MSG_CHECK_EMAIL_VERIFICATION_CODE, signUpRequest.emailAddress());

        return ResponseEntity.ok(new SignupResponseDTO(false, "", message));
    }

    @PostMapping("/verificationToken")
    public ResponseEntity<ADXResponseDTO> sendVerificationToken(@RequestBody Map<String, String> sendVerificationCodeRequest) {
        String email = sendVerificationCodeRequest.get("emailAddress");
        //Delete existing verification code for this user.
        User user = userService.getUserByEmailAddress(email);
        verificationTokenService.deleteAllByUser(user);
        //Create new verification token for this user.
        String token = verificationTokenService.createVerificationToken(user);
        verificationTokenService.sendVerificationCodeEmail(email, token);
        String message = String.format("Account verification email is sent to %s. " +
                MSG_CHECK_EMAIL_VERIFICATION_CODE, email);
        return ResponseEntity.ok(new ADXResponseDTO(false, "", message));
    }

    @GetMapping("verifyAccount/{token}")
    public ResponseEntity<ActivateAccountResponseDTO> verifyAccount(@PathVariable("token") String token)
            throws ResourceNotFoundException, TokenRefreshException {
        Map response = new HashMap<String, String>();
        Optional<VerificationToken> verificationToken = verificationTokenService.findByToken(token);
        if (!verificationToken.isPresent()) {
            throw new ResourceNotFoundException(String.format("Verification token %s not found", token));
        }
        User user = verificationTokenService.verfiyTokenAndEnableUser(verificationToken.get());

        eventPublisher.publishUserAccountVerifiedEvent(user);

        return ResponseEntity.ok(new ActivateAccountResponseDTO(false, "", "Your account is now active!"));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<JwtResponse> validateJwt(@RequestBody ValidateTokenRequest vtr) {
        String jwt = vtr.token();
        if (jwt == null || jwt.isBlank() || jwt.isEmpty()) {
            return null;
        }

        if (jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(jwt,
                    "no-refresh-token",
                    userDetails.getUser().getId(),
                    userDetails.getUser().getEmailAddress(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    roles));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


    @PostMapping(value = "/password/forgot")
    public ResponseEntity<ResetPasswordResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        String recipientEmailAddress = forgotPasswordRequestDTO.getEmailAddress();
        User user = userService.getUserByEmailAddress(recipientEmailAddress);
        String resetPasswordToken = passwordResetTokenService.createPasswordResetToken(user);

        passwordResetTokenService.sendForgotPasswordRestTokenEmail(recipientEmailAddress, resetPasswordToken);

        String message = String.format("A verification code is sent to your email %s. " +
                "Copy and paste the verification code on this screen along with your new password.", recipientEmailAddress);

        ResetPasswordResponseDTO passwordResetRequestResponseDTO = new ResetPasswordResponseDTO(false, "", message);

        return ResponseEntity.status(HttpStatus.OK).body(passwordResetRequestResponseDTO);
    }


    @PostMapping(value = "/password/reset")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        passwordResetTokenService.resetPassword(passwordResetRequestDTO);

        String message = "Password is successfully changed. Log back in with your new password";
        ResetPasswordResponseDTO passwordResetRequestResponseDTO = new ResetPasswordResponseDTO(false, "", message);
        return ResponseEntity.status(HttpStatus.OK).body(passwordResetRequestResponseDTO);
    }


    @DeleteMapping(value = "/users/{userId}")
    public ResponseEntity<ADXResponseDTO> deleteAccount(
            @PathVariable("userId") String userId
            , @RequestBody ValidateTokenRequest tokenRequest) {
        String jwt = tokenRequest.token();
        if (jwt == null || jwt.isBlank() || jwt.isEmpty()) {
            ADXResponseDTO responseDTO = new ADXResponseDTO(true, "Invalid token", "Invalid token");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        Optional<User> user = userService.findById(userId);
        if(user.isEmpty() ) {
            ADXResponseDTO responseDTO = new ADXResponseDTO(true, "User not found!", "User not found!");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        String emailAddressOfUserToDelete = user.get().getEmailAddress();
        String emailAddressFoundByJwT = "";
        if (jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
            emailAddressFoundByJwT = userDetails.getEmail();
        }
        if(!emailAddressOfUserToDelete.equalsIgnoreCase(emailAddressFoundByJwT)) {
            ADXResponseDTO responseDTO = new ADXResponseDTO(false, "", "Unauthorized request to delete the user");
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        userVehicleService.deleteAllUserData(user.get());
        ADXResponseDTO responseDTO = new ADXResponseDTO(false, "", "User and associated datae is deleted");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
