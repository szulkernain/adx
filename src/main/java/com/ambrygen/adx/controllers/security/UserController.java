package com.ambrygen.adx.controllers.security;

import com.ambrygen.adx.dto.MessageResponse;
import com.ambrygen.adx.dto.security.PasswordRequest;
import com.ambrygen.adx.dto.security.UserDTO;
import com.ambrygen.adx.dto.security.UserListRequest;
import com.ambrygen.adx.dto.security.UserListResponse;
import com.ambrygen.adx.services.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(@PathVariable("id") UUID id) {
        userService.delete(id);
        MessageResponse messageResponse = new MessageResponse("User is deleted.");
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping(value = "/list", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getUserList(@RequestBody UserListRequest userListRequest) {
        UserListResponse userListResponse = userService.getUserList(userListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userListResponse);
    }


    @PatchMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PatchMapping(value = "/{id}/profile", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('SUBSCRIBER') or hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateProfile(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PostMapping(value = "/{id}/password/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('SUBSCRIBER') or hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<MessageResponse> validatePassword(@RequestBody PasswordRequest vpr) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(vpr.emailAddress(), vpr.password()));
        if (authentication != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Valid password"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid Password"));
    }

    @PostMapping(value = "/{id}/password/change", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('SUBSCRIBER') or hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody PasswordRequest pwdr) {
        userService.updatePassword(pwdr);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Password changed successfully."));
    }
}
