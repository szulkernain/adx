package com.ambrygen.adx.services.security;

import com.ambrygen.adx.dto.security.*;
import com.ambrygen.adx.models.security.*;
import com.ambrygen.adx.repositories.security.*;
import com.ambrygen.adx.dto.security.*;
import com.ambrygen.adx.errors.ResourceAlreadyExistsException;
import com.ambrygen.adx.errors.ResourceNotFoundException;
import com.ambrygen.adx.models.ERole;
import com.ambrygen.adx.models.security.*;
import com.ambrygen.adx.repositories.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder encoder;
    private final PasswordResetTokenService passwordResetTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserSpecification userSpecification;
    private final RefreshTokenService refreshTokenService;

    @Value("${pagination.page.size.default: 25}")
    private Integer defaultPageSize;
    private final RefreshTokenRepository refreshTokenRepository;

    public User getUserByEmailAddress(String emailAddress) {
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with email: " + emailAddress));
        return user;
    }

    public User registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmailAddress(signUpRequest.emailAddress())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }
        // Create new user's account
        User user = new User(signUpRequest.emailAddress(), signUpRequest.firstName(), signUpRequest.lastName(),
                encoder.encode(signUpRequest.password()));
        user.setEnabled(false);

        Set<String> roleList = signUpRequest.roles();
        Set<Role> roles = getRoles(roleList);
        User createdUser = userRepository.save(user);

        //Create the association between User and each of the role for this user
        for (Role role : roles) {
            UserRole userRole = new UserRole();
            userRole.setUser(createdUser);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
        return createdUser;
    }


    /**
     * We currently allow only following user details:
     * <ol>
     *     <li>Roles</li>
     *     <li>Locked</li>
     * </ol>
     *
     * @param userDTO
     * @return
     */
    public UserDTO updateUser(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + userDTO.getId() + " not found");
        }
        User existingUser = optionalUser.get();
        //Delete all existing roles associated with this user
        Set<UserRole> userRoles = existingUser.getUserRoles();
        for (UserRole userRole : userRoles) {
            userRoleRepository.delete(userRole);
        }
        //Create the association between User and each of the role for this user
        Set<UserRole> userRolesToAssign = assignRolesToUser(existingUser, userDTO.getRoles());
        //Updated locked status for the user
        existingUser.setLocked(userDTO.isLocked());
        existingUser.setUserRoles(userRolesToAssign);
        User updatedUser = userRepository.save(existingUser);
        return userDTO;
    }

    /**
     * Self service function - user is updating his/her profile details
     * <ol>
     *     <li>First Name</li>
     *     <li>Last Name</li>
     * </ol>
     *
     * @param userDTO
     * @return
     */
    public UserDTO updateProfile(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + userDTO.getId() + " not found");
        }
        User existingUser = optionalUser.get();
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setFamilyName(userDTO.getFamilyName());
        User updatedUser = userRepository.save(existingUser);
        return userDTO;
    }


    public void updatePassword(PasswordRequest pwdr) {
        Optional<User> optionalUser = userRepository.findByEmailAddress(pwdr.emailAddress());
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + pwdr.emailAddress() + " not found");
        }
        User existingUser = optionalUser.get();
        existingUser.setPassword(encoder.encode(pwdr.password()));
        userRepository.save(existingUser);
    }

    private Set<UserRole> assignRolesToUser(User user, List<String> roles) {
        //Create new roles for this user
        // create an empty set
        Set<String> roleSet = new HashSet<>();

        // Add each element of list into the set
        for (String role : roles) {
            roleSet.add(role);
        }
        Set<Role> rolesToAssign = getRoles(roleSet);

        Set<UserRole> userRolesToAssign = new HashSet<>();

        for (Role role : rolesToAssign) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRolesToAssign.add(userRoleRepository.save(userRole));
        }
        return userRolesToAssign;
    }

    private Set<Role> getRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_SUBSCRIBER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            return roles;
        }

        for (String role : strRoles) {
            Role adminRole = roleRepository.findByName(ERole.valueOf(role))
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
        }
        return roles;
    }

    public void delete(UUID id) {
        Optional<User> userOptional = userRepository.findById(id.toString());

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException(String.format("No User found with id: %s", id));
        }
        User existingUser = userOptional.get();

        deleteAllTokens(existingUser);
        Set<UserRole> userRoles = existingUser.getUserRoles();
        for (UserRole userRole : userRoles) {
            userRoleRepository.delete(userRole);
        }
        userRepository.delete(existingUser);
    }

    private void deleteAllTokens(User existingUser) {

        //Delete all password reset tokens
        List<PasswordResetToken> passwordResetTokens =
                passwordResetTokenService.getPasswordResetTokensForAUser(existingUser);
        for (PasswordResetToken passwordResetToken : passwordResetTokens) {
            passwordResetTokenService.delete(passwordResetToken);
        }
        //Delete all verification tokens
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUserId(existingUser.getId());
        if (verificationToken.isPresent()) {
            verificationTokenRepository.delete(verificationToken.get());
        }

        //Delete all Refresh tokens
        refreshTokenService.deleteByUserId(existingUser.getId());
    }


    public UserListResponse getUserList(UserListRequest request) {
        List<User> list = null;
        Page<User> pages = null;
        Optional<Role> role = null;
        String roleId = null;
        String roleName = request.role();
        if (roleName != null && !roleName.isBlank()) {
            role = roleRepository.findByName(ERole.valueOf(roleName));
            if (role.isPresent()) {
                roleId = role.get().getId();
            }
        }
        if (request.pageNumber() == null) {
            pages = new PageImpl<>(userRepository.findAll(userSpecification.getUsers(request, roleId)));
        } else {
            Integer pageSize = 0;
            if (request.pageSize() == null) {
                pageSize = defaultPageSize;
            } else {
                pageSize = request.pageSize();
            }
            Pageable paging = PageRequest.of(request.pageNumber(), pageSize);
            pages = userRepository.findAll(userSpecification.getUsers(request, roleId), paging);
        }
        if (pages != null && pages.getContent() != null) {
            list = pages.getContent();
            if (list != null && list.size() > 0) {
                UserListResponse respList = new UserListResponse();
                respList.setTotalPages(pages.getTotalPages());
                respList.setTotalCount(pages.getTotalElements());
                respList.setPageNumber(pages.getNumber());
                List<UserDTO> userDTOList = getUserDTOList(list);
                respList.setUsers(userDTOList);
                return respList;
            }
        }
        return null;
    }

    private List<UserDTO> getUserDTOList(List<User> users) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = getUserDTO(user);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    private UserDTO getUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setFamilyName(user.getFamilyName());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setLocked(user.isLocked());
        userDTO.setExpired(user.isExpired());
        userDTO.setCredentialsExpired(user.isCredentialsExpired());
        Set<UserRole> userRoles = user.getUserRoles();
        List<String> roleNames = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            String roleName = userRole.getRole().getName().name();
            roleNames.add(roleName);
        }
        userDTO.setRoles(roleNames);
        return userDTO;
    }

    public void validatePassword(PasswordRequest vpr) {
        User user = userRepository.findByEmailAddress(vpr.emailAddress())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with email: " + vpr.emailAddress()));

    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }
}
