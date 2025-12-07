package Position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Position.entity.FinalShiftRecordEntity;
import Position.entity.FinalShiftRecordAssignmentEntity;
import Position.repository.FinalShiftRecordRepository;
import Position.repository.FinalShiftRecordAssignmentRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FinalShiftRecordService {

    private final FinalShiftRecordRepository recordRepo;
    private final FinalShiftRecordAssignmentRepository assignRepo;
    
    private final ObjectMapper mapper = new ObjectMapper();

    public FinalShiftRecordService(
            FinalShiftRecordRepository recordRepo,
            FinalShiftRecordAssignmentRepository assignRepo
    ) {
        this.recordRepo = recordRepo;
        this.assignRepo = assignRepo;
    }

    /**
     * フロントからの "Assign結果(JSON)" を保存する
     */
    @SuppressWarnings("unchecked")
    public FinalShiftRecordEntity saveShift(Map<String, Object> rawJson, boolean overwrite) {

        // ===== 1) ヘッダ情報 =====
        LocalDate date = LocalDate.parse((String) rawJson.get("date"));
        
        // ===== 上書き判定 =====
        boolean exists = recordRepo.existsByDate(date);

        if (exists && !overwrite) {
            throw new IllegalStateException("既に保存されているため、上書きが必要です。");
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
        
        // ========= 🔥 JSON を丸ごと保存（履歴用） =========
        try {
            record.setFinalAssignmentJson(
                    mapper.writeValueAsString(rawJson.get("finalAssignment"))
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON 保存エラー", e);
        }
        
        record = recordRepo.save(record);

        // ===== 2) 明細（finalAssignment）保存 =====
        Map<String, Object> finalAssignmentObj =
                (Map<String, Object>) rawJson.get("finalAssignment");

        for (Map.Entry<String, Object> entry : finalAssignmentObj.entrySet()) {
            String shiftTime = entry.getKey();
            Map<String, Object> posMap = (Map<String, Object>) entry.getValue();

            for (Map.Entry<String, Object> posEntry : posMap.entrySet()) {
                String posCode = posEntry.getKey();
                List<Object> staffList = (List<Object>) posEntry.getValue();

                for (Object o : staffList) {
                    Map<String, Object> staff = (Map<String, Object>) o;
                    Integer employeeNumber = (Integer) staff.get("id");

                    FinalShiftRecordAssignmentEntity a = new FinalShiftRecordAssignmentEntity();
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
     * 全履歴を日付降順で取得
     */
    public List<FinalShiftRecordEntity> findAll() {
        return recordRepo.findAllByOrderByDateDesc();
    }

    public List<FinalShiftRecordEntity> findByDate(LocalDate date) {
        return recordRepo.findByDate(date);
    }
    
    public boolean exists(LocalDate date) {
        return recordRepo.existsByDate(date);
    }

}
