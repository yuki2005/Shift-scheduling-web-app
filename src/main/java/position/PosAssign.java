package position;

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

        // Schedule から休日判定を取得
        boolean isHoliday = conditions.isHoliday();

        // 選定ロジックをisHolidayによって決定する
        AssignmentStrategy strategy = strategyFactory.get(isHoliday);
        
        // 時間帯ごとに選定ロジックを実行する
        for (ShiftTime targetTime : ShiftTime.values()) {
        	
        	// 時間帯ごとの選ばれた従業員を格納する
            List<Employee> staffForTime =
                    staffByTime.getOrDefault(targetTime, Collections.emptyList());
            
            // 誰も選ばれなかったら処理を飛ばして次の時間帯に移る
            if (staffForTime.isEmpty()) {
                continue;
            }
            
            // 選定された従業員をポジションに割り振る
            Map<Pos, List<Employee>> posAssignments =
                    strategy.assign(staffForTime, conditions, targetTime);
            
            // 各時間帯の選定された従業員リストを格納したMapに新たに決定された従業員リストを追加する
            result.put(targetTime, posAssignments);
        }

        return Collections.unmodifiableMap(result);
    }
}
