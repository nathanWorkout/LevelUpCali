import java.time.LocalDate;

public class perfStock {

    public LocalDate date;
    public String exerciseName;
    public int reps;
    public int hold;
    public boolean isSkill;

    public perfStock(LocalDate date, String exerciseName, int reps, int hold, boolean isSkill) {
        this.date = date;
        this.exerciseName = exerciseName;
        this.reps = reps;
        this.hold = hold;
        this.isSkill = isSkill;
    }
}
