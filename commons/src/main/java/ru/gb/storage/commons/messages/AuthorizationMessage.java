package ru.gb.storage.commons.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorizationMessage extends Message {
    @JsonProperty("login")
    private String login;
    @JsonProperty("password")
    private String password;
    @JsonProperty("authorizationStatus")
    private boolean authorizationStatus;

    public AuthorizationMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public AuthorizationMessage() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getAuthorizationStatus() {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(boolean status) {
        this.authorizationStatus = status;
    }

}




