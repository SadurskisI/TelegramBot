package tgBotJava.TelegramJavaBot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity(name = "workTime")
public class WorkTime {

    @Id
    private int workId;
    private Long userId;
    private String userName;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private String hoursWorked;
    private String weather;

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }

    public String  getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", hoursWorked=" + hoursWorked +
                ", weather='" + weather + '\'' +
                '}';
    }
}
