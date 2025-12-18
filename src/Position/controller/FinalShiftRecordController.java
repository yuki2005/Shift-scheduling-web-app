package Position.controller;

import org.springframework.web.bind.annotation.*;

import Position.FinalShiftRecordSaveRequest;
import Position.entity.FinalShiftRecordEntity;
import Position.service.FinalShiftRecordService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-records")
public class FinalShiftRecordController {

    private final FinalShiftRecordService recordService;

    public FinalShiftRecordController(FinalShiftRecordService service) {
        this.recordService = service;
    }

    @PostMapping
    public FinalShiftRecordEntity save(@RequestBody FinalShiftRecordSaveRequest dto) {
    	Map<String, Object> map = new HashMap<>();
    	map.put("date", dto.getDate());
    	map.put("dayOfWeek", dto.getDayOfWeek());
    	map.put("isHoliday", dto.isHoliday());
    	map.put("message", dto.getMessage());
    	map.put("finalAssignment", dto.getFinalAssignment());

    	return recordService.saveShift(map, false);

    }
    
    @PostMapping("/save")
    public String saveLegacy(@RequestBody Map<String, Object> rawJson) {
    	boolean overwrite = rawJson.containsKey("overwrite")
                && Boolean.TRUE.equals(rawJson.get("overwrite"));
        recordService.saveShift(rawJson, overwrite);
        return "saved";
    }

    @GetMapping("/all")
    public List<FinalShiftRecordEntity> getAll() {
        return recordService.findAll();
    }
    
    @GetMapping("/{date}")
    public FinalShiftRecordEntity getByDate(@PathVariable String date) {
        return recordService.findAll().stream()
                .filter(r -> r.getDate().toString().equals(date))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("指定された日のシフトは存在しません: " + date));
    }

    @GetMapping("/search")
    public List<FinalShiftRecordEntity> searchByDate(@RequestParam String date) {
        LocalDate target = LocalDate.parse(date);
        return recordService.findByDate(target);
    }
    
    @GetMapping("/exists/{date}")
    public Map<String, Boolean> exists(@PathVariable String date) {
        boolean exists = recordService.exists(LocalDate.parse(date));
        return Map.of("exists", exists);
    }
    
    // 修正保存
    @PostMapping("/update")
    public void update(@RequestBody Map<String, Object> body) {

        Long recordId = Long.valueOf(body.get("recordId").toString());
        Map<String, Object> finalAssignment =
                (Map<String, Object>) body.get("finalAssignment");

        recordService.updateShift(recordId, finalAssignment);
    }

}
