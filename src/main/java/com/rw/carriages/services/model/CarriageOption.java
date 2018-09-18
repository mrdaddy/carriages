package com.rw.carriages.services.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CarriageOption {
    private List<String> availableServiceClasses = new ArrayList<>();

}
