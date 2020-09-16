package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudStorageApplicationTests {
    @LocalServerPort
    public int port;
    public String baseURL;

    public static WebDriver driver;

    public SignupPage signupPage;
    public LoginPage loginPage;
    public HomePage homePage;
    public CredentialTabPage credentialTabPage;
    public NotesTabPage notesTabPage;

    public WebDriverWait wait;
    public Boolean mark;
    public WebElement element;

    String firstName = "Yana";
    String lastName = "Test";
    String userName = "TestUser";
    String password = "test123";

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

    }

    @BeforeEach
    public void beforeEach() {
        baseURL = "http://localhost:" + port;
        wait = new WebDriverWait(driver, 100);
    }

    @AfterAll
    public static void afterAll() {
        driver.quit();
        driver = null;
    }

    @Test
    @Order(1)
    public void getLoginPage(){
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    @Test
    @Order(2)
    public void testValidUserSignupAndLogin() throws InterruptedException {
        //To check whether signup page is loading or not
        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        //once signup page is loaded, we will register user
        signupPage = new SignupPage(driver);
        signupPage.signup(firstName, lastName, userName, password);

        //once user is successfully registered we should be able to login
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
        loginPage = new LoginPage(driver);
        loginPage.login(userName, password);
        assertEquals("Home", driver.getTitle());

    }

    @Test
    @Order(3)
    public void testInvalidSignupAndLogin() {
        String firstName = "Yana";
        String lastName = "Test";
        String userName = "IanaTest";
        String password = "123Test";

        //To check whether signup page is loading or not
        driver.get("Http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        //once signup page is loaded, we will register user
        signupPage = new SignupPage(driver);
        signupPage.signup(firstName, lastName, userName, password);

        //once user is successfully registered we should be able to login
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
        loginPage = new LoginPage(driver);
        loginPage.login("tester", password);
        driver.get("Http://localhost:" + this.port + "/login-user");

    }

    @Test
    @Order(4)
    private void doLoginFunction() {
        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        signupPage = new SignupPage(driver);
        signupPage.signup(firstName, lastName, userName, password);

        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
        loginPage = new LoginPage(driver);
        loginPage.login(userName, password);
        assertEquals("Home", driver.getTitle());

    }



    @Test
    @Order(5)
    public void testAddEditDeleteNote() {
        doLoginFunction();

        notesTabPage = new NotesTabPage(driver);
        WebElement nav = driver.findElement(By.id("nav-notes-tab"));
        nav.click();
        notesTabPage.addNote(driver, "This is my title", "This is Description", nav);

        List<String> detail = notesTabPage.getDetail(driver);

        Assertions.assertEquals("This is my title", detail.get(0));
        Assertions.assertEquals("This is Description", detail.get(1));

        notesTabPage.editNote(driver, "Edit title", "Edit Description");

        driver.get("http://localhost:" + this.port +  "/home");

        detail = notesTabPage.getDetail(driver);

        assertEquals("Edit Description", detail.get(0));
        assertEquals("This is Description", detail.get(1));

        notesTabPage.deleteNote(driver);

        driver.get("http://localhost:" + this.port + "/home");

        wait.until(driver -> driver.findElement(By.id("nav-notes-tab"))).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String noteSize = wait.until(driver -> driver.findElement(By.id("note-size")).getText());

        assertEquals("0", noteSize);

        homePage = new HomePage(driver);
        homePage.logout();

        wait.until(ExpectedConditions.titleContains("Login"));

        //  assertEquals("http://localhost:" + this.port + "/login?logout", driver.getCurrentUrl());
        assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
    }

    @Test
    @Order(6)
    public void testAddEditDeleteCredentials() {
        doLoginFunction();

        credentialTabPage = new CredentialTabPage(driver);
        WebElement nav = driver.findElement(By.id("nav-credentials-tab"));
        nav.click();
        driver.findElement(By.id("add-credential"));
        nav.click();
        credentialTabPage.addCredential(driver, "www.superduperdrive.com", "Test", "test123", nav);
        driver.findElement(By.id("credentialSubmit"));
        nav.click();
        List<String> detail = credentialTabPage.getDetail(driver);

        Assertions.assertEquals("www.superduperdrive.com", detail.get(0));
        Assertions.assertEquals("Test", detail.get(1));
        Assertions.assertNotEquals("test123", detail.get(2));

        wait.until(driver -> driver.findElement(By.id("edit-credential"))).click();
        WebElement inputPassword = wait.until(driver -> driver.findElement(By.id("credential-password")));
        String password = inputPassword.getAttribute("value");
        Assertions.assertEquals("test123", password);
        credentialTabPage.close(driver);

        credentialTabPage.editCredential(driver, "www.superduperdrive.com", "Test1", "testPass");

        driver.get("http://localhost:" + this.port + "/home");

        detail = credentialTabPage.getDetail(driver);

        Assertions.assertEquals("www.superduperdrive.com", detail.get(0));
        Assertions.assertEquals("Test1", detail.get(1));
        Assertions.assertEquals("testPass", detail.get(2));

    }
}
