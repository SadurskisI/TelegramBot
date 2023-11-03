package tgBotJava.TelegramJavaBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tgBotJava.TelegramJavaBot.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT count(is_active) FROM `tg-bot-v1`.users count where is_active = 'True';", nativeQuery = true)
    int findActiveUsers();

    User findByChatId(long chatId);

}
