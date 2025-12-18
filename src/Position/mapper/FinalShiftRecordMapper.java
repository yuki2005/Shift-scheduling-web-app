package Position.mapper;

import Position.dto.FinalShiftRecordDto;
import Position.entity.FinalShiftRecordEntity;

public class FinalShiftRecordMapper {

    public static FinalShiftRecordDto toDto(FinalShiftRecordEntity e) {
        FinalShiftRecordDto dto = new FinalShiftRecordDto();
        dto.setId(e.getId());
        dto.setDate(e.getDate().toString());
        dto.setDayOfWeek(e.getDayOfWeek());
        dto.setHoliday(e.isHoliday());
        dto.setMessage(e.getMessage());
        dto.setFinalAssignmentJson(e.getFinalAssignmentJson());
        return dto;
    }
}
