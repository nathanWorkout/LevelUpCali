import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static List<HistoryEntry> historyEntries = new ArrayList<>();


    public static void addEntry(String title, String date) {
        historyEntries.add(0, new HistoryEntry(title, date));
    }


    public static void addEntry(String title, String date, String exerciseName, int reps, int hold) {
        historyEntries.add(0, new HistoryEntry(title, date, exerciseName, reps, hold));

        // Ajouter automatiquement dans PerformanceManager
        boolean isSkill = PerformanceManager.checkIfSkill(exerciseName);
        PerformanceManager.addPerformance(
                new perfStock(
                        parseDate(date),  // Convertir la date String en LocalDate
                        exerciseName,
                        reps,
                        hold,
                        isSkill
                )
        );
    }

    public static List<HistoryEntry> getEntries() {
        return new ArrayList<>(historyEntries);
    }

    // Convertir une date String (ex: "24/11/2025") en LocalDate
    private static LocalDate parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("/");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser la date du jour
        }
        return LocalDate.now();
    }

    public static class HistoryEntry {
        private String title;
        private String date;
        private String exerciseName;
        private int reps;
        private int hold;

        // Constructeur d'origine
        public HistoryEntry(String title, String date) {
            this(title, date, "", 0, 0);
        }

        // Constructeur avec performances
        public HistoryEntry(String title, String date, String exerciseName, int reps, int hold) {
            this.title = title;
            this.date = date;
            this.exerciseName = exerciseName;
            this.reps = reps;
            this.hold = hold;
        }

        public String getTitle() { return title; }
        public String getDate() { return date; }
        public String getExerciseName() { return exerciseName; }
        public int getReps() { return reps; }
        public int getHold() { return hold; }
    }
}