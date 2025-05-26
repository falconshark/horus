package com.sardo.learnjava.horus.Form;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserForm implements Serializable {
    private String fullName;
    private String username;
    private String emailAddress;
    private String password;
    private String phone;
}