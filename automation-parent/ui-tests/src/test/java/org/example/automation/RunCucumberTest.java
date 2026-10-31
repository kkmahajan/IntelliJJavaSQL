package org.example.automation;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "org.example.automation.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, json:target/cucumber-report.json, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
public class RunCucumberTest {}
