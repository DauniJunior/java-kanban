import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;
        history.add(task.copy());
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private Task copyTask(Task original) {
        if (original instanceof Epic) {
            Epic epic = (Epic) original;
            Epic copy = new Epic(epic.getName(), epic.getDescription());
            copy.setId(epic.getId());
            copy.status = epic.getStatus();
            return copy;
        } else if (original instanceof Subtask) {
            Subtask subtask = (Subtask) original;
            Subtask copy = new Subtask(subtask.getName(), subtask.getDescription(),
                    subtask.getStatus(), subtask.getEpicId());
            copy.setId(subtask.getId());
            return copy;
        } else {
            Task task = new Task(original.getName(), original.getDescription(), original.getStatus());
            task.setId(original.getId());
            return task;
        }
    }
}
