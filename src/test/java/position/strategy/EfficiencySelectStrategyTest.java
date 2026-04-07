package position.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import position.model.DayOfWeek;
import position.model.Employee;
import position.model.Pos;
import position.model.Schedule;
import position.model.ShiftPreference;
import position.model.ShiftTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EfficiencySelectStrategy のユニットテスト。
 *
 * テスト観点:
 *  1. 希望を出していない従業員は選定されない
 *  2. 出勤不可（availability=0）の時間帯は選定されない
 *  3. スキル合計値が高い順に選定される
 *  4. 必要人数を超えて選定されない
 *  5. 全員が不可の場合は空リストを返す
 *  6. 希望リストが空の場合は空リストを返す
 */
class EfficiencySelectStrategyTest {

    private EfficiencySelectStrategy strategy;
    private LocalDate testDate;

    // ============================================================
    // テスト用ヘルパーメソッド
    // ============================================================

    /** スキルをすべてゼロで初期化した Employee を生成 */
    private Employee createEmployee(int id, String name) {
        return new Employee(id, name, Map.of(
                Pos.D,  0, Pos.Y,  0, Pos.A,  0, Pos.I,  0,
                Pos.IF, 0, Pos.AF, 0, Pos.W,  0
        ));
    }

    /** 指定スキルのみ設定した Employee を生成 */
    private Employee createEmployee(int id, String name, Map<Pos, Integer> skills) {
        return new Employee(id, name, skills);
    }

    /** 指定した時間帯のみ出勤可能な ShiftPreference を生成 */
    private ShiftPreference createPref(Employee emp, ShiftTime... availableTimes) {
        Map<ShiftTime, Integer> map = new java.util.EnumMap<>(ShiftTime.class);
        for (ShiftTime t : ShiftTime.values()) {
            map.put(t, 0);
        }
        for (ShiftTime t : availableTimes) {
            map.put(t, 1);
        }
        return new ShiftPreference(emp, map, testDate);
    }

    // ============================================================
    // セットアップ
    // ============================================================

    @BeforeEach
    void setUp() {
        strategy = new EfficiencySelectStrategy();
        testDate  = LocalDate.of(2025, 10, 1);
    }

    // ============================================================
    // テストケース
    // ============================================================

    @Test
    @DisplayName("希望を提出していない従業員は選定されない")
    void shouldNotSelectEmployeeWithoutPreference() {
        Employee emp = createEmployee(1, "田中");
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(emp),
                schedule,
                List.of(),          // 希望リストなし
                ShiftTime.LUNCH
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("出勤不可（availability=0）の時間帯は選定されない")
    void shouldNotSelectUnavailableEmployee() {
        Employee emp = createEmployee(1, "田中");
        // LUNCH は不可、DINNER のみ可
        ShiftPreference pref = createPref(emp, ShiftTime.DINNER);

        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(emp),
                schedule,
                List.of(pref),
                ShiftTime.LUNCH     // LUNCH で選定 → 不可なので除外
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("スキル合計が高い従業員から優先して選定される")
    void shouldSelectHigherSkillEmployeeFirst() {
        // emp1: スキル合計 低
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 2, Pos.I, 1, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        // emp2: スキル合計 高
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 8, Pos.I, 7, Pos.D, 5, Pos.A, 6,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));

        ShiftPreference pref1 = createPref(emp1, ShiftTime.LUNCH);
        ShiftPreference pref2 = createPref(emp2, ShiftTime.LUNCH);

        // 平日LUNCH: Y,A,I に1人ずつ → 必要人数3
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(emp1, emp2),
                schedule,
                List.of(pref1, pref2),
                ShiftTime.LUNCH
        );

        // 出勤可能は2人なので2人全員が選ばれる
        assertThat(result).hasSize(2);
        // emp2 がスキル合計が高いので先頭に来る
        assertThat(result.get(0)).isEqualTo(emp2);
        assertThat(result.get(1)).isEqualTo(emp1);
    }

    @Test
    @DisplayName("必要人数を超えて選定されない")
    void shouldNotExceedRequiredCount() {
        // 平日IDLE: Y と I にのみ1人ずつ → 必要合計 2人
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 5, Pos.I, 5, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 4, Pos.I, 4, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.Y, 3, Pos.I, 3, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));

        ShiftPreference pref1 = createPref(emp1, ShiftTime.IDLE);
        ShiftPreference pref2 = createPref(emp2, ShiftTime.IDLE);
        ShiftPreference pref3 = createPref(emp3, ShiftTime.IDLE);

        List<Employee> result = strategy.selectStaff(
                List.of(emp1, emp2, emp3),
                schedule,
                List.of(pref1, pref2, pref3),
                ShiftTime.IDLE
        );

        // 必要人数は2なので3人いても2人だけ選ばれる
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("全員が出勤不可の場合は空リストを返す")
    void shouldReturnEmptyWhenAllUnavailable() {
        Employee emp1 = createEmployee(1, "田中");
        Employee emp2 = createEmployee(2, "山田");

        // 2人とも LUNCH は不可
        ShiftPreference pref1 = createPref(emp1, ShiftTime.TOP);
        ShiftPreference pref2 = createPref(emp2, ShiftTime.DINNER);

        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(emp1, emp2),
                schedule,
                List.of(pref1, pref2),
                ShiftTime.LUNCH
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("休日ピーク時（LUNCH）は出勤可能な人数だけ選定される")
    void shouldSelectAvailableStaffOnHolidayLunch() {
        // 休日LUNCH: W以外すべて1人ずつ → 必要合計6人
        // 出勤可能は2人のため2人だけ選ばれることを確認
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 6, Pos.I, 4, Pos.D, 3, Pos.A, 3,
                Pos.IF, 2, Pos.AF, 2, Pos.W, 1
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 5, Pos.I, 3, Pos.D, 2, Pos.A, 2,
                Pos.IF, 1, Pos.AF, 1, Pos.W, 1
        ));

        ShiftPreference pref1 = createPref(emp1, ShiftTime.LUNCH);
        ShiftPreference pref2 = createPref(emp2, ShiftTime.LUNCH);

        List<Employee> result = strategy.selectStaff(
                List.of(emp1, emp2),
                schedule,
                List.of(pref1, pref2),
                ShiftTime.LUNCH
        );

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("希望リストが空の場合は空リストを返す")
    void shouldReturnEmptyWhenPreferencesEmpty() {
        Employee emp = createEmployee(1, "田中");
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(emp),
                schedule,
                List.of(),
                ShiftTime.LUNCH
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("出勤可能従業員リストが空の場合は空リストを返す")
    void shouldReturnEmptyWhenNoStaff() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        List<Employee> result = strategy.selectStaff(
                List.of(),
                schedule,
                List.of(),
                ShiftTime.LUNCH
        );

        assertThat(result).isEmpty();
    }
}
