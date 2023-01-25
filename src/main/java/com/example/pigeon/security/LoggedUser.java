package com.example.pigeon.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class LoggedUser implements HttpSessionBindingListener, Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private ActiveUserStore activeUserStore;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();

        if(!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {

        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();

        users.remove(user.getUsername());
    }
}
