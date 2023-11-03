package tgBotJava.TelegramJavaBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgBotJava.TelegramJavaBot.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
}
