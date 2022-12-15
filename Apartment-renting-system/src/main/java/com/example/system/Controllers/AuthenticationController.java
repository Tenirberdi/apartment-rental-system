package com.example.system.Controllers;

import com.example.system.DTOs.AuthenticationRequestDTO;
import com.example.system.DTOs.SignUpDTO;
import com.example.system.Entities.User;
import com.example.system.Exceptions.CustomNotFoundException;
import com.example.system.Security.Jwt.JwtTokenProvider;
import com.example.system.Services.AuthenticationService;
import com.example.system.Services.UserService;
import com.example.system.Utils.MailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.example.system.EndPoints.URLs.*;

@RestController
@RequestMapping(path = AUTHENTICATION_BASE_URL, produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {

    private final MailUtil mailUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;

    private final Environment env;
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, PasswordEncoder passwordEncoder, MailUtil mailUtil, ObjectMapper objectMapper, AuthenticationService authenticationService, Environment env) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.mailUtil = mailUtil;
        this.objectMapper = objectMapper;
        this.authenticationService = authenticationService;
        this.env = env;
    }


    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO requestDto) {
        try {
            String username = requestDto.getUsername();
            if (userService.findByUsername(username) != null) {
                if (!userService.findByUsername(username).isEnabled()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account was disabled by admin");
                }
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username);

            Map<Object, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }
    
    @PostMapping(REGISTER)
    public ResponseEntity<?> registerUser(@NotEmpty @RequestParam("data") String data, MultipartHttpServletRequest request) throws JsonProcessingException {
        SignUpDTO signUpDto = objectMapper.readValue(data, SignUpDTO.class);
        Map< String, MultipartFile> photos = request.getFileMap();
        authenticationService.registerUser(signUpDto, photos);

        return ResponseEntity.created(URI.create("/user/profile")).build();

    }

    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<?> processForgotPassword(@RequestParam("email") String email) throws CustomNotFoundException, MessagingException {
        try {
            String token = RandomString.make(6);
            authenticationService.updateResetPasswordToken(token, email);
            mailUtil.sendEmail(email, token);
            return ResponseEntity.ok().body("We have sent a reset password code to your email. Please check.");
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new MessagingException();
        }
    }

    @GetMapping(RESET_PASSWORD)
    public ResponseEntity<?> showResetPasswordForm( @RequestParam("token") String token) {
        User user = authenticationService.getByResetPasswordToken(token);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
        Map<Object, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<?> processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {

        User user = authenticationService.getByResetPasswordToken(token);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }else {
            authenticationService.updatePassword(user, password);
            return ResponseEntity.noContent().build();
        }
    }


}
