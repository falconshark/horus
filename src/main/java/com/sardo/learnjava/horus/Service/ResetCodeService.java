package com.sardo.learnjava.horus.Service;
import java.util.Optional;
import com.sardo.learnjava.horus.Entity.ResetCode;

public interface ResetCodeService {
	ResetCode SelectByCode(String code);
    
    /* データを更新する */
    void SaveCode(ResetCode code);

    void DeleteCode(ResetCode user);

    String CreateRandomCode();
}