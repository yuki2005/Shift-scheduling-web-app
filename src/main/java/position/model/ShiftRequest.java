package position.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import position.dto.EmployeeDto;
import position.dto.ShiftPreferenceDto;

/**
 * シフト自動割当 API(/api/shift/assign) の受け口となる DTO。
 * フロントから送られてきた JSON を Domain Model へ変換する役割。
 */
public class ShiftRequest {

    private String dayOfWeekString;
    private LocalDate date;
    private boolean isHoliday;

    private List<EmployeeDto> employeeCandidates;      // 候補従業員
    private List<ShiftPreferenceDto> shiftPreferences; // 希望シフト

    public ShiftRequest() {}

    // =============================
    // Getter / Setter
    // =============================
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDayOfWeekString() { return dayOfWeekString; }
    public void setDayOfWeekString(String dayOfWeekString) { this.dayOfWeekString = dayOfWeekString; }

    public boolean isHoliday() { return isHoliday; }
    public void setHoliday(boolean holiday) { this.isHoliday = holiday; }

    public List<EmployeeDto> getEmployeeCandidates() { return employeeCandidates; }
    public void setEmployeeCandidates(List<EmployeeDto> employeeCandidates) {
        this.employeeCandidates = employeeCandidates;
    }

    public List<ShiftPreferenceDto> getShiftPreferences() { return shiftPreferences; }
    public void setShiftPreferences(List<ShiftPreferenceDto> shiftPreferences) {
        this.shiftPreferences = shiftPreferences;
    }

    // =============================
    // Domain Model への変換
    // =============================

    /** 
     * EmployeeDto → Domain Employee へ変換 
     */
    public List<Employee> toEmployeeList() {
        if (employeeCandidates == null) return List.of();
        return employeeCandidates.stream()
                .map(EmployeeDto::toEmployee)
                .collect(Collectors.toList());
    }

    /**
     * ShiftPreferenceDto → Domain ShiftPreference へ変換
     * 事前に employees を toEmployeeList() で作成しておくこと
     */
    public List<ShiftPreference> toPreferenceList(List<Employee> employees) {

        if (shiftPreferences == null || employees == null) {
            return List.of();
        }

        // 従業員ID（employeeNumber）をキーに検索しやすくする
        Map<Integer, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity()));

        return shiftPreferences.stream()
                .map(prefDto -> {
                	
                	int id = prefDto.getEmployeeId();
                	
                	if (id <= 0) {
                        System.out.println("[WARN] skip ShiftPreferenceDto with invalid employeeId=" + id);
                        return null;
                    }
                	
                    Employee employee = employeeMap.get(id);
                    if (employee == null) {
                        // 存在しない employeeId の場合スキップ
                        throw new IllegalStateException("Unknown employeeId: " + id);
                    }

                    return ShiftPreference.fromStringMap(
                            employee,
                            prefDto.getAvailabilityMap(),
                            prefDto.getDate()
                    );
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }


    // =============================
    // 補助変換処理
    // =============================

    /** データベース保存時に利用する DayOfWeek Enum */
    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.valueOf(dayOfWeekString.toUpperCase());
    }

    /** AutoShift の内部ロジック用に boolean を返す */
    public boolean getIsHolidayFlag() {
        return isHoliday;
    }
}
