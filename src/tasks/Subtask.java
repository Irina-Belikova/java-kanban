package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        status = TaskStatus.NEW;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        status = TaskStatus.NEW;
    }

    public Subtask(int id, String name, String description, LocalDateTime startTime, Duration duration, int epicId, TaskStatus status) {
        super(name, description, startTime, duration);
        this.id = id;
        this.epicId = epicId;
        this.status = status;
    }

    @Override
    public Task getOldTask() {
        return (new Subtask(this.id, this.name, this.description, this.startTime, this.duration, this.epicId, this.status));
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                "} ";
    }
}
