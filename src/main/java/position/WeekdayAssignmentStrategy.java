package position;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class WeekdayAssignmentStrategy
        extends AbstractAssignmentStrategy {

    @Override
    public Map<Pos, List<Employee>> assign(
            List<Employee> staff,
            Schedule conditions,
            ShiftTime time
    ) {
        Map<Pos, List<Employee>> result = new HashMap<>();
        Set<Employee> assigned = new HashSet<>();

        Map<Pos, Integer> requiredCount =
                new HashMap<>(conditions.getRequiredCountsByTime().get(time));

        // 共通ルール
        result.putAll(assignFixedPositionStaff(staff, requiredCount, assigned));

        // Aが空いていたら総合能力最大を割り当てる
        if (requiredCount.getOrDefault(Pos.A, 0) > 0) {
            Employee best = calcOverallSkill(staff).entrySet().stream()
                    .filter(e -> !assigned.contains(e.getKey()))
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (best != null) {
                result.computeIfAbsent(Pos.A, k -> new ArrayList<>()).add(best);
                assigned.add(best);
                requiredCount.put(Pos.A, requiredCount.get(Pos.A) - 1);
            }
        }

        // 残りはポジション重み順
        for (Pos p : getSortedPosByWeight()) {
            int count = requiredCount.getOrDefault(p, 0);

            for (int i = 0; i < count; i++) {
                for (Employee e : staff) {
                    if (!assigned.contains(e) && e.getSkill(p) > 0) {
                        result.computeIfAbsent(p, k -> new ArrayList<>()).add(e);
                        assigned.add(e);
                        break;
                    }
                }
            }
        }

        result.replaceAll((k, v) -> Collections.unmodifiableList(v));
        return Collections.unmodifiableMap(result);
    }
}
