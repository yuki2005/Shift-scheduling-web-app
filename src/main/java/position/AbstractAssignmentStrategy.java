package position;

import java.util.*;

public abstract class AbstractAssignmentStrategy
        implements AssignmentStrategy {

    /**
     * W以外に1つしか対応できないポジションがない従業員を先に割り当てる
     */
    protected Map<Pos, List<Employee>> assignFixedPositionStaff(
            List<Employee> staff,
            Map<Pos, Integer> requiredCount,
            Set<Employee> assignedStaff
    ) {
        Map<Pos, List<Employee>> result = new HashMap<>();

        for (Employee e : staff) {
            List<Pos> available = new ArrayList<>();

            for (Pos p : Pos.values()) {
                if (p != Pos.W && e.getSkill(p) > 0) {
                    available.add(p);
                }
            }

            if (available.size() == 1) {
                Pos fixed = available.get(0);

                result.computeIfAbsent(fixed, k -> new ArrayList<>()).add(e);
                assignedStaff.add(e);

                // 必要人数を減らす
                requiredCount.put(
                        fixed,
                        Math.max(0, requiredCount.getOrDefault(fixed, 0) - 1)
                );
            }
        }
        return result;
    }

    /**
     * ポジションを重み順で取得
     */
    protected List<Pos> getSortedPosByWeight() {
        List<Pos> list = new ArrayList<>(List.of(Pos.values()));
        list.sort((a, b) -> b.getWeight() - a.getWeight());
        return list;
    }

    /**
     * 総合能力値（スキル × 重み）を計算
     */
    protected Map<Employee, Integer> calcOverallSkill(List<Employee> staff) {
        Map<Employee, Integer> result = new HashMap<>();

        for (Employee e : staff) {
            int sum = 0;
            for (Pos p : Pos.values()) {
                sum += e.getSkill(p) * p.getWeight();
            }
            result.put(e, sum);
        }
        return result;
    }
}
