package position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import position.dto.AssignedStaffDto;
import position.dto.FinalShiftRecordSaveRequest;
import position.entity.FinalShiftRecordAssignmentEntity;
import position.entity.FinalShiftRecordEntity;
import position.repository.FinalShiftRecordAssignmentRepository;
import position.repository.FinalShiftRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 自動生成されたシフト結果（確定シフト）を管理するサービスクラス。
 */
@Service
@Transactional
public class FinalShiftRecordService {

    private final FinalShiftRecordRepository recordRepo;
    private final FinalShiftRecordAssignmentRepository assignRepo;
    private final ObjectMapper mapper = new ObjectMapper();
    
    // コンストラクタ
    public FinalShiftRecordService(
            FinalShiftRecordRepository recordRepo,
            FinalShiftRecordAssignmentRepository assignRepo
    ) {
        this.recordRepo = recordRepo;
        this.assignRepo = assignRepo;
    }

    /**
     * DTOベースで確定シフトを保存する。
     *
     * フロントのJSON形式はそのまま、
     * バックエンド内部のみ型安全に扱う。
     */
    public FinalShiftRecordEntity saveShift(FinalShiftRecordSaveRequest dto, boolean overwrite) {
    	
    	// 保存するシフトの対象となる日付
        LocalDate date = LocalDate.parse(dto.getDate());
        
        // すでに保存されたシフトがある場合
        boolean exists = recordRepo.existsByDate(date);
        if (exists) {
            if (!overwrite) {
                throw new IllegalStateException("既に保存されているため、上書きが必要です。");
            }
            recordRepo.deleteByDate(date);
        }

        FinalShiftRecordEntity record = new FinalShiftRecordEntity();
        record.setDate(date);
        record.setDayOfWeek(dto.getDayOfWeek());
        record.setHoliday(dto.isHoliday());
        record.setMessage(dto.getMessage() != null ? dto.getMessage() : "");

        Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment = dto.getFinalAssignment();
        if (finalAssignment == null) {
            throw new IllegalArgumentException("finalAssignment が不正です");
        }

        try {
            record.setFinalAssignmentJson(
                    mapper.writeValueAsString(finalAssignment)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON 保存エラー", e);
        }

        record = recordRepo.save(record);

        saveAssignmentDetails(record, finalAssignment);

        return record;
    }

    /**
     * 保存済みのシフト情報を更新する。
     */
    public void updateShift(
            Long recordId,
            Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment
    ) {
        FinalShiftRecordEntity record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("record not found"));

        try {
            record.setFinalAssignmentJson(
                    mapper.writeValueAsString(finalAssignment)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON変換失敗", e);
        }

        assignRepo.deleteByRecord(record);
        saveAssignmentDetails(record, finalAssignment);
    }

    /**
     * 明細保存の共通処理。
     */
    private void saveAssignmentDetails(
            FinalShiftRecordEntity record,
            Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment
    ) {
        for (Map.Entry<String, Map<String, List<AssignedStaffDto>>> timeEntry : finalAssignment.entrySet()) {
            String shiftTime = timeEntry.getKey();
            Map<String, List<AssignedStaffDto>> posMap = timeEntry.getValue();

            if (posMap == null) {
                continue;
            }

            for (Map.Entry<String, List<AssignedStaffDto>> posEntry : posMap.entrySet()) {
                String posCode = posEntry.getKey();
                List<AssignedStaffDto> staffList = posEntry.getValue();

                if (staffList == null) {
                    continue;
                }

                for (AssignedStaffDto staff : staffList) {
                    if (staff == null || staff.getId() == null) {
                        continue;
                    }

                    FinalShiftRecordAssignmentEntity a = new FinalShiftRecordAssignmentEntity();
                    a.setRecord(record);
                    a.setShiftTime(shiftTime);
                    a.setPosCode(posCode);
                    a.setEmployeeNumber(staff.getId());

                    assignRepo.save(a);
                }
            }
        }
    }

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