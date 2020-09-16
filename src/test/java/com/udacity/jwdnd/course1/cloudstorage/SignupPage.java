package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {

    @FindBy(id="inputFirstName")
    private WebElement firstNameField;

    @FindBy(id="inputLastName")
    private WebElement lastnameField;

    @FindBy(id="inputUsername")
    private WebElement usernameField;

    @FindBy(id="inputPassword")
    private WebElement passwordField;

    @FindBy(id="submit-button")
    private WebElement submitButton;

    public SignupPage(WebDriver driver){
        PageFactory.initElements(driver, this);
    }

    public void signup(String firstName, String lastName, String userName, String password){
        this.firstNameField.sendKeys(firstName);
        this.lastnameField.sendKeys(lastName);
        this.usernameField.sendKeys(userName);
        this.passwordField.sendKeys(password);
        this.submitButton.click();
    }
}
