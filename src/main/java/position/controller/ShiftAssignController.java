package position.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import position.*;
import position.model.DayOfWeek;
import position.model.Employee;
import position.model.Pos;
import position.model.Schedule;
import position.model.ShiftPreference;
import position.model.ShiftRequest;
import position.model.ShiftResponse;
import position.model.ShiftTime;
import position.service.AutoShift;
import position.service.PosAssign;


@RestController
@RequestMapping("/api/shift")
public class ShiftAssignController {

    private final AutoShift autoShift;
    private final PosAssign posAssign;

    public ShiftAssignController(AutoShift autoShift, PosAssign posAssign) {
        this.autoShift = autoShift;
        this.posAssign = posAssign;
    }

    @PostMapping("/assign")
    public ShiftResponse assignShift(@RequestBody ShiftRequest request) {

        DayOfWeek day = request.getDayOfWeek();
        boolean isHoliday = request.getIsHolidayFlag();
        LocalDate date = request.getDate();

        List<Employee> allAvailableEmployees = request.toEmployeeList();
        List<ShiftPreference> allPreferences = request.toPreferenceList(allAvailableEmployees);

        Schedule shiftConditions = new Schedule(day, isHoliday);

        Map<ShiftTime, List<Employee>> workingStaff =
                autoShift.selectWorkingStaffByTime(allAvailableEmployees, shiftConditions, allPreferences);

        Map<ShiftTime, Map<Pos, List<Employee>>> finalAssignment =
                posAssign.execute(workingStaff, shiftConditions);

        String message = finalAssignment.isEmpty()
                ? "警告: 割り当て可能なポジションが見つかりませんでした。"
                : "シフト割り当てが完了しました。";

        return new ShiftResponse(finalAssignment, workingStaff, message, date, day.name(), isHoliday);
    }
}
