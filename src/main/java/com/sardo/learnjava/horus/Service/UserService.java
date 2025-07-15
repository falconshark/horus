package com.sardo.learnjava.horus.Service;
import java.util.Optional;
import com.sardo.learnjava.horus.Entity.User;

public interface UserService {
	/* 全件取得 */
	Iterable<User> SelectAll();
	
	Optional<User> SelectById(Integer id);

	User SelectByUsername(String username);

	User SelectByEmail(String email);
    
    /* データを更新する */
    void SaveUser(User user);

    void DeleteUser(User user);
}
