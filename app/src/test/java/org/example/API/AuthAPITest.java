package org.example.API;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.example.BaseTest;
import org.example.dto.AuthDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;

public class AuthAPITest extends BaseTest {
    @Story("Успешный вход (все роли)")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Вход в аккаунты всех ролей")
    @ParameterizedTest
    @CsvSource(value = {
        "Redactor, qazedcrfvs1A",
        "ContentManager, qazedcrfvs1A",
        "LicenceManager, qazedcrfvs1A",
        "Analyst, qazedcrfvs1A",
        "admin, qazedcrfvs1A",
    })
    public void testLogin(String username, String password) {
        String body = (new AuthDto(username, password)).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/public/auth/login")
            .then().statusCode(200)
            .assertThat().body(matchesJsonSchemaInClasspath("schemas/auth.json"));
    }
}
