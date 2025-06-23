package com.sardo.learnjava.horus.Repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sardo.learnjava.horus.Entity.ResetCode;

public interface ResetCodeRepository extends CrudRepository<ResetCode, Integer> {
    public ResetCode findByCode(String code);
}
