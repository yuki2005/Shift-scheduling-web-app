package Position.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Position.ShiftPreference;
import Position.dto.ShiftPreferenceDto;
import Position.Employee;
import Position.service.ShiftPreferenceService;
import Position.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/preferences")
public class ShiftPreferenceController {

    private final ShiftPreferenceService prefService;
    private final EmployeeService employeeService;

    public ShiftPreferenceController(
            ShiftPreferenceService prefService,
            EmployeeService employeeService
    ) {
        this.prefService = prefService;
        this.employeeService = employeeService;
    }

    /**
     * POST /api/preferences
     * 希望シフトの保存
     */
    @PostMapping
    public  ResponseEntity<String> save(@RequestBody ShiftPreferenceDto dto) {

        Employee emp = employeeService.findByEmployeeNumber(dto.getEmployeeId());

        ShiftPreference pref = new ShiftPreference(
                emp,
                dto.getAvailabilityMap().entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> Position.ShiftTime.valueOf(e.getKey()),
                                Map.Entry::getValue
                        )),
                dto.getDate()
        );

        prefService.savePreference(pref);
        return ResponseEntity.status(HttpStatus.CREATED).body("saved");
    }

    @GetMapping("/{employeeNumber}/{date}")
    public ShiftPreference getOne(
            @PathVariable int employeeNumber,
            @PathVariable String date
    ) {
        return prefService.findOne(employeeNumber, LocalDate.parse(date));
    }

    @GetMapping("/employee/{employeeNumber}")
    public List<ShiftPreference> getByEmployee(@PathVariable int employeeNumber) {
        return prefService.findByEmployeeNumber(employeeNumber);
    }

    @GetMapping("/date/{date}")
    public List<ShiftPreference> getByDate(@PathVariable String date) {
        LocalDate d = LocalDate.parse(date);
        List<ShiftPreference> list = prefService.findByDate(d);

        // employee_number から Employee を紐付ける
        return list.stream()
            .map(pref -> {
                Employee emp = employeeService.findByEmployeeNumber(pref.getEmployee().getId());
                return new ShiftPreference(
                        emp,
                        pref.getAvailabilityMap(),
                        pref.getDate()
                );
            })
            .collect(Collectors.toList());
    }

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
