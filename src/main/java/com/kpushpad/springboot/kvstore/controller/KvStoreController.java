package com.kpushpad.springboot.kvstore.controller;

import com.kpushpad.springboot.kvstore.model.KvRequest;
import com.kpushpad.springboot.kvstore.service.KvStoreBusinessServ;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/kvstore")
public class KvStoreController {

    private final KvStoreBusinessServ<String, String> kvStoreBusinessServ;

    @Autowired
     KvStoreController(KvStoreBusinessServ<String, String> kvStoreBusinessServ) {
        this.kvStoreBusinessServ = kvStoreBusinessServ;
    }


    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public  String  getValueByKey(@PathVariable String key) {
        String v = kvStoreBusinessServ.get(key);
        return v == null ? "nil" : v;
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    public  String  deleteByKey(@PathVariable String key) throws IOException {
        String v = kvStoreBusinessServ.delete(key);
        return v == null ? "nil" : v;
    }

    @PostMapping
    public boolean putByValue(@RequestBody KvRequest request) {
        return kvStoreBusinessServ.put(request.getKey(), request.getValue(), request.getTtl());
    }

    @PostConstruct
    public void init() {
        // This method runs once, after the bean is initialized
        kvStoreBusinessServ.put("test", "test", 100L);
    }
}
