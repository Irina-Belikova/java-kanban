package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        status = TaskStatus.NEW;
        duration = Duration.ZERO;
        subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, ArrayList<Integer> subtasksId, LocalDateTime startTime,
                Duration duration) {
        super(name, description, startTime, duration);
        this.id = id;
        status = TaskStatus.NEW;
        this.subtasksId = subtasksId;
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtasksId,
                LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.id = id;
        this.status = status;
        this.subtasksId = subtasksId;
    }

    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        super(name, description, startTime, duration);
        this.id = id;
        this.status = status;
        subtasksId = new ArrayList<>();
    }

    @Override
    public Task getOldTask() {
        return (new Epic(this.id, this.name, this.description, this.status, this.subtasksId, this.startTime, this.duration));
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void deleteSubtask(Integer id) {
        subtasksId.remove(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasksId=" + subtasksId +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                "} ";
    }
}
