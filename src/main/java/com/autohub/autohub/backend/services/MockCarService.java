package com.autohub.autohub.backend.services;

import java.util.ArrayList;
import java.util.List;

public class MockCarService {
    public List<String> getAllCarNames() {
        List<String> list = new ArrayList<>();
        list.add("Any");
        list.add("Range Rover Sport");
        list.add("BMW 5 Series");
        list.add("Mercedes-Benz E-Class");
        list.add("Toyota Corolla");
        return list;
    }
}
