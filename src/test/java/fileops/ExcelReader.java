package fileops;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {

    public static List<List<String>> readExcel(String filePath) {
        List<List<String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Read the first sheet
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(getCellValueAsString(cell));
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<HashMap<String, String>> readExcelAsMap(String filePath) {
        List<HashMap<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Read first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Read header row
            List<String> headers = new ArrayList<>();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue().trim()); // Store column names
                }
            }

            // Read data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                HashMap<String, String> rowData = new HashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(i), getCellValueAsString(cell));
                }
                dataList.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "blank_cell";
            default -> "";
        };
    }

    public static void main(String[] args) {
        String filePath = "src/test/resources/data.xlsx";
        List<List<String>> excelData = readExcel(filePath);
        for (List<String> row : excelData) {
            System.out.println(row);
        }

        List<HashMap<String, String>> excelDataInListOfMap = readExcelAsMap(filePath);
        for (HashMap<String, String> row : excelDataInListOfMap) {
            System.out.println(row);
        }
    }
}