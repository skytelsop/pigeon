package com.example.pigeon.security;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActiveUserStore {

    public List<String> users;

    public ActiveUserStore() { users = new ArrayList<>(); }
}
