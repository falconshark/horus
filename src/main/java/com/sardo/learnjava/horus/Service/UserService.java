package com.sardo.learnjava.horus.Service;
import java.util.Optional;
import com.sardo.learnjava.horus.Entity.User;

public interface UserService {
	/* 全件取得 */
	Iterable<User> SelectAll();
	
	/**
	 * id(主キー)をキーにして1件取得する
	 * 戻り値型はOptional型を使用 
	 * isPresent()を使用でき、値がある場合はtrueを返す
	 */
	Optional<User> SelectById(Integer id);

	User SelectByUsername(String username);

	User SelectByEmail(String email);
    
    /* データを更新する */
    void SaveUser(User user);

    void DeleteUser(User user);
}
