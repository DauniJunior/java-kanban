 public class Subtask extends Task {
    protected final int epicId;


    public  Subtask(String name, String description, int epicId) {
        super(name, description);
        this.status = Status.NEW;
        this.epicId = epicId;
    }


    public  Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }


    public int getEpicId() { return epicId; }


     @Override
     public String getType() {
         return "Subtask";
     }

     @Override
     public String toString() {
         return super.toString().replace("}", "") +
                 ", epicId=" + epicId +
                 '}';
     }

     @Override
     public Subtask copy() {
         Subtask copy = new Subtask(this.name, this.description, this.status, this.epicId);
         copy.setId(this.id);
         return copy;
     }
}
