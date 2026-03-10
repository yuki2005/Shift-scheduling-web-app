package position;

import org.springframework.stereotype.Component;

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
