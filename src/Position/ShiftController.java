package Position;

import org.springframework.web.bind.annotation.*;
import Position.entity.FinalShiftRecordEntity;
import Position.service.FinalShiftRecordService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift")
public class ShiftController {

    private final AutoShift autoShift;
    private final PosAssign posAssign;
    private final FinalShiftRecordService shiftRecordService;

    public ShiftController(AutoShift autoShift,
                           PosAssign posAssign,
                           FinalShiftRecordService shiftRecordService) {
        this.autoShift = autoShift;
        this.posAssign = posAssign;
        this.shiftRecordService = shiftRecordService;
    }

    @PostMapping("/assign")
    public ShiftResponse assignShift(@RequestBody ShiftRequest request) {

        DayOfWeek day = request.getDayOfWeek();
        boolean isHoliday = request.getIsHolidayFlag();
        String date = request.getDate();
        
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

        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }
        String dayString = day.name();

        return new ShiftResponse(finalAssignment, workingStaff, message, date, dayString, isHoliday);
    }

    // 🔥 JSON をそのまま Map で受け取って保存
    @PostMapping("/save")
    public String saveShift(@RequestBody Map<String, Object> rawJson) {
        shiftRecordService.saveShift(rawJson);
        return "saved";
    }

    @GetMapping("/{date}")
    public FinalShiftRecordEntity getShift(@PathVariable String date) {
        LocalDate d = LocalDate.parse(date);
        return shiftRecordService.findAll().stream()
                .filter(r -> r.getDate().equals(d))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("指定された日のシフトは存在しません: " + date));
    }

    @GetMapping("/all")
    public List<FinalShiftRecordEntity> getAll() {
        return shiftRecordService.findAll();
    }
}
