package com.github.pmoerenhout.jsmpp.web.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.pmoerenhout.jsmpp.web.jpa.model.Dr;

@Repository
public interface DrRepository extends JpaRepository<Dr, Long> {

  Dr findOneByMessageId(@Param("messageId") String messageId);

  Dr findOneByMessageIdAndSource(@Param("messageId") String messageId, @Param("source") String source);
}