package org.example.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Aggregates Surefire TEST-*.xml files into one Extent Spark HTML report.
 * Reads the final results of every module; no test framework listener is needed.
 */
public final class ExtentReportGenerator {
    private ExtentReportGenerator() {}

    public static void main(String[] args) throws Exception {
        Path reactor = Path.of(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
        Path output = Path.of(args.length > 1 ? args[1] : "reports/latest/extent/index.html")
                .toAbsolutePath().normalize();
        Files.createDirectories(output.getParent());

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(new ExtentSparkReporter(output.toString()));
        reports.setSystemInfo("Project", "Java Multi-Module Automation Framework");
        reports.setSystemInfo("Source", "Maven Surefire JUnit XML");
        int executed = 0;
        int failed = 0;
        for (String module : List.of("sample-app", "api-tests", "ui-tests")) {
            Path dir = reactor.resolve(module).resolve("target/surefire-reports");
            if (!Files.isDirectory(dir)) continue;
            List<Path> files;
            try (Stream<Path> paths = Files.list(dir)) {
                files = paths.filter(p -> p.getFileName().toString().startsWith("TEST-")
                                && p.getFileName().toString().endsWith(".xml"))
                        .sorted().toList();
            }
            for (Path xml : files) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                factory.setXIncludeAware(false);
                factory.setExpandEntityReferences(false);
                Document document = factory.newDocumentBuilder().parse(xml.toFile());
                NodeList tests = document.getElementsByTagName("testcase");
                for (int i = 0; i < tests.getLength(); i++) {
                    Element testcase = (Element) tests.item(i);
                    String name = testcase.getAttribute("name");
                    String classname = testcase.getAttribute("classname");
                    ExtentTest test = reports.createTest(module + " / " + classname + " / " + name)
                            .assignCategory(module);
                    Element failure = child(testcase, "failure");
                    Element error = child(testcase, "error");
                    if (failure != null || error != null) {
                        Element problem = failure != null ? failure : error;
                        test.fail(problem.getAttribute("message") + "\n" + problem.getTextContent());
                        failed++;
                        executed++;
                    } else if (child(testcase, "skipped") != null) {
                        test.skip("Skipped by JUnit assumption or test configuration");
                    } else {
                        test.pass("Passed (" + testcase.getAttribute("time") + " seconds)");
                        executed++;
                    }
                }
            }
        }
        reports.flush();
        System.out.println("Extent report: " + output + " | executed=" + executed + " failed=" + failed);
        if (executed == 0) throw new IllegalStateException("No executed Surefire test cases found");
    }

    private static Element child(Element parent, String tag) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element element && element.getTagName().equals(tag)) return element;
        }
        return null;
    }
}
