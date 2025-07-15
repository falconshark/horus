package com.sardo.learnjava.horus.Form;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResetForm implements Serializable {
    private String resetCode;
    private String password ;
}