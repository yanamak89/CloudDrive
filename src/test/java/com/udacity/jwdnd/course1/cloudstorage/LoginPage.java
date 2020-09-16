package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    @FindBy(id="inputUsername")
    private WebElement userName;

    @FindBy(id="inputPassword")
    private WebElement password;

    @FindBy(id="signup-msg")
    private WebElement signupSuccess;

    @FindBy(id="submit-button")
    private WebElement button;

    public LoginPage(WebDriver webDriver){
        PageFactory.initElements(webDriver, this);
    }

    public void login(String userName, String password){
        this.userName.sendKeys(userName);
        this.password.sendKeys(password);
        this.button.click();
    }
}
