package com.sardo.learnjava.horus.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sardo.learnjava.horus.Entity.ResetCode;
import com.sardo.learnjava.horus.Repository.ResetCodeRepository;

@Service
@Transactional
public class ResetCodeServiceImpl implements ResetCodeService {
	
	@Autowired
	ResetCodeRepository repository;
	public void SaveCode(ResetCode code) {
		repository.save(code);
	}

	@Override
	public ResetCode SelectByCode(String code){
		return repository.findByCode(code);
	}

	@Override
	public void DeleteCode(ResetCode code) {
		repository.delete(code);
	}

	public String CreateRandomCode(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 5;
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
		String randomCode = stringBuilder.toString();
		return randomCode;
	}
}