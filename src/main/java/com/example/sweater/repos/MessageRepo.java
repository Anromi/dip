package com.example.sweater.repos;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.domain.Mess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MessageRepo extends CrudRepository<Message, Long> {

    Page<Mess> findAll(Pageable pageable, @Param("user") User user);

    Page<Mess> findByTag(@Param("tag") String tag, Pageable pageable, @Param("user") User user);

    Page<Mess> findByUser(Pageable pageable, @Param("author") User author, @Param("user") User user);
}
