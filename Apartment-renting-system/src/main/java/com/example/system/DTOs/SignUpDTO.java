package com.example.system.DTOs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpDTO {
    private Long id;
    @NotEmpty
    private String fullName;
    @NotEmpty
    @Size(max = 70)
    private String email;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String contactInfo;
}
