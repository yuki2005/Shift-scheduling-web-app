package Position.mapper;

import Position.entity.FinalShiftRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

public class FinalShiftRecordMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static FinalShiftRecordEntity toEntity(
            Object assignmentMapJsonConvertible,
            LocalDate date,
            String dayOfWeek,
            boolean isHoliday
    ) {
        FinalShiftRecordEntity entity = new FinalShiftRecordEntity();
        entity.setDate(date);
        entity.setDayOfWeek(dayOfWeek);
        entity.setHoliday(isHoliday);

        try {
            String json = mapper.writeValueAsString(assignmentMapJsonConvertible);
            entity.setFinalAssignmentJson(json); //注意
        } catch (Exception e) {
            throw new RuntimeException("シフト結果のJSON変換に失敗しました", e);
        }

        return entity;
    }
}
