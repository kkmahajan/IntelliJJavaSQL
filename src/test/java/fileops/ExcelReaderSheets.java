package fileops;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReaderSheets {

    public static Map<String, List<HashMap<String, String>>> readAllSheets(String filePath) {
        Map<String, List<HashMap<String, String>>> workbookData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Iterate through all sheets
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName(); // Get sheet name
                List<HashMap<String, String>> sheetData = new ArrayList<>();

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

                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData.put(headers.get(j), getCellValueAsString(cell));
                    }
                    sheetData.add(rowData);
                }

                // Store sheet data in the map
                workbookData.put(sheetName, sheetData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbookData;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    public static void main(String[] args) {
        String filePath = "src/test/resources/data.xlsx"; // Provide the correct path
        Map<String, List<HashMap<String, String>>> excelData = readAllSheets(filePath);

        // Print the extracted data
        for (String sheetName : excelData.keySet()) {
            System.out.println("Sheet: " + sheetName);
            for (HashMap<String, String> row : excelData.get(sheetName)) {
                System.out.println(row);
            }
        }
    }
}