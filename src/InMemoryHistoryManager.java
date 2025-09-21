import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        int taskId = task.getId();

        // Если задача уже есть в истории - удаляем старый узел
        if (nodeMap.containsKey(taskId)) {
            removeNode(nodeMap.get(taskId));
            nodeMap.remove(taskId);
        }

        // Создаем копию задачи и добавляем в конец списка
        Task taskCopy = task.copy();
        Node newNode = new Node(taskCopy);
        linkLast(newNode);
        nodeMap.put(taskId, newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodeMap.get(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            nodeMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Добавляем узел в конец списка
    private void linkLast(Node newNode) {
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Удаляем узел из списка
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // Удаляем голову
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // Удаляем хвост
        }

        // Очищаем ссылки удаленного узла
        node.prev = null;
        node.next = null;
    }

    // Внутренний класс для узла двусвязного списка
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

}
