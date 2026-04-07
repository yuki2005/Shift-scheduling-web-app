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
 * HolidayAssignmentStrategy のユニットテスト。
 *
 * テスト観点:
 *  1. 休日LUNCHでW以外の全ポジションに割り当てが行われる
 *  2. 総合能力最大の従業員がDに割り当てられる
 *  3. 同じ従業員が複数ポジションに重複して割り当てられない
 *  4. スキル値0の従業員はそのポジションに割り当てられない
 *  5. W以外に対応ポジションが1つのみの従業員は固定ポジションに割り当てられる
 *  6. 休日アイドル時間帯（IDLE）はY,Iのみに割り当てられる
 *  7. 従業員リストが空の場合は空のMapを返す
 */
class HolidayAssignmentStrategyTest {

    private HolidayAssignmentStrategy strategy;

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
        strategy = new HolidayAssignmentStrategy();
    }

    // ============================================================
    // テストケース
    // ============================================================

    @Test
    @DisplayName("休日LUNCHでW以外の全ポジションに割り当てが行われる")
    void shouldAssignAllPositionsExceptWOnHolidayLunch() {
        // 休日LUNCH: W以外すべて1人ずつ必要
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 8, Pos.Y, 6, Pos.A, 5, Pos.I, 7,
                Pos.IF, 4, Pos.AF, 3, Pos.W, 1
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 5, Pos.Y, 7, Pos.A, 6, Pos.I, 5,
                Pos.IF, 3, Pos.AF, 2, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 3, Pos.Y, 4, Pos.A, 8, Pos.I, 3,
                Pos.IF, 5, Pos.AF, 4, Pos.W, 1
        ));
        Employee emp4 = createEmployee(4, "鈴木", Map.of(
                Pos.D, 2, Pos.Y, 3, Pos.A, 4, Pos.I, 6,
                Pos.IF, 7, Pos.AF, 5, Pos.W, 1
        ));
        Employee emp5 = createEmployee(5, "高橋", Map.of(
                Pos.D, 1, Pos.Y, 2, Pos.A, 3, Pos.I, 4,
                Pos.IF, 6, Pos.AF, 8, Pos.W, 1
        ));
        Employee emp6 = createEmployee(6, "伊藤", Map.of(
                Pos.D, 4, Pos.Y, 5, Pos.A, 2, Pos.I, 2,
                Pos.IF, 2, Pos.AF, 1, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3, emp4, emp5, emp6),
                schedule,
                ShiftTime.LUNCH
        );

        // W以外の全ポジションに1人ずつ割り当てられていること
        assertThat(result.getOrDefault(Pos.D, List.of())).isNotEmpty();
        assertThat(result.getOrDefault(Pos.Y, List.of())).isNotEmpty();
        assertThat(result.getOrDefault(Pos.A, List.of())).isNotEmpty();
        assertThat(result.getOrDefault(Pos.I, List.of())).isNotEmpty();
        assertThat(result.getOrDefault(Pos.IF, List.of())).isNotEmpty();
        assertThat(result.getOrDefault(Pos.AF, List.of())).isNotEmpty();

        // Wは休日でも必要人数0なので空
        assertThat(result.getOrDefault(Pos.W, List.of())).isEmpty();
    }

    @Test
    @DisplayName("総合能力最大の従業員がDポジションに割り当てられる")
    void shouldAssignHighestOverallSkillEmployeeToD() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        // emp1: 総合能力が最大
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 9, Pos.Y, 8, Pos.A, 8, Pos.I, 9,
                Pos.IF, 7, Pos.AF, 7, Pos.W, 5
        ));
        // emp2: 総合能力が低い
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 3, Pos.Y, 3, Pos.A, 3, Pos.I, 3,
                Pos.IF, 2, Pos.AF, 2, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 4, Pos.Y, 5, Pos.A, 4, Pos.I, 4,
                Pos.IF, 3, Pos.AF, 3, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // 総合能力最大の emp1 が D に割り当てられるはず
        List<Employee> dAssigned = result.getOrDefault(Pos.D, List.of());
        assertThat(dAssigned).contains(emp1);
    }

    @Test
    @DisplayName("同じ従業員が複数ポジションに重複して割り当てられない")
    void shouldNotAssignSameEmployeeToMultiplePositions() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 9, Pos.Y, 8, Pos.A, 8, Pos.I, 9,
                Pos.IF, 7, Pos.AF, 7, Pos.W, 5
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 5, Pos.Y, 6, Pos.A, 5, Pos.I, 5,
                Pos.IF, 4, Pos.AF, 4, Pos.W, 2
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 4, Pos.Y, 5, Pos.A, 6, Pos.I, 4,
                Pos.IF, 5, Pos.AF, 3, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // 全ポジションを結合して重複チェック
        List<Employee> allAssigned = result.values().stream()
                .flatMap(List::stream)
                .toList();

        long distinctCount = allAssigned.stream().distinct().count();
        assertThat(distinctCount).isEqualTo(allAssigned.size());
    }

    @Test
    @DisplayName("スキル値0の従業員はそのポジションに割り当てられない")
    void shouldNotAssignEmployeeWithZeroSkillToThatPosition() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        // emp1: Y スキルが 0
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 5, Pos.Y, 0, Pos.A, 4, Pos.I, 5,
                Pos.IF, 3, Pos.AF, 3, Pos.W, 1
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 4, Pos.Y, 7, Pos.A, 5, Pos.I, 4,
                Pos.IF, 3, Pos.AF, 2, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 3, Pos.Y, 5, Pos.A, 6, Pos.I, 3,
                Pos.IF, 4, Pos.AF, 4, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // Y ポジションに emp1（Yスキル0）が含まれていないこと
        List<Employee> yAssigned = result.getOrDefault(Pos.Y, List.of());
        assertThat(yAssigned).doesNotContain(emp1);
    }

    @Test
    @DisplayName("W以外に対応ポジションが1つのみの従業員は固定ポジションに割り当てられる")
    void shouldAssignFixedPositionStaffFirst() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        // emp1: Y スキルのみ（固定ポジション候補）
        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 0, Pos.Y, 8, Pos.A, 0, Pos.I, 0,
                Pos.IF, 0, Pos.AF, 0, Pos.W, 2
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 7, Pos.Y, 5, Pos.A, 6, Pos.I, 7,
                Pos.IF, 4, Pos.AF, 5, Pos.W, 1
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 5, Pos.Y, 4, Pos.A, 7, Pos.I, 5,
                Pos.IF, 6, Pos.AF, 4, Pos.W, 1
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
    @DisplayName("休日アイドル時間帯（IDLE）はY,Iのみに割り当てが行われる")
    void shouldOnlyAssignYandIOnHolidayIdle() {
        // 休日でもIDLE時間帯のルールは平日と同じ（Y,Iのみ）
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        Employee emp1 = createEmployee(1, "田中", Map.of(
                Pos.D, 5, Pos.Y, 8, Pos.A, 4, Pos.I, 3,
                Pos.IF, 2, Pos.AF, 2, Pos.W, 1
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 3, Pos.Y, 4, Pos.A, 5, Pos.I, 7,
                Pos.IF, 3, Pos.AF, 3, Pos.W, 1
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(emp1, emp2),
                schedule,
                ShiftTime.IDLE
        );

        // IDLE時間帯はD,A,IF,AF,Wは必要人数0
        assertThat(result.getOrDefault(Pos.D,  List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.A,  List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.IF, List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.AF, List.of())).isEmpty();
        assertThat(result.getOrDefault(Pos.W,  List.of())).isEmpty();
    }

    @Test
    @DisplayName("WeekdayAssignmentStrategyとの違い：DにはAではなく総合能力最大が割り当てられる")
    void shouldDifferFromWeekdayStrategyByAssigningToDNotA() {
        // HolidayStrategy は D に総合能力最大を割り当てる
        // WeekdayStrategy は A に総合能力最大を割り当てる
        // この差が2つのStrategyの核心的な違いであることを確認する
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        // 総合能力が明確に最大の従業員
        Employee topEmployee = createEmployee(1, "エース", Map.of(
                Pos.D, 10, Pos.Y, 10, Pos.A, 10, Pos.I, 10,
                Pos.IF, 10, Pos.AF, 10, Pos.W, 10
        ));
        Employee emp2 = createEmployee(2, "山田", Map.of(
                Pos.D, 3, Pos.Y, 3, Pos.A, 3, Pos.I, 3,
                Pos.IF, 3, Pos.AF, 3, Pos.W, 3
        ));
        Employee emp3 = createEmployee(3, "佐藤", Map.of(
                Pos.D, 2, Pos.Y, 2, Pos.A, 2, Pos.I, 2,
                Pos.IF, 2, Pos.AF, 2, Pos.W, 2
        ));

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(topEmployee, emp2, emp3),
                schedule,
                ShiftTime.LUNCH
        );

        // HolidayStrategy: 総合能力最大は D に割り当てられる
        assertThat(result.getOrDefault(Pos.D, List.of())).contains(topEmployee);
    }

    @Test
    @DisplayName("従業員リストが空の場合は全ポジションが空リスト")
    void shouldReturnEmptyAssignmentWhenNoStaff() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        Map<Pos, List<Employee>> result = strategy.assign(
                List.of(),
                schedule,
                ShiftTime.LUNCH
        );

        // 空でも例外が発生しないこと、かつ全ポジションが空であること
        result.values().forEach(list -> assertThat(list).isEmpty());
    }
}
