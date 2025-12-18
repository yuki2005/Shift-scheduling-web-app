package Position.controller;

import org.springframework.web.bind.annotation.*;

import Position.FinalShiftRecordSaveRequest;
import Position.dto.FinalShiftRecordDto;
import Position.entity.FinalShiftRecordEntity;
import Position.mapper.FinalShiftRecordMapper;
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
    public FinalShiftRecordDto save(@RequestBody FinalShiftRecordSaveRequest dto) {
    	Map<String, Object> map = new HashMap<>();
    	map.put("date", dto.getDate());
    	map.put("dayOfWeek", dto.getDayOfWeek());
    	map.put("isHoliday", dto.isHoliday());
    	map.put("message", dto.getMessage());
    	map.put("finalAssignment", dto.getFinalAssignment());

    	FinalShiftRecordEntity saved =
                recordService.saveShift(map, false);

        return FinalShiftRecordMapper.toDto(saved);

    }
    
    @PostMapping("/save")
    public String saveLegacy(@RequestBody Map<String, Object> rawJson) {
    	boolean overwrite = rawJson.containsKey("overwrite")
                && Boolean.TRUE.equals(rawJson.get("overwrite"));
        recordService.saveShift(rawJson, overwrite);
        return "saved";
    }

    @GetMapping("/all")
    public List<FinalShiftRecordDto> getAll() {
    	return recordService.findAll().stream()
                .map(FinalShiftRecordMapper::toDto)
                .toList();
    }
    
    @GetMapping("/{date}")
    public FinalShiftRecordDto getByDate(@PathVariable String date) {
        FinalShiftRecordEntity record = recordService.findByDate(
                LocalDate.parse(date)
        ).stream().findFirst()
         .orElseThrow(() -> new RuntimeException("存在しません"));

        return FinalShiftRecordMapper.toDto(record);
    }


    @GetMapping("/search")
    public List<FinalShiftRecordDto> searchByDate(@RequestParam String date) {
        LocalDate target = LocalDate.parse(date);
        return recordService.findByDate(target).stream()
                .map(FinalShiftRecordMapper::toDto)
                .toList();
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
