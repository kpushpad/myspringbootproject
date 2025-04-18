package com.kpushpad.springboot.kvstore.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KvRequest {
    // Getters and Setters
    private String key;
    private String value;
    private Long ttl; // optional

}

