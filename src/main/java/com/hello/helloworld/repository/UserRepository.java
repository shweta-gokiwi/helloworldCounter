package com.hello.helloworld.repository;

import com.hello.helloworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    @Transactional
    @Modifying
    @Query("update User u set u.name = :name where u.id = :id")
    int updateNameById(@Param("name") String name, @Param("id") Long id);
}