package org.example;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.example.dto.AuthDto;
import org.junit.jupiter.api.BeforeAll;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.codeborne.selenide.logevents.SelenideLogger;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseTest {
    protected static RequestSpecification spec;
    protected static String authToken;
    protected static Dotenv dotenv = Dotenv.load();

    protected static final Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
    protected static Map<String, Object> testData;

    @BeforeAll
    public static void initSpec() {
        spec = new RequestSpecBuilder()
            .setBaseUri(dotenv.get("BASE_API_URL"))
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured()
                .setRequestAttachmentName("Request")
                .setResponseAttachmentName("Response"))
            .build();

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        try (InputStream inputStream = BaseTest.class.getResourceAsStream("/test-data.yml")) {
            testData = yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(((Map<String, String>)(((Map<String, Object>)testData.get("users")).get("admin"))).get("username"));
    }

    public void setupAuth(String role) {
        String body = (new AuthDto(dotenv.get(role + "_LOGIN"), dotenv.get(role + "_PASSWORD"))).toJsonString();
        authToken = RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/public/auth/login")
            .then().statusCode(200)
            .assertThat().body(matchesJsonSchemaInClasspath("schemas/auth.json"))
            .extract().jsonPath().getString("token");
    }
}
