package Position.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * 従業員のシフト希望をフロントエンドから受け取るための DTO クラス。
 *
 * ・API 経由で送信されるシフト希望入力を表す
 * ・時間帯ごとの勤務可否は Map<String, Integer> として受け取る
 *   （JSONではキーが文字列となるため）
 *
 * Service 層でドメインモデル（ShiftPreference）へ変換されることを前提とする。
 */

public class ShiftPreferenceDto {
	
	// 従業員の社員番号（業務上の一意キー）
	private int employeeId;
	
	// 希望を提出する対象日付
	private LocalDate date;
	
	// シフト時間帯ごとの勤務可否（0: 不可, 1: 可）
	private Map<String, Integer> availabilityMap;
	
	// デフォルトコンストラクタ
	public ShiftPreferenceDto() {
	}
	
	
	// === getter/setter ===
	public int getEmployeeId() { 
		return employeeId; 
		}
	public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
	
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate d) { this.date = d; }
	
	public Map<String, Integer> getAvailabilityMap(){ return availabilityMap; }
	public void setAvailabilityMap(Map<String, Integer> availabilityMap) { this.availabilityMap = availabilityMap; }
}
