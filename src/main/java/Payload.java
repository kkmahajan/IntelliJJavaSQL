import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.example.TestData.COURSE_JSON_FILE_PATH;

public class Payload {

    public static String coursePrice() {

        String courseJson=null;
        try{
        courseJson = new String(Files.readAllBytes(Paths.get(COURSE_JSON_FILE_PATH)));
        }catch (IOException ioException)
        {
            ioException.getCause();
            ioException.printStackTrace();
        }
        return courseJson;
    }
}
