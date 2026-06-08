package com.epsel.epsel_api.modules.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OCRApiResponse {

    private Integer reading;
    private Double confidence;
    private List<String> texts;

}