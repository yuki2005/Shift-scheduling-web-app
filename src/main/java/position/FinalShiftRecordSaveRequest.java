package position;

import java.util.List;
import java.util.Map;

public class FinalShiftRecordSaveRequest {

    private String date;
    private String dayOfWeek;
    private boolean isHoliday;
    private String message;

    // shiftTime → posCode → staffList
    private Map<String, Map<String, List<Map<String, Object>>>> finalAssignment;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public boolean isHoliday() { return isHoliday; }
    public void setHoliday(boolean holiday) { isHoliday = holiday; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Map<String, List<Map<String, Object>>>> getFinalAssignment() {
        return finalAssignment;
    }

    public void setFinalAssignment(Map<String, Map<String, List<Map<String, Object>>>> finalAssignment) {
        this.finalAssignment = finalAssignment;
    }
}
