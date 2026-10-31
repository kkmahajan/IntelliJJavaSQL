package org.example.automation.reporting;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.databind.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/** Generates Extent Spark from the SAME Allure results as the Allure HTML report.
 * Preserves nested steps and attachments for JUnit, Selenium and Cucumber.
 */
public final class ExtentReportGenerator {
    private static final ObjectMapper JSON = new ObjectMapper();
    private ExtentReportGenerator() {}

    public static void main(String[] args) throws Exception {
        Path reactor = Path.of(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
        Path output = Path.of(args.length > 1 ? args[1] : "reports/latest/extent/index.html")
                .toAbsolutePath().normalize();
        Path results = reactor.resolve("target/combined-allure-results");
        if (!Files.isDirectory(results)) throw new IllegalStateException("Missing combined Allure results: " + results);
        Files.createDirectories(output.getParent());
        Path assets = output.getParent().resolve("attachments");
        Files.createDirectories(assets);

        ExtentReports report = new ExtentReports();
        report.attachReporter(new ExtentSparkReporter(output.toString()));
        report.setSystemInfo("Project", "Service Desk Automation Framework");
        report.setSystemInfo("Source", "Allure structured result JSON (shared with Allure HTML)");
        int count = 0, failed = 0;
        List<Path> resultFiles;
        try (Stream<Path> stream = Files.list(results)) {
            resultFiles = stream.filter(p -> p.getFileName().toString().endsWith("-result.json")).sorted().toList();
        }
        for (Path file : resultFiles) {
            JsonNode result = JSON.readTree(file.toFile());
            String status = result.path("status").asText("unknown");
            if (status.equals("skipped")) continue;
            String name = result.path("fullName").asText(result.path("name").asText("Unnamed test"));
            ExtentTest test = report.createTest(name);
            for (JsonNode label : result.path("labels")) {
                if (label.path("name").asText().equals("suite") || label.path("name").asText().equals("feature"))
                    test.assignCategory(label.path("value").asText());
            }
            for (JsonNode step : result.path("steps")) addStep(test, step, results, assets);
            for (JsonNode attachment : result.path("attachments")) attach(test, attachment, results, assets);
            switch (status) {
                case "passed" -> test.pass("Passed");
                case "failed", "broken" -> {
                    failed++;
                    test.fail(result.path("statusDetails").path("message").asText(status));
                    String trace = result.path("statusDetails").path("trace").asText("");
                    if (!trace.isBlank()) test.info("<pre>" + escape(trace) + "</pre>");
                }
                default -> test.warning("Status: " + status);
            }
            count++;
        }
        report.flush();
        System.out.println("Extent report: " + output + " | executed=" + count + " failed=" + failed);
        if (count == 0) throw new IllegalStateException("No executed Allure result JSON files found");
    }

    private static void addStep(ExtentTest parent, JsonNode step, Path results, Path assets) {
        String name = step.path("name").asText("Unnamed step");
        ExtentTest node = parent.createNode(name);
        for (JsonNode child : step.path("steps")) addStep(node, child, results, assets);
        for (JsonNode attachment : step.path("attachments")) attach(node, attachment, results, assets);
        String status = step.path("status").asText("unknown");
        if (status.equals("passed")) node.pass("Completed");
        else if (status.equals("failed") || status.equals("broken"))
            node.fail(step.path("statusDetails").path("message").asText(status));
        else if (status.equals("skipped")) node.skip("Skipped");
        else node.info("Status: " + status);
    }

    private static void attach(ExtentTest test, JsonNode attachment, Path results, Path assets) {
        String source = attachment.path("source").asText("");
        if (source.isBlank() || !source.equals(Path.of(source).getFileName().toString())) return;
        Path from = results.resolve(source);
        if (!Files.isRegularFile(from)) return;
        String label = attachment.path("name").asText("Attachment");
        String type = attachment.path("type").asText("");
        try {
            Files.copy(from, assets.resolve(source), StandardCopyOption.REPLACE_EXISTING);
            String relative = "attachments/" + source;
            if (type.startsWith("image/")) test.addScreenCaptureFromPath(relative, label);
            else if (type.startsWith("text/") || type.contains("json")) {
                String content = Files.readString(from);
                if (content.length() > 12000) content = content.substring(0, 12000) + "... [truncated]";
                test.info("<details><summary>" + escape(label) + "</summary><pre>" + escape(content) + "</pre></details>");
            } else test.info(escape(label) + " (attachment: " + escape(relative) + ")");
        } catch (Exception ex) {
            test.warning("Could not copy attachment " + escape(label) + ": " + escape(ex.getMessage()));
        }
    }
    private static String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
