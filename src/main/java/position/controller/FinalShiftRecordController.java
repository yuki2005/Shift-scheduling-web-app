package position.controller;

import org.springframework.web.bind.annotation.*;

import position.FinalShiftRecordSaveRequest;
import position.dto.FinalShiftRecordDto;
import position.dto.FinalShiftUpdateRequest;
import position.entity.FinalShiftRecordEntity;
import position.mapper.FinalShiftRecordMapper;
import position.service.FinalShiftRecordService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 確定シフト履歴に関する REST API を提供する Controller クラス。
 */
@RestController
@RequestMapping("/api/shift-records")
public class FinalShiftRecordController {

    private final FinalShiftRecordService recordService;

    public FinalShiftRecordController(FinalShiftRecordService service) {
        this.recordService = service;
    }

    /**
     * 新形式保存API。
     * DTOベースで受ける。
     */
    @PostMapping
    public FinalShiftRecordDto save(@RequestBody FinalShiftRecordSaveRequest dto) {
        FinalShiftRecordEntity saved = recordService.saveShift(dto, false);
        return FinalShiftRecordMapper.toDto(saved);
    }

    /**
     * 旧形式・互換用API。
     *
     * フロントの saveCurrentShift() が
     * /save に dayOfWeekString を送ってくる仕様を維持する。
     */
    @PostMapping("/save")
    public String saveLegacy(@RequestBody FinalShiftRecordSaveRequest dto) {
        recordService.saveShift(dto, dto.isOverwrite());
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
        FinalShiftRecordEntity record = recordService.findByDate(LocalDate.parse(date))
                .stream()
                .findFirst()
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

    /**
     * 保存済みの確定シフトを更新する。
     *
     * history.js の
     * { recordId, finalAssignment } 形式にそのまま対応する。
     */
    @PostMapping("/update")
    public void update(@RequestBody FinalShiftUpdateRequest body) {
        recordService.updateShift(body.getRecordId(), body.getFinalAssignment());
    }
}