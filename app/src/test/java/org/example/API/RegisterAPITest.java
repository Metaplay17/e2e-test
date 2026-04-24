package org.example.API;

import org.example.BaseTest;
import org.example.dto.RegisterRequest;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Authority")
@Feature("Регистрация")
public class RegisterAPITest extends BaseTest {

    @Story("Успешная регистрация")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Базовая проверка успешной регистрации")
    @Test
    public void testRegister_success() {
        String body = (new RegisterRequest(faker.letterify("??????"), faker.name().fullName())).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/register-response.json"));
    }

    @Story("null или Empty поля при регистрации")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверка ответа сервера при отправке null полей")
    @Test
    public void testRegister_nullLoginField_badRequestExpected() {
        String body = (new RegisterRequest(faker.letterify("??????"), null)).toJsonString();


        // ЗАДОКУМЕНТИРОВАННЫЙ БАГ
        // RestAssured.given()
        //     .spec(spec)
        //     .body(body)
        //     .post("/register")
        //     .then().statusCode(400)
        //     .contentType(ContentType.JSON)
        //     .body("detail", equalTo("display_name: не должно быть пустым"));

        
        body = (new RegisterRequest(null, faker.name().fullName())).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(400)
            .contentType(ContentType.JSON)
            .body("detail", equalTo("login: не должно быть пустым"));


        body = (new RegisterRequest("", faker.name().fullName())).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(400)
            .contentType(ContentType.JSON)
            .body("detail", equalTo("login: не должно быть пустым"));

        
        body = (new RegisterRequest(faker.letterify("??????"), "")).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(400)
            .contentType(ContentType.JSON)
            .body("detail", equalTo("displayName: не должно быть пустым"));
    }

    @Story("Повторное использование логина")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка корректности реакции сервера на повториное использование данных при регистрации")
    @Test
    public void testRegister_conflict() {
        String login = faker.letterify("??????");
        String showedName = faker.name().fullName();
        String body = (new RegisterRequest(login, showedName)).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/register-response.json"));

        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(409)
            .contentType(ContentType.JSON)
            .body("detail", equalTo("Слот уже занят"));

        body = (new RegisterRequest(faker.letterify("??????"), showedName)).toJsonString();
        RestAssured.given()
            .spec(spec)
            .body(body)
            .post("/register")
            .then().statusCode(201)
            .contentType(ContentType.JSON)
            .body(matchesJsonSchemaInClasspath("schemas/register-response.json"));
    }

}
