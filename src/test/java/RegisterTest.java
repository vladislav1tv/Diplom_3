import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.ResponseUser;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RegisterTest extends BaseTest {
    private final String name = RandomStringUtils.randomAlphabetic(6);
    private final String email = generateRandomEmail();
    private final String password = RandomStringUtils.randomAlphabetic(6);

    @Test
    @DisplayName("Регистрация пользователя")
    @Description("Для регистрации пользователь должен заполнить поля имени, email и пароля")
    public void registerTest() {
        HomePage homePage = new HomePage(webDriver);
        homePage.clickPersonalAccountButton();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.clickRegisterButton();

        RegisterPage registerPage = new RegisterPage(webDriver);
        registerPage.inputLoginInfoUser(name, email, password);

        // Ожидание загрузки страницы после регистрации
        loginPage.waitForLoad();

        // Попытка входа с использованием зарегистрированных данных
        loginPage.inputLoginInfoUser(email, password);

        assertTrue("Кнопка создания заказа не активна", homePage.createOrderButtonIsEnabled());
    }

    @After
    public void deleteUser() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);

        // Аутентификация пользователя для получения токена
        ResponseUser responseUser = steps
                .authUser(user)
                .extract().as(ResponseUser.class);
        String accessToken = responseUser.getAccessToken();

        // Удаление пользователя
        steps.deleteUser(accessToken).statusCode(202);
    }

    private String generateRandomEmail() {
        return RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
    }
}