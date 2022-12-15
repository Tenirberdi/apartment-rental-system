package com.example.system.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private Long id;
    @NotEmpty
    @Pattern(regexp = "(?=.*[A-Z])[\\p{Punct}A-Z0-9 ]{1,32}", message = "Name must be all in uppercase and contain at least one letter")
    private String name;
    @NotNull
    private Integer ordered;
    @NotNull
    private Integer price;
}
