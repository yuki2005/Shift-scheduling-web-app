package position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import position.entity.FinalShiftRecordAssignmentEntity;
import position.entity.FinalShiftRecordEntity;
import position.repository.FinalShiftRecordAssignmentRepository;
import position.repository.FinalShiftRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 自動生成されたシフト結果（確定シフト）を管理するサービスクラス。
 * Controller と Repository の間に位置し、
 * シフト履歴の永続化、上書き判定、明細データの再構築といった
 * 業務ロジックを集約する。
 *
 * ・シフト全体は JSON として履歴保存
 * ・同時に、検索・集計用に明細テーブルへ正規化して保存する
 */
@Service
@Transactional
public class FinalShiftRecordService {

    private final FinalShiftRecordRepository recordRepo;
    private final FinalShiftRecordAssignmentRepository assignRepo;
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    //コンストラクタ
    public FinalShiftRecordService(
            FinalShiftRecordRepository recordRepo,
            FinalShiftRecordAssignmentRepository assignRepo
    ) {
        this.recordRepo = recordRepo;
        this.assignRepo = assignRepo;
    }

    /**
     * フロントエンドから送信された確定シフト結果（JSON形式）を保存する。
     *
     * ・同一日付のシフトが既に存在する場合は、overwrite フラグにより
     *   上書き可否を判定する
     * ・シフト全体は JSON として保存し、
     *   各時間帯・ポジション・従業員の割り当ては明細テーブルへ分解して保存する
     *
     * @param rawJson    フロントから送信されたシフト結果(JSON)
     * @param overwrite  既存データを上書きするかどうか
     * @return 保存されたシフトレコード
     */
    @SuppressWarnings("unchecked")
    public FinalShiftRecordEntity saveShift(Map<String, Object> rawJson, boolean overwrite) {

        // ===== 1) ヘッダ情報 =====
        LocalDate date = LocalDate.parse((String) rawJson.get("date"));
        
        // ===== 上書き判定 =====
        boolean exists = recordRepo.existsByDate(date);

        if (exists) {
            if (!overwrite) {
                throw new IllegalStateException("既に保存されているため、上書きが必要です。");
            }
            recordRepo.deleteByDate(date);
        }
        
        String dayOfWeek =
                rawJson.containsKey("dayOfWeek") ? 
                    (String) rawJson.get("dayOfWeek") :
                    (String) rawJson.get("dayOfWeekString");
        Boolean holidayObj = (Boolean) rawJson.get("isHoliday");
        boolean isHoliday = holidayObj != null ? holidayObj : false;

        String message = (String) rawJson.getOrDefault("message", "");

        FinalShiftRecordEntity record = new FinalShiftRecordEntity();
        record.setDate(date);
        record.setDayOfWeek(dayOfWeek);
        record.setHoliday(isHoliday);
        record.setMessage(message);
        
     // finalAssignment を Map として取得（明細用）
        Object finalAssignmentRaw = rawJson.get("finalAssignment");

        if (!(finalAssignmentRaw instanceof Map)) {
            throw new IllegalArgumentException("finalAssignment が不正です");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> finalAssignmentObj =
                (Map<String, Object>) finalAssignmentRaw;

        // JSON保存（履歴用）
        try {
            record.setFinalAssignmentJson(
                mapper.writeValueAsString(finalAssignmentObj)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON 保存エラー", e);
        }
        
        record = recordRepo.save(record);
        
        // シフト明細の保存
        for (Map.Entry<String, Object> entry : finalAssignmentObj.entrySet()) {
            String shiftTime = entry.getKey();
            Map<String, Object> posMap = (Map<String, Object>) entry.getValue();

            for (Map.Entry<String, Object> posEntry : posMap.entrySet()) {
                String posCode = posEntry.getKey();
                List<Object> staffList = (List<Object>) posEntry.getValue();

                for (Object o : staffList) {
                    Map<String, Object> staff = (Map<String, Object>) o;
                    Integer employeeNumber = (Integer) staff.get("id");

                    FinalShiftRecordAssignmentEntity a =
                        new FinalShiftRecordAssignmentEntity();
                    a.setRecord(record);
                    a.setShiftTime(shiftTime);
                    a.setPosCode(posCode);
                    a.setEmployeeNumber(employeeNumber);

                    assignRepo.save(a);
                }
            }
        }

        return record;
    }
    
    /**
     * 保存済みのシフト情報を更新する。
     *
     * ・指定されたレコードIDを基に対象シフトを取得
     * ・JSON形式のシフト履歴を更新
     * ・既存の明細データを一旦削除し、最新の割り当て内容で再作成する
     *
     * @param recordId        更新対象のシフトレコードID
     * @param finalAssignment 更新後のシフト割り当て情報
     */
    @SuppressWarnings("unchecked")
    public void updateShift(Long recordId, Map<String, Object> finalAssignment) {

        FinalShiftRecordEntity record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("record not found"));

        // ① JSON を保存（履歴用）
        try {
            record.setFinalAssignmentJson(
                    mapper.writeValueAsString(finalAssignment)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON変換失敗", e);
        }

        // ② 既存明細を削除
        assignRepo.deleteByRecord(record);

        // ③ 明細を再作成
        for (Map.Entry<String, Object> timeEntry : finalAssignment.entrySet()) {
            String shiftTime = timeEntry.getKey();
            Map<String, Object> posMap =
                    (Map<String, Object>) timeEntry.getValue();

            for (Map.Entry<String, Object> posEntry : posMap.entrySet()) {
                String posCode = posEntry.getKey();
                List<Map<String, Object>> staffList =
                        (List<Map<String, Object>>) posEntry.getValue();

                for (Map<String, Object> staff : staffList) {
                    Integer empId = (Integer) staff.get("id");

                    FinalShiftRecordAssignmentEntity a =
                            new FinalShiftRecordAssignmentEntity();
                    a.setRecord(record);
                    a.setShiftTime(shiftTime);
                    a.setPosCode(posCode);
                    a.setEmployeeNumber(empId);

                    assignRepo.save(a);
                }
            }
        }
    }

    /**
     * 保存されている全てのシフト履歴を日付の降順で取得する。
     *
     * @return シフト履歴一覧
     */
    public List<FinalShiftRecordEntity> findAll() {
        return recordRepo.findAllByOrderByDateDesc();
    }
    
    /**
     * 指定した日付のシフト履歴を取得する。
     *
     * @param date 検索対象の日付
     * @return 該当するシフト履歴
     */
    public List<FinalShiftRecordEntity> findByDate(LocalDate date) {
        return recordRepo.findByDate(date);
    }
    
    /**
     * 指定した日付のシフトが既に保存されているかを判定する。
     *
     * @param date 判定対象の日付
     * @return 存在する場合は true
     */
    public boolean exists(LocalDate date) {
        return recordRepo.existsByDate(date);
    }

}
