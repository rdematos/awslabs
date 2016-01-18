package com.amazonaws.com.amazonaws.model;

import java.util.UUID;

/**
 * Created by Joey Grover on 12/16/15.
 */
public class ClientId {

    private static String uuid = null;

    public synchronized static String getUID(){
        if(uuid == null){
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }
}
