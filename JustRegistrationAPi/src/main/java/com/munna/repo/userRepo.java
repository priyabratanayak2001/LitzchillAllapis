package com.munna.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.munna.entity.User;


@Repository
public interface userRepo extends JpaRepository<User, Integer> {
	
	Optional<User> findByEmail(String email);

}
