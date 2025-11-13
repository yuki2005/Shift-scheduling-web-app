package Position.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import Position.entity.FinalShiftRecordEntity;
import Position.repository.FinalShiftRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class FinalShiftRecordService {

    private final FinalShiftRecordRepository repository;
    private final ObjectMapper objectMapper;

    public FinalShiftRecordService(FinalShiftRecordRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * フロントから送られてきた ShiftResponse 相当の JSON
     * （date, dayOfWeek, isHoliday, message, finalAssignment, workingStaff ...）
     * をそのまま Map で受け取り、必要な情報を DB に保存する。
     */
    public FinalShiftRecordEntity saveShift(Map<String, Object> rawJson) {
        try {
            // --- 基本情報の取り出し ---
            String dateStr = (String) rawJson.get("date");
            String dayOfWeek = (String) rawJson.get("dayOfWeek");
            Object holidayObj = rawJson.get("isHoliday");
            Boolean isHoliday = (holidayObj instanceof Boolean) ? (Boolean) holidayObj : Boolean.FALSE;
            String message = (String) rawJson.getOrDefault("message", "");

            if (dateStr == null || dateStr.isBlank()) {
                dateStr = LocalDate.now().toString();
            }

            // --- エンティティ生成 ---
            FinalShiftRecordEntity entity = new FinalShiftRecordEntity();
            entity.setDate(LocalDate.parse(dateStr));
            entity.setDayOfWeek(dayOfWeek);
            entity.setHoliday(isHoliday);
            entity.setMessage(message);

            // --- ネストされた構造は JSON 文字列に変換して保存 ---
            Object finalAssignmentObj = rawJson.get("finalAssignment");
            Object workingStaffObj = rawJson.get("workingStaff");

            entity.setFinalAssignmentJson(
                    objectMapper.writeValueAsString(finalAssignmentObj)
            );
            entity.setWorkingStaffJson(
                    objectMapper.writeValueAsString(workingStaffObj)
            );

            return repository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("シフト保存中にエラー発生: " + e.getMessage(), e);
        }
    }

    public List<FinalShiftRecordEntity> findAll() {
        return repository.findAll();
    }
}
