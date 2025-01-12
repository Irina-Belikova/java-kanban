package tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        status = TaskStatus.NEW;
    }

    public Subtask(int id, String name, String description, int epicId, TaskStatus status) {
        super(name, description);
        this.id = id;
        this.epicId = epicId;
        this.status = status;
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
                ", status=" + status +
                "} ";
    }
}
