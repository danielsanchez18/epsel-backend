package com.epsel.epsel_api.shared.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportPreviewResponse<T> {
    private Integer totalRows;
    private Integer validCount;
    private Integer invalidCount;
    private List<T> validData;
    private List<ImportErrorDTO> errors;
}
