package Position.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Position.dto.ShiftPreferenceDto;
import Position.service.EmployeeService;
import Position.service.ShiftPreferenceService;
import Position.Employee;
import Position.ShiftPreference;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 従業員のシフト希望に関する REST API を提供する Controller クラス。
 *
 * ・従業員から提出された希望シフトの保存
 * ・従業員別／日付別の希望シフト取得
 *
 * 業務ロジックは ShiftPreferenceService に委譲し、
 * 本クラスは API の入出力制御および DTO からドメインモデルへの変換を担当する。
 *
 * エンドポイント: /api/preferences
 */

@RestController
@RequestMapping("/api/preferences")
public class ShiftPreferenceController {
	
	// シフト希望に関する業務ロジックを扱う Service
    private final ShiftPreferenceService prefService;
    
    // 従業員情報を取得するための Service
    private final EmployeeService employeeService;
    
    // 各 Service を注入するコンストラクタ
    public ShiftPreferenceController(
            ShiftPreferenceService prefService,
            EmployeeService employeeService
    ) {
        this.prefService = prefService;
        this.employeeService = employeeService;
    }

    /**
     * 希望シフトを保存する。
     *
     * ・フロントエンドから送信された DTO を受け取る
     * ・従業員番号を基に従業員情報を取得
     * ・DTO をドメインモデル（ShiftPreference）へ変換して保存する
     *
     * @param dto フロントエンドから送信されたシフト希望情報
     * @return 保存結果メッセージ
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
    
    /**
     * 従業員番号と日付を指定して、1件のシフト希望を取得する。
     *
     * @param employeeNumber 従業員番号
     * @param date           対象日付（yyyy-MM-dd）
     * @return 該当するシフト希望
     */
    @GetMapping("/{employeeNumber}/{date}")
    public ShiftPreference getOne(
            @PathVariable int employeeNumber,
            @PathVariable String date
    ) {
        return prefService.findOne(employeeNumber, LocalDate.parse(date));
    }
    
    /**
     * 指定した従業員の全てのシフト希望を取得する。
     *
     * @param employeeNumber 従業員番号
     * @return シフト希望の一覧
     */
    @GetMapping("/employee/{employeeNumber}")
    public List<ShiftPreference> getByEmployee(@PathVariable int employeeNumber) {
        return prefService.findByEmployeeNumber(employeeNumber);
    }
    
    /**
     * 指定した日付の全従業員のシフト希望を取得する。
     *
     * ・取得した希望シフトに対して、
     *   従業員番号から Employee 情報を紐付け直す
     *
     * @param date 対象日付（yyyy-MM-dd）
     * @return シフト希望の一覧
     */
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

    /**
     * RuntimeException をハンドリングし、
     * クライアントへエラーメッセージを返却する。
     *
     * @param ex 発生した例外
     * @return エラーメッセージ（400 Bad Request）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
