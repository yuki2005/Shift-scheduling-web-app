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

/**
 * 確定シフト履歴に関する REST API を提供する Controller クラス。
 *
 * ・自動生成されたシフト結果の保存
 * ・保存済みシフト履歴の取得・検索
 * ・既存シフトの更新（手動修正反映）
 *
 * 業務ロジックは FinalShiftRecordService に委譲し、
 * 本クラスは API の入出力制御に専念する。
 *
 * エンドポイント: /api/shift-records
 */

@RestController
@RequestMapping("/api/shift-records")
public class FinalShiftRecordController {
	
	// 確定シフト履歴に関する業務ロジックを扱う Service
    private final FinalShiftRecordService recordService;
    
    // FinalShiftRecordService を注入するコンストラクタ
    public FinalShiftRecordController(FinalShiftRecordService service) {
        this.recordService = service;
    }
    
    /**
     * 確定シフト結果を保存する（通常利用API）。
     *
     * ・DTO形式で受け取ったデータを内部形式に変換
     * ・同一日付の既存シフトがある場合は上書きせず保存を行う
     *
     * @param dto フロントエンドから送信された確定シフト情報
     * @return 保存された確定シフト履歴
     */
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
    
    /**
     * 確定シフト結果を保存する（旧形式・互換用API）。
     *
     * ・Map形式のJSONをそのまま受け取る
     * ・overwrite フラグが指定された場合は既存データを上書きする
     *
     * @param rawJson 確定シフト情報（JSON形式）
     * @return 保存結果メッセージ
     */
    @PostMapping("/save")
    public String saveLegacy(@RequestBody Map<String, Object> rawJson) {
    	boolean overwrite = rawJson.containsKey("overwrite")
                && Boolean.TRUE.equals(rawJson.get("overwrite"));
        recordService.saveShift(rawJson, overwrite);
        return "saved";
    }
    
    /**
     * 保存されている全ての確定シフト履歴を取得する。
     *
     * @return 確定シフト履歴一覧
     */
    @GetMapping("/all")
    public List<FinalShiftRecordDto> getAll() {
    	return recordService.findAll().stream()
                .map(FinalShiftRecordMapper::toDto)
                .toList();
    }
    
    /**
     * 指定した日付の確定シフト履歴を1件取得する。
     *
     * @param date 対象日付（yyyy-MM-dd）
     * @return 該当する確定シフト履歴
     */
    @GetMapping("/{date}")
    public FinalShiftRecordDto getByDate(@PathVariable String date) {
        FinalShiftRecordEntity record = recordService.findByDate(
                LocalDate.parse(date)
        ).stream().findFirst()
         .orElseThrow(() -> new RuntimeException("存在しません"));

        return FinalShiftRecordMapper.toDto(record);
    }

    /**
     * 指定した日付の確定シフト履歴を検索する。
     *
     * @param date 検索対象日付（yyyy-MM-dd）
     * @return 該当する確定シフト履歴一覧
     */
    @GetMapping("/search")
    public List<FinalShiftRecordDto> searchByDate(@RequestParam String date) {
        LocalDate target = LocalDate.parse(date);
        return recordService.findByDate(target).stream()
                .map(FinalShiftRecordMapper::toDto)
                .toList();
    }

    /**
     * 指定した日付の確定シフトが既に存在するかを判定する。
     *
     * @param date 判定対象日付（yyyy-MM-dd）
     * @return 存在有無
     */
    @GetMapping("/exists/{date}")
    public Map<String, Boolean> exists(@PathVariable String date) {
        boolean exists = recordService.exists(LocalDate.parse(date));
        return Map.of("exists", exists);
    }
    
    /**
     * 保存済みの確定シフトを更新する（手動修正反映用）。
     *
     * ・recordId を基に更新対象を特定
     * ・シフト割り当て情報を最新内容で置き換える
     *
     * @param body 更新情報（recordId / finalAssignment）
     */
    @PostMapping("/update")
    public void update(@RequestBody Map<String, Object> body) {

        Long recordId = Long.valueOf(body.get("recordId").toString());
        Map<String, Object> finalAssignment =
                (Map<String, Object>) body.get("finalAssignment");

        recordService.updateShift(recordId, finalAssignment);
    }

}
