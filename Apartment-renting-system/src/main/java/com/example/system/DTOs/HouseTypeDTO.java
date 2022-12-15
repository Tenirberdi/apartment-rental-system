package com.example.system.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseTypeDTO {
    private long id;
    @NotEmpty(message = "Type must not be empty")
    private String type;
}
