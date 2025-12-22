package Position;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PosAssign {

    private final AssignmentStrategyFactory strategyFactory;

    public PosAssign(AssignmentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    /**
     * 時間帯ごとにポジション割り当てを行う
     */
    public Map<ShiftTime, Map<Pos, List<Employee>>> execute(
            Map<ShiftTime, List<Employee>> staffByTime,
            Schedule conditions
    ) {
        Map<ShiftTime, Map<Pos, List<Employee>>> result = new HashMap<>();

        // ★ Schedule から休日判定を取得
        boolean isHoliday = conditions.isHoliday();

        // ★ Strategy はここで一度だけ決定
        AssignmentStrategy strategy = strategyFactory.get(isHoliday);

        for (ShiftTime targetTime : ShiftTime.values()) {

            List<Employee> staffForTime =
                    staffByTime.getOrDefault(targetTime, Collections.emptyList());

            if (staffForTime.isEmpty()) {
                continue;
            }

            Map<Pos, List<Employee>> posAssignments =
                    strategy.assign(staffForTime, conditions, targetTime);

            result.put(targetTime, posAssignments);
        }

        return Collections.unmodifiableMap(result);
    }
}
