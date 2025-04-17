package com.kpushpad.springboot.kvstore.serdser;

import java.io.IOException;

public interface SerialzDSer {
     void serialize(String file, Object obj) throws Exception;
     Object dSerialize(String file) throws IOException;
}
