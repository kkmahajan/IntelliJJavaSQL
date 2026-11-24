package org.example.automation;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.regex.Pattern;

/** Adds real HTTP exchange evidence to each Allure test, shared with Extent. */
final class ApiExecutionFilter implements Filter {
    private static final Pattern SECRET = Pattern.compile(
            "(?i)(\\\"(?:password|token|secret|apiKey|authorization)\\\"\\s*:\\s*\\\")[^\\\"]*(\\\")");
    private static String redact(String body) {
        if (body == null) return "";
        String sanitized = SECRET.matcher(body).replaceAll("$1[REDACTED]$2");
        return sanitized.length() > 12000 ? sanitized.substring(0, 12000) + "\n[TRUNCATED]" : sanitized;
    }
    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification responseSpec, FilterContext context) {
        String label = request.getMethod() + " " + request.getURI().replaceAll("(?i)([?&](?:token|key|secret|password)=)[^&]+", "$1[REDACTED]");
        long start = System.nanoTime();
        return Allure.step("HTTP " + label, () -> {
            Object body = request.getBody();
            if (body != null) Allure.addAttachment("Request body", "application/json", redact(String.valueOf(body)), ".json");
            Response response = context.next(request, responseSpec);
            Allure.step("Received HTTP " + response.statusCode() + " in " + ((System.nanoTime() - start) / 1_000_000) + " ms");
            String responseBody = response.asString();
            if (!responseBody.isBlank()) Allure.addAttachment("Response body", "application/json", redact(responseBody), ".json");
            return response;
        });
    }
}
