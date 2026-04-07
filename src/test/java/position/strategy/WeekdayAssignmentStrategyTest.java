package position.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import position.model.DayOfWeek;
import position.model.Employee;
import position.model.Pos;
import position.model.Schedule;
import position.model.ShiftTime;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WeekdayAssignmentStrategy のユニットテスト。
 *
 * テスト観点:
 *  1. 平日LUNCH: Y,A,I が必要なポジションに割り当てられる
 *  2. スキルが1つしかない従業員は固定ポジションに割り当てられる
 *  3. 同じ従業員が複数ポジションに重複して割り当てられない
 *  4. スキル値0の従業員はそのポジションに割り当てられない
 *  5. 平日アイドル時間帯（IDLE）: Y と I のみ割り当てられる
 *  6. 従業員が空の場合は空のMapを返す
 */
class WeekdayAssignmentStrategyTest {

    private WeekdayAssignmentStrategy strategy;

    // ============================================================
    // テスト用ヘルパーメソッド
    // ============================================================

    private Employee createEmployee(int id, String name, Map<Pos, Integer> skills) {
        return new Employee(id, name, skills);
    }

    // ============================================================
    // セットアップ
    // ============================================================

    @BeforeEach
    void setUp() {
        strategy = new WeekdayAssignmentStrategy();
    }

    // ============================================================
    // テストケース
    // ============================================================

    @Test
    @DisplayName("平日LUNCHでY,A,Iに必要人数が割り当てられる")
    void shouldAssignRequiredPositionsOnWeekdayLunch() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 8, Pos.I, 2, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 3, Pos.I, 9, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.Y, 4, Pos.I, 4, Pos.D, 3, Pos.A, 7,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // 平日LUNCH: Y,A,I に1人ずつ必要
        assertThat(result.get(Pos.Y)).isNotEmpty();
        assertThat(result.get(Pos.A)).isNotEmpty();
        assertThat(result.get(Pos.I)).isNotEmpty();
    }

    @Test
    @DisplayName("同じ従業員が複数ポジションに重複して割り当てられない")
    void shouldNotAssignSameEmployeeToMultiplePositions() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        // スキルを持つ従業員が少ない状況
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 8, Pos.I, 7, Pos.D, 5, Pos.A, 9,
                Pos.IF, 4, Pos.AF, 3, Pos.W, 2
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 3, Pos.I, 5, Pos.D, 2, Pos.A, 4,
                Pos.IF, 1, Pos.AF, 1, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.Y, 2, Pos.I, 3, Pos.D, 1, Pos.A, 3,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // 全ポジションを結合して重複がないか確認
        List<Employee> allAssigned = result.values().stream()
                .flatMap(List::stream)
                .toList();

        long distinctCount = allAssigned.stream().distinct().count();
        assertThat(distinctCount).isEqualTo(allAssigned.size());
    }

    @Test
    @DisplayName("スキル値が0の従業員はそのポジションに割り当てられない")
    void shouldNotAssignEmployeeWithZeroSkill() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        // emp1: Y スキルが 0
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 0, Pos.I, 8, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 5, Pos.I, 3, Pos.D, 0, Pos.A, 5,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.Y, 6, Pos.I, 4, Pos.D, 0, Pos.A, 7,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // Y ポジションに emp1 が含まれていないことを確認
        List<Employee> yAssigned = result.getOrDefault(Pos.Y, List.of());
        assertThat(yAssigned).doesNotContain(emp1);
    }

    @Test
    @DisplayName("平日IDLEではY,Iのみに割り当てが行われる")
    void shouldOnlyAssignYandIOnWeekdayIdle() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 7, Pos.I, 3, Pos.D, 4, Pos.A, 5,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 2
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 4, Pos.I, 8, Pos.D, 2, Pos.A, 3,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2),
                schedule,
                ShiftTime.IDLE
        );

        // IDLE時間帯はY,Iにのみ1人ずつ必要
        // D,A,IF,AF,W は必要人数0なのでリストが空のはず
        assertThat(result.getOrDefault(Pos.D, List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.A, List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.W, List.of())).isEmpty();
    }

    @Test
    @DisplayName("W以外に対応ポジションが1つのみの従業員は固定ポジションに割り当てられる")
    void shouldAssignFixedPositionStaffFirst() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        // emp1: Y スキルのみ持つ（固定ポジション候補）
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.Y, 8, Pos.I, 0, Pos.D, 0, Pos.A, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 0
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.Y, 5, Pos.I, 7, Pos.D, 4, Pos.A, 6,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.Y, 3, Pos.I, 6, Pos.D, 2, Pos.A, 8,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // emp1 は Y スキルしかないので Y に割り当てられるはず
        List<Employee> yAssigned = result.getOrDefault(Pos.Y, List.of());
        assertThat(yAssigned).contains(emp1);
    }

    @Test
    @DisplayName("従業員リストが空の場合は全ポジションが空リスト")
    void shouldReturnEmptyAssignmentWhenNoStaff() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(),
                schedule,
                ShiftTime.LUNCH
        );

        // 空でも例外が発生しないこと、かつ全ポジションが空であること
        result.values().forEach(list -> assertThat(list).isEmpty());
    }
}
