package com.example.system.Services;

import com.example.system.DTOs.SignUpDTO;
import com.example.system.Entities.User;
import com.example.system.Exceptions.CustomNotFoundException;
import com.example.system.Repositories.RoleRepo;
import com.example.system.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Validated
public class AuthenticationService {
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final FilesStorageServiceImpl filesStorageService;

    @Autowired
    public AuthenticationService(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo, FilesStorageServiceImpl filesStorageService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.filesStorageService = filesStorageService;
    }

    public void updateResetPasswordToken(String token, String email) throws CustomNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            user.setTokenExpirationDate(new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));
            userRepo.save(user);
        } else {
            throw new CustomNotFoundException("Could not find any user with the email " + email);
        }
    }

    public User getByResetPasswordToken(String token) {
        return userRepo.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPass(encodedPassword);

        user.setResetPasswordToken(null);
        userRepo.save(user);
    }

    public void registerUser(@Valid SignUpDTO signUpDto, Map< String, MultipartFile> files){
        final String[] fileName = new String[1];
        if(userRepo.existsByUsername(signUpDto.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken!");
        }
        if(userRepo.findByEmail(signUpDto.getEmail()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such email already exists");
        }

        if(files.size() > 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to upload only one photo for profile");
        }

        files.values().forEach(file -> {
            fileName[0] = filesStorageService.save(file);
        });

        User user = new User();
        user.setFullName(signUpDto.getFullName());
        user.setUsername(signUpDto.getUsername());
        user.setPass(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(roleRepo.findByRole("ROLE_USER"));
        user.setContactInfo(signUpDto.getContactInfo());
        user.setEnabled(true);
        user.setPhotoName(fileName[0]);
        user.setEmail(signUpDto.getEmail());
        user.setRegistrationDate(new java.sql.Date(System.currentTimeMillis()));
        userRepo.save(user);
    }

    @Scheduled(cron = "0 */5 * ? * *")
    @Transactional
    public void expireResetPasswordToken(){
        System.out.println("expiring reset password tokens");
        userRepo.expireTokens();
    }
}
