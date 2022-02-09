package com.alo.digital.conectividad.sftpserver.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class User {
    String usuario;
    String password;

    public User(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public User() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
