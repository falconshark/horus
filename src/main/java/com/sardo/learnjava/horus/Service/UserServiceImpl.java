package com.sardo.learnjava.horus.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sardo.learnjava.horus.Entity.User;
import com.sardo.learnjava.horus.Repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository repository;
	public void SaveUser(User user) {
		repository.save(user);
	}

	@Override
	public void DeleteUser(User user) {
		repository.delete(user);
	}

	@Override
	public Iterable<User> SelectAll() {
		return repository.findAll();
	}

	@Override
	public Optional<User> SelectById(Integer id) {
		return repository.findById(id);
	}

	@Override
	public User SelectByUsername(String username){
		return repository.findByUsername(username);
	}
	
	@Override
	public User SelectByEmail(String email){
		return repository.findByEmailAddress(email);
	}
}