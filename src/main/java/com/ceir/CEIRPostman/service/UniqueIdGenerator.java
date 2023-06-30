package com.ceir.CEIRPostman.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIdGenerator {

    private static Map<String, AtomicInteger> counterMap = new HashMap<>();

    public static String generateUniqueId(String operatorName) {
        String shortName = getOperatorShortName(operatorName);
        String timestamp = getCurrentTimestamp();
        int counter = getNextCounter(shortName);
        return shortName + timestamp + String.format("%04d", counter);
    }

    private static String getOperatorShortName(String operatorName) {
        if (operatorName.length() >= 2) {
            return operatorName.substring(0, 2).toLowerCase();
        }
        return operatorName.toLowerCase();
    }

    private static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(formatter);
    }

    private static int getNextCounter(String shortName) {
        counterMap.putIfAbsent(shortName, new AtomicInteger(0));
        return counterMap.get(shortName).incrementAndGet();
    }

}

