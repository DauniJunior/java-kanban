import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
    }


    public Epic(String name, String description, int taskId) {
        super(name, description, Status.NEW);
        this.setId(taskId);
        //this.status = Status.NEW;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }


    @Override
    public String getType() {
        return "Epic";
    }


    @Override
    public String toString() {
        return getType() + "{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
