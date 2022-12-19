package com.example.system.utilities;

import com.example.system.dtos.AdDTO;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Component
public class PaginationUtility {

    public PagedListHolder<?> getPages(List<?> data, int page, int size){
        if(size < 1 || size > 10000 || page < 0 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for page size or number");
        }
        PagedListHolder<?> pages = new PagedListHolder<>(data);
        pages.setPage(page); //set current page number
        pages.setPageSize(size); // set the size of page

        return pages;
    }
}
