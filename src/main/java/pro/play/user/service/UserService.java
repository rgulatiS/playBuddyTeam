package pro.play.user.service;

import pro.play.user.model.User;

public interface UserService {
    User register(User user);
    User findByEmail(String email);
}

