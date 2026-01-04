import java.util.ArrayList;
import java.util.List;

public class PerformanceManager {
    private static List<perfStock> allPerformances = new ArrayList<>();

    public static void addPerformance(perfStock point) {
        allPerformances.add(point);
    }

    public static List<perfStock> getAllPerformances() {
        return new ArrayList<>(allPerformances);
    }

    public static List<perfStock> getPerformancesByExercise(String exerciseName) {
        return allPerformances.stream()
                .filter(p -> p.exerciseName.equalsIgnoreCase(exerciseName))
                .toList();
    }

    public static List<perfStock> getSkillPerformances() {
        return allPerformances.stream()
                .filter(p -> p.isSkill)
                .toList();
    }


    public static boolean checkIfSkill(String exerciseName) {
        String[] skills = {
                "Planche", "Front lever", "Back lever",
                "Handstand",  "L Sit", "V Sit", "I Sit",
                "Pellican", "Hefesto"
        };

        for (String skill : skills) {
            if (exerciseName.equalsIgnoreCase(skill)) return true;
        }
        return false;
    }



    public static boolean checkIfRep(String exerciseName) {
        return !checkIfSkill(exerciseName);
    }
}
