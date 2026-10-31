package org.example.automation;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.nio.file.Files;
import java.nio.file.Path;

final class UiFailureCapture implements AfterTestExecutionCallback {
    @Override public void afterTestExecution(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isEmpty()) return;
        Object instance = context.getRequiredTestInstance();
        if (!(instance instanceof UiTestSupport test) || test.driver == null) return;
        Path folder = Path.of("target", "failure-artifacts");
        Files.createDirectories(folder);
        byte[] png = ((TakesScreenshot) test.driver).getScreenshotAs(OutputType.BYTES);
        Files.write(folder.resolve(context.getRequiredTestMethod().getName() + ".png"), png);
        io.qameta.allure.Allure.addAttachment("Selenium failure screenshot", "image/png",
                new java.io.ByteArrayInputStream(png), ".png");
    }
}
