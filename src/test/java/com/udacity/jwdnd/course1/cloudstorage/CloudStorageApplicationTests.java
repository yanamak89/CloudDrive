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
        driver.get("http://localhost:" + this.port +  "/result");
        driver.findElement(By.id("home-link")).click();
        driver.get("http://localhost:" + this.port +  "/home");
        driver.findElement(By.id("nav-notes-tab"));

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
        //Add credentials
        credentialTabPage = new CredentialTabPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-credential"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url")))
                .sendKeys("www.superduperdrive.com");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-userName")))
                .sendKeys("Test UserName");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-password")))
                .sendKeys("test123");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-modal-submit"))).click();

        //Check added credentials
        List<String> detail = credentialTabPage.getDetail(driver);
        Assertions.assertEquals("www.superduperdrive.com", detail.get(0));
        Assertions.assertEquals("Test UserName", detail.get(1));
        Assertions.assertNotEquals("test123", detail.get(2));


        //Editing the username only
        driver.get("http://localhost:" + this.port + "/home");
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-credential"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url"))).clear();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url")))
                .sendKeys("www.superduperdrive.com");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-userName"))).clear();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-userName")))
                .sendKeys("Test");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-password")))
                .sendKeys("test123");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-modal-submit"))).click();

        //Check edited credentials
        driver.get("http://localhost:" + this.port + "/home");
        detail=credentialTabPage.getDetail(driver);
        Assertions.assertEquals("www.superduperdrive.com",detail.get(0));
        Assertions.assertEquals("Test", detail.get(1));
        Assertions.assertNotEquals("test123", detail.get(2));

        //Delete credentials
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("delete-credential"))).click();
        driver.get("http://localhost:" + this.port + "/result");

        driver.get("http://localhost:" + this.port + "/home");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e) {
            e.printStackTrace();
            System.out.println("InterruptedException");
        }
        driver.get("http://localhost:" + this.port + "/home");

        homePage = new HomePage(driver);
        homePage.logout();
        wait.until(ExpectedConditions.titleContains("Login"));
        Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());

    }
}
