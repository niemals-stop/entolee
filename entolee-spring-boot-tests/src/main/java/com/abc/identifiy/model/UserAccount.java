package com.abc.identifiy.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserAccount {

    private final UUID id;
    private final String name;

}
