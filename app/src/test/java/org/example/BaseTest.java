package org.example;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.codeborne.selenide.logevents.SelenideLogger;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.datafaker.Faker;

public class BaseTest {
    protected static RequestSpecification spec;
    protected static String authToken;
    protected static Dotenv dotenv = Dotenv.load();

    protected static final Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
    protected static Map<String, Object> testData;
    protected static Faker faker = new Faker();

    @BeforeAll
    public static void initSpec() {
        spec = new RequestSpecBuilder()
            .setBaseUri(dotenv.get("BASE_API_URL"))
            .setContentType(ContentType.JSON)
            .addHeader("Accept-Language", "ru")
            .addFilter(new AllureRestAssured()
                .setRequestAttachmentName("Request")
                .setResponseAttachmentName("Response"))
            .build();

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }
}
