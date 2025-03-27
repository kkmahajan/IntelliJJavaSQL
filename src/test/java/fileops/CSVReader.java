package fileops;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVReader {

    public static List<HashMap<String, String>> readCSV(String filePath) {
        List<HashMap<String, String>> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;

            // Read the header row
            if ((line = br.readLine()) != null) {
                headers = line.split(",", -1); // Split by comma, keeping empty values
            }

            if (headers == null) return dataList; // Return empty list if no headers found

            // Read each data row
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1); // Split row into values
                HashMap<String, String> rowData = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    rowData.put(headers[i].trim(), i < values.length ? values[i].trim() : "");
                }

                dataList.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static void main(String[] args) {
        String filePath = "src/test/resources/data.csv"; // Provide the correct path
        List<HashMap<String, String>> csvData = readCSV(filePath);

        // Print the extracted data
        for (HashMap<String, String> row : csvData) {
            System.out.println(row);
        }
    }
}