package com.example.system.dtos;

import com.example.system.utilities.CustomDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedListDTO {
    private Long id;
    private String title;
    private String location;
    private int price;
    private boolean available;
    @JsonSerialize(using = CustomDateFormatter.class)
    private Date dateOfPosting;
}
