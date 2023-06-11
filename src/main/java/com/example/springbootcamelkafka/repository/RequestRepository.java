package com.example.springbootcamelkafka.repository;

import com.example.springbootcamelkafka.model.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
}
