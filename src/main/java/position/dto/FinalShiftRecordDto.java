package position.dto;

/**
 * 確定シフト履歴をフロントエンドへ返却するための DTO クラス。
 *
 * ・Controller から API レスポンスとして使用する
 * ・日付や曜日は表示・通信向けに文字列として保持する
 * ・シフト割り当て情報は JSON 文字列のまま扱い、
 *   フロントエンド側でパース・描画を行う
 */

public class FinalShiftRecordDto {

    private Long id;			// DBの主キー
    private String date;        // yyyy-MM-dd
    private String dayOfWeek;   // TUE / 火曜 など
    private boolean holiday;    // 休日フラグ
    private String message;     // メッセージ

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
