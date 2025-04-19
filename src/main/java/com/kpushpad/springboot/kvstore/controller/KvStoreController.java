package com.kpushpad.springboot.kvstore.controller;

import com.kpushpad.springboot.kvstore.model.KvRequest;
import com.kpushpad.springboot.kvstore.service.CacheRestoreService;
import com.kpushpad.springboot.kvstore.service.KvStoreBusinessServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/kvstore")
public class KvStoreController {

    private final KvStoreBusinessServ<String, String> kvStoreBusinessServ;
    private final CacheRestoreService cacheRestoreService;

    @Autowired
    KvStoreController(KvStoreBusinessServ<String, String> kvStoreBusinessServ,
                      CacheRestoreService cacheRestoreService) {
        this.kvStoreBusinessServ = kvStoreBusinessServ;
        this.cacheRestoreService = cacheRestoreService;
    }


    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public  ResponseEntity<String>  getValueByKey(@PathVariable String key) {
        if (!cacheRestoreService.isApplicationReadToUse())
            return returnError();
        String v = kvStoreBusinessServ.get(key);
        return ResponseEntity.ok(v == null ? "nil" : v);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    public  ResponseEntity<String>  deleteByKey(@PathVariable String key) throws IOException {
        if (!cacheRestoreService.isApplicationReadToUse())
            return returnError();

        String v = kvStoreBusinessServ.delete(key);
        return ResponseEntity.ok(v == null ? "nil" : v);
    }

    @PostMapping
    public ResponseEntity<String> putByValue(@RequestBody KvRequest request) {
        if (!cacheRestoreService.isApplicationReadToUse())
            return returnError();

        String value = Boolean.TRUE
                .equals(kvStoreBusinessServ.put(request.getKey(), request.getValue(), request.getTtl()))
                ?  "1" : "nil";
        return ResponseEntity.ok(value);
    }


    @RequestMapping(value = "/keys", method = RequestMethod.GET)
    public  ResponseEntity<List<String>>  getAllKeys() {
        if (!cacheRestoreService.isApplicationReadToUse())
            return returnErrorList();

        return ResponseEntity.ok(kvStoreBusinessServ.getAllKeys());
    }

    @RequestMapping(value = "/totalKeys", method = RequestMethod.GET)
    public  ResponseEntity<String>  getTotalKeys() {
        if (!cacheRestoreService.isApplicationReadToUse())
            return returnError();
        return ResponseEntity.ok(kvStoreBusinessServ.getTotalKeysCount().toString());
    }

    public ResponseEntity<String> returnError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Service is not ready yet to be used: ");
    }

    public ResponseEntity<List<String>> returnErrorList() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList("Service is not ready yet to be used: "));
    }

}
