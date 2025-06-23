package com.sardo.learnjava.horus.Form;

import lombok.Data;
import java.io.Serializable;

@Data
public class ForgetForm implements Serializable {
    private String emailAddress;
}