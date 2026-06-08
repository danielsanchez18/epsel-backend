package com.epsel.epsel_api.shared.utils;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelImportHelper {

    public static boolean hasExcelFormat(MultipartFile file) {
        String type = file.getContentType();
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(type) ||
               "application/vnd.ms-excel".equals(type) ||
               "text/csv".equals(type);
    }

    public static List<List<String>> readExcel(MultipartFile file) {
        try {
            String type = file.getContentType();
            if ("text/csv".equals(type)) {
                return readCsv(file);
            }

            List<List<String>> rows = new ArrayList<>();
            InputStream is = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(is);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            int rowNumber = 0;
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                List<String> rowData = new ArrayList<>();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    
                    while (cellIdx < currentCell.getColumnIndex()) {
                        rowData.add("");
                        cellIdx++;
                    }

                    switch (currentCell.getCellType()) {
                        case STRING:
                            rowData.add(currentCell.getStringCellValue().trim());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                rowData.add(currentCell.getLocalDateTimeCellValue().toString());
                            } else {
                                long val = (long) currentCell.getNumericCellValue();
                                rowData.add(String.valueOf(val));
                            }
                            break;
                        case BOOLEAN:
                            rowData.add(String.valueOf(currentCell.getBooleanCellValue()));
                            break;
                        default:
                            rowData.add("");
                            break;
                    }
                    cellIdx++;
                }

                if (!rowData.isEmpty() && rowData.stream().anyMatch(s -> !s.isEmpty())) {
                    rows.add(rowData);
                }
                rowNumber++;
            }

            workbook.close();
            return rows;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }

    private static List<List<String>> readCsv(MultipartFile file) {
        try {
            List<List<String>> rows = new ArrayList<>();
            java.io.BufferedReader fileReader = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream(), "UTF-8"));
            com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(fileReader);

            String[] nextRecord;
            int rowNumber = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                List<String> rowData = new ArrayList<>();
                for (String cell : nextRecord) {
                    rowData.add(cell != null ? cell.trim() : "");
                }
                if (!rowData.isEmpty() && rowData.stream().anyMatch(s -> !s.isEmpty())) {
                    rows.add(rowData);
                }
                rowNumber++;
            }
            csvReader.close();
            return rows;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }
}
