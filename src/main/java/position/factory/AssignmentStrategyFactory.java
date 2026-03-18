package position.factory;

import org.springframework.stereotype.Component;

import position.strategy.AssignmentStrategy;
import position.strategy.HolidayAssignmentStrategy;
import position.strategy.WeekdayAssignmentStrategy;

@Component
public class AssignmentStrategyFactory {

    private final WeekdayAssignmentStrategy weekday;
    private final HolidayAssignmentStrategy holiday;

    public AssignmentStrategyFactory(
            WeekdayAssignmentStrategy weekday,
            HolidayAssignmentStrategy holiday
    ) {
        this.weekday = weekday;
        this.holiday = holiday;
    }

    public AssignmentStrategy get(boolean isHoliday) {
        return isHoliday ? holiday : weekday;
    }
}
