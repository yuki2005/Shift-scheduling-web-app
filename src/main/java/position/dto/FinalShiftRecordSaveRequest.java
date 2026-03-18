package position.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 確定シフト保存リクエストDTO。
 *
 * フロントの既存JSON形式との互換性を維持する。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalShiftRecordSaveRequest {

    private String date;

    @JsonAlias({"dayOfWeek", "dayOfWeekString"})
    private String dayOfWeek;

    private boolean isHoliday;
    private String message;
    private boolean overwrite;

    /**
     * shiftTime -> posCode -> staffList
     */
    private Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public Map<String, Map<String, List<AssignedStaffDto>>> getFinalAssignment() {
        return finalAssignment;
    }

    public void setFinalAssignment(Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment) {
        this.finalAssignment = finalAssignment;
    }
}