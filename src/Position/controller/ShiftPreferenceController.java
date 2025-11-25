package Position.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import Position.ShiftPreference;
import Position.ShiftPreferenceDto;
import Position.service.ShiftPreferenceService;
import Position.service.EmployeeService;
import Position.Employee;

@RestController
@RequestMapping("/api/preferences")
public class ShiftPreferenceController {

    private final ShiftPreferenceService service;
    private final EmployeeService employeeService; // ← 追加

    public ShiftPreferenceController(
            ShiftPreferenceService service,
            EmployeeService employeeService   // ← 追加
    ) {
        this.service = service;
        this.employeeService = employeeService; // ← 追加
    }

    // 1件保存
    @PostMapping
    public ShiftPreference save(@RequestBody ShiftPreferenceDto dto) {

        // 🔥 DBから従業員を取得（ここが今回の修正ポイント）
        Employee emp = employeeService.findByEmployeeNumber(dto.getEmployeeId());

        ShiftPreference pref = ShiftPreference.fromStringMap(
                emp,
                dto.getAvailabilityMap(),
                dto.getDate().toString()
        );

        return service.save(pref);
    }

    @GetMapping("/employee/{employeeNumber}")
    public List<ShiftPreference> getByEmployee(@PathVariable int employeeNumber) {
        return service.findByEmployeeNumber(employeeNumber);
    }

    @GetMapping("/date/{date}")
    public List<ShiftPreference> getByDate(@PathVariable String date) {
        return service.findByDate(LocalDate.parse(date));
    }

    @GetMapping("/{employeeNumber}/{date}")
    public ShiftPreference getOne(@PathVariable int employeeNumber, @PathVariable String date) {
        return service.findOne(employeeNumber, LocalDate.parse(date));
    }
}
