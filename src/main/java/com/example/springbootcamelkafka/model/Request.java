package com.example.springbootcamelkafka.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "request_id")
    @Column(nullable = false)
    private String requestID;

    @JsonFormat(pattern = "code")
    @Column(nullable = false)
    private Integer code;
}
