package com.example.system.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMetaDataDTO {
    private int totalElementCount;
    private int pageSize;
    private int firstPage;
    private int currentPage;
    private int lastPage;
}
