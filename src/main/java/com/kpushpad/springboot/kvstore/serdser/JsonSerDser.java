package com.kpushpad.springboot.kvstore.serdser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class JsonSerDser implements  SerialzDSer{

    private static final Logger log = LoggerFactory.getLogger(JsonSerDser.class);

    @Override
    public void serialize(String file, Object obj) throws IOException {
        File fHandle = new File(file);
        if (fHandle.createNewFile()) {
            log.debug("Create new file snapshot file: {}", file);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(fHandle, obj);
    }

    @Override
    public Object dSerialize(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, ValueWithTTL<String>>> typeRef = new TypeReference<>() {};
        return mapper.readValue(new File(file), typeRef);
    }
}
