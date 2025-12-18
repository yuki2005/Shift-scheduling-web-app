package Position.dto;

public class FinalShiftRecordDto {

    private Long id;
    private String date;        // yyyy-MM-dd
    private String dayOfWeek;   // TUE / 火曜 など
    private boolean holiday;
    private String message;

    /**
     * shiftTime → posCode → staffList
     * JSON文字列のまま扱う（フロントで parse）
     */
    private String finalAssignmentJson;

    // ===== getter / setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public boolean isHoliday() { return holiday; }
    public void setHoliday(boolean holiday) { this.holiday = holiday; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getFinalAssignmentJson() { return finalAssignmentJson; }
    public void setFinalAssignmentJson(String finalAssignmentJson) {
        this.finalAssignmentJson = finalAssignmentJson;
    }
}
