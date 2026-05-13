package com.epsel.epsel_api.shared.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedResponse<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    private Boolean last;

}
