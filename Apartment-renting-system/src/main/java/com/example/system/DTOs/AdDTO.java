package com.example.system.DTOs;

import com.example.system.Utils.CustomDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdDTO {
    private Long id;
    @NotEmpty(message = "Title can't be empty")
    private String title;
    @Size(max = 300, message = "Max allowed 300 characters")
    private String description;
    private List<String> photoURLs;
    @NotNull(message = "Price can't be null")
    private Integer pricePerMonth;
    @NotNull
    private Integer totalRoomAmount;
    @NotNull
    private int bathRoomAmount;
    @NotNull
    private int bedRoomAmount;
    @NotNull
    private int kitchenRoomAmount;
    @NotNull
    private Integer area;
    @NotNull
    private Integer whichFloor;
    @Size(max = 100, message = "Max allowed 100 characters")
    @Builder.Default
    private String furniture = "None";
    @NotEmpty
    private String location;
    @JsonSerialize(using = CustomDateFormatter.class)
    private Date dateOfPosting;
    @Builder.Default
    private Boolean available = true;
    @Builder.Default
    private Boolean enabled = true;
    @NotEmpty
    private String houseType;
    private Long renterId;
    private Integer viewers;
    private Integer savedAmount;
    private String promotionType;

}
