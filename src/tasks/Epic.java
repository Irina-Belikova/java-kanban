package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        status = TaskStatus.NEW;
        subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, ArrayList<Integer> subtasksId) {
        super(name, description);
        this.id = id;
        status = TaskStatus.NEW;
        this.subtasksId = subtasksId;
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtasksId) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.subtasksId = subtasksId;
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(name, description);
        this.id = id;
        this.status = status;
        subtasksId = new ArrayList<>();
    }

    @Override
    public Task getOldTask() {
        return (new Epic(this.id, this.name, this.description, this.status, this.subtasksId));
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void deleteSubtask(Integer id) {
        subtasksId.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasksId=" + subtasksId +
                "} ";
    }
}
