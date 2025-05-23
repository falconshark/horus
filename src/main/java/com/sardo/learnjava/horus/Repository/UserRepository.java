package com.sardo.learnjava.horus.Repository;

import org.springframework.data.repository.CrudRepository;

import com.sardo.learnjava.horus.Entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	
}
