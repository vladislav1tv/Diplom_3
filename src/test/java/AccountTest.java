import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.ResponseUser;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccountTest extends BaseTest {
    private String accessToken;
    private User user;

    @Before
    public void setupUser() {
        // Создание нового пользователя с случайными данными
        user = new User();
        user.setEmail(generateRandomEmail());
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setName(RandomStringUtils.randomAlphabetic(6));

        // Регистрация пользователя и получение токена доступа
        ResponseUser responseUser = steps
                .createUser(user)
                .extract().as(ResponseUser.class);
        accessToken = responseUser.getAccessToken();

        // Вход в аккаунт
        loginUser(user.getEmail(), user.getPassword());
    }

    private void loginUser(String email, String password) {
        HomePage homePage = new HomePage(webDriver);
        homePage.clickLogInToAccountButton();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.inputLoginInfoUser(email, password);
    }

    private String generateRandomEmail() {
        return RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
    }

    @Test
    @DisplayName("Переход в Личный кабинет")
    @Description("После авторизации пользователь может перейти в свой Личный кабинет")
    public void transferToPersonalAccount() {
        HomePage homePage = new HomePage(webDriver);
        homePage.clickPersonalAccountButton();

        ProfilePage profilePage = new ProfilePage(webDriver);
        assertTrue("Кнопка 'Выход' не отображается", profilePage.exitButtonIsDisplayed());
    }

    @Test
    @DisplayName("Переход из личного кабинета в Конструктор")
    @Description("Из личного кабинета пользователь может перейти в конструктор, нажав кнопку Конструктор")
    public void clickOnTheButtonConstructorOn() {
        navigateToProfilePage();

        ProfilePage profilePage = new ProfilePage(webDriver);
        profilePage.clickConstructorButton();

        HomePage homePage = new HomePage(webDriver);
        assertTrue("Кнопка 'Создать заказ' не активна", homePage.createOrderButtonIsEnabled());
    }

    @Test
    @DisplayName("Переход из личного кабинета в Конструктор(лого)")
    @Description("Из личного кабинета пользователь может перейти в конструктор, нажав на логотип")
    public void clickOnTheButtonLogotype() {
        navigateToProfilePage();

        ProfilePage profilePage = new ProfilePage(webDriver);
        profilePage.clickLogotypeButton();

        HomePage homePage = new HomePage(webDriver);
        assertTrue("Кнопка 'Создать заказ' не активна", homePage.createOrderButtonIsEnabled());
    }

    @Test
    @DisplayName("Выход из личного кабинета")
    @Description("Из личного кабинета можно выйти по кнопке Выход")
    public void clickOnExitButton() {
        navigateToProfilePage();

        ProfilePage profilePage = new ProfilePage(webDriver);
        profilePage.clickExitButton();

        LoginPage loginPage = new LoginPage(webDriver);
        assertTrue("Форма входа не отображается", loginPage.loginInfoIsDisplayed());
    }

    private void navigateToProfilePage() {
        HomePage homePage = new HomePage(webDriver);
        homePage.clickPersonalAccountButton();
    }

    @After
    public void deleteUser() {
        steps.deleteUser(accessToken).statusCode(202);
    }
}
