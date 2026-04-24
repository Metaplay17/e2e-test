package org.example.UI;

import org.example.BaseTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.SelenideElement;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;

import com.codeborne.selenide.Selectors;

import static com.codeborne.selenide.Selenide.*;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.*;

@Epic("Функционал редактора")
@Feature("Вход выход + формирование плейлиста")
public class LoginTest extends BaseTest {

    private static Logger logger = LoggerFactory.getLogger(LoginTest.class);
    private static Dotenv dotenv = Dotenv.load();
    private static String BASE_URL = dotenv.get("BASE_URL");
    
    @Step("Авторизация: username={0}, password={1}")
    private void login(String username, String password) {
        open(BASE_URL + "/login");

        SelenideElement usernameInput = $("#username");
        usernameInput.setValue("Redactor");

        SelenideElement passwordInput = $("#password");
        passwordInput.setValue("qazedcrfvs1A");

        SelenideElement loginButton = $("button");
        loginButton.click();
    }
    
    @Test
    @Story("Успешный вход (редактор)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Вход в аккаунт редактора")
    public void testLogin() {
        login(dotenv.get("REDACTOR_LOGIN"), dotenv.get("REDACTOR_PASSWORD"));

        SelenideElement pageTitle = $("h1");
        pageTitle.shouldHave(text("Страница музыкального редактора"));
    }

    @Test
    @Story("Успешное формирование плейлиста")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Заполнение формы и отправка запроса на формирование плейлиста")
    public void testFormPlaylist() {
        localStorage().setItem("token", authToken);
        LocalDate date = LocalDate.now();

        $("#duration").setValue("300");
        $("#timeOfDay").selectOptionByValue("morning");
        SelenideElement dateInput = $("#date");
        executeJavaScript(
            "const dateInput = document.getElementById('date');" +
            "const valueSetter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;" +
            "valueSetter.call(dateInput, '" + date.toString() + "');" +
            "const event = new Event('input', { bubbles: true });" +
            "dateInput.dispatchEvent(event);"
        );

        dateInput.shouldHave(value(date.toString()));
        $(Selectors.byText("Сформировать")).click();

        $(Selectors.byText("Track One"))
            .should(visible);

        $(Selectors.byText("Исполнитель 1"))
            .should(visible);
        
        $(Selectors.byText("3:03"))
            .should(visible);

        $(Selectors.byText("120"))
            .should(visible);
    }
}