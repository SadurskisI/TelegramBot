package tgBotJava.TelegramJavaBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgBotJava.TelegramJavaBot.entity.WorkTime;


public interface WorkTimeRepository extends JpaRepository<WorkTime,Long> {


}
