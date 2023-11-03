package tgBotJava.TelegramJavaBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgBotJava.TelegramJavaBot.entity.User;
import tgBotJava.TelegramJavaBot.repository.UserRepository;
import tgBotJava.TelegramJavaBot.entity.UserRole;
import tgBotJava.TelegramJavaBot.repository.UserRoleRepository;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }


    public void addRoleToUser(User user, String role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        user.getRoles().add(userRole);

        userRepository.save(user);
        userRoleRepository.save(userRole);
    }

}
