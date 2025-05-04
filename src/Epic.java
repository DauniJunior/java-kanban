public class Epic extends Task {


    public Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
    }


    public Epic(String name, String description, int taskId) {
        super(name, description);
        this.id = taskId;
        this.status = Status.NEW;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
