package position.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Schedule のユニットテスト。
 *
 * テスト観点:
 *  1. 平日アイドル時間帯（TOP/IDLE/LAST）は Y,I のみ1人
 *  2. 平日ピーク時間帯（LUNCH/DINNER）は Y,A,I のみ1人
 *  3. 休日ピーク時間帯（LUNCH/DINNER）は W以外すべて1人
 *  4. 休日アイドル時間帯は平日と同じルール（Y,I のみ1人）
 *  5. 国民の祝日フラグが正しく保持される
 *  6. 返却されるMapはイミュータブルである
 *  7. 全時間帯・全ポジションに対してエントリが存在する
 */
class ScheduleTest {

    @Test
    @DisplayName("平日TOP/IDLE/LAST: Y と I のみ必要人数1、それ以外は0")
    void weekdayIdleTimesShouldRequireOnlyYandI() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        for (ShiftTime time : new ShiftTime[]{ShiftTime.TOP, ShiftTime.IDLE, ShiftTime.LAST}) {
            Map<Pos, Integer> req = schedule.getRequiredCountsByTime().get(time);

            assertThat(req.get(Pos.Y)).as("%s: Y", time).isEqualTo(1);
            assertThat(req.get(Pos.I)).as("%s: I", time).isEqualTo(1);

            assertThat(req.get(Pos.D)).as("%s: D", time).isEqualTo(0);
            assertThat(req.get(Pos.A)).as("%s: A", time).isEqualTo(0);
            assertThat(req.get(Pos.IF)).as("%s: IF", time).isEqualTo(0);
            assertThat(req.get(Pos.AF)).as("%s: AF", time).isEqualTo(0);
            assertThat(req.get(Pos.W)).as("%s: W", time).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("平日LUNCH/DINNER: Y,A,I のみ必要人数1、それ以外は0")
    void weekdayPeakTimesShouldRequireYandAandI() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);

        for (ShiftTime time : new ShiftTime[]{ShiftTime.LUNCH, ShiftTime.DINNER}) {
            Map<Pos, Integer> req = schedule.getRequiredCountsByTime().get(time);

            assertThat(req.get(Pos.Y)).as("%s: Y", time).isEqualTo(1);
            assertThat(req.get(Pos.A)).as("%s: A", time).isEqualTo(1);
            assertThat(req.get(Pos.I)).as("%s: I", time).isEqualTo(1);

            assertThat(req.get(Pos.D)).as("%s: D", time).isEqualTo(0);
            assertThat(req.get(Pos.IF)).as("%s: IF", time).isEqualTo(0);
            assertThat(req.get(Pos.AF)).as("%s: AF", time).isEqualTo(0);
            assertThat(req.get(Pos.W)).as("%s: W", time).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("休日LUNCH/DINNER: W以外すべて必要人数1、W は0")
    void holidayPeakTimesShouldRequireAllExceptW() {
        Schedule schedule = new Schedule(DayOfWeek.SAT, true);

        for (ShiftTime time : new ShiftTime[]{ShiftTime.LUNCH, ShiftTime.DINNER}) {
            Map<Pos, Integer> req = schedule.getRequiredCountsByTime().get(time);

            assertThat(req.get(Pos.D)).as("%s: D", time).isEqualTo(1);
            assertThat(req.get(Pos.Y)).as("%s: Y", time).isEqualTo(1);
            assertThat(req.get(Pos.A)).as("%s: A", time).isEqualTo(1);
            assertThat(req.get(Pos.I)).as("%s: I", time).isEqualTo(1);
            assertThat(req.get(Pos.IF)).as("%s: IF", time).isEqualTo(1);
            assertThat(req.get(Pos.AF)).as("%s: AF", time).isEqualTo(1);

            assertThat(req.get(Pos.W)).as("%s: W は休日ピークでも0", time).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("休日アイドル時間帯（TOP/IDLE/LAST）は平日と同じくY,Iのみ1人")
    void holidayIdleTimesShouldMatchWeekdayRule() {
        Schedule holidaySchedule = new Schedule(DayOfWeek.SAT, true);
        Schedule weekdaySchedule = new Schedule(DayOfWeek.WED, false);

        for (ShiftTime time : new ShiftTime[]{ShiftTime.TOP, ShiftTime.IDLE, ShiftTime.LAST}) {
            Map<Pos, Integer> holiday = holidaySchedule.getRequiredCountsByTime().get(time);
            Map<Pos, Integer> weekday = weekdaySchedule.getRequiredCountsByTime().get(time);

            assertThat(holiday)
                    .as("アイドル時間帯は休日でも平日と同じルールになるはず: %s", time)
                    .isEqualTo(weekday);
        }
    }

    @Test
    @DisplayName("isHoliday フラグが正しく保持される")
    void isHolidayFlagShouldBeRetainedCorrectly() {
        Schedule holiday = new Schedule(DayOfWeek.WED, true);
        Schedule weekday = new Schedule(DayOfWeek.WED, false);

        assertThat(holiday.isHoliday()).isTrue();
        assertThat(weekday.isHoliday()).isFalse();
    }

    @Test
    @DisplayName("土日（weekend）は祝日フラグなしでも休日ピークルールが適用される")
    void weekendWithoutHolidayFlagShouldApplyBusyDayRule() {
        // SAT は isWeekend() = true なので、祝日フラグがなくても休日ルール適用
        Schedule saturday = new Schedule(DayOfWeek.SAT, false);

        Map<Pos, Integer> lunchReq = saturday.getRequiredCountsByTime().get(ShiftTime.LUNCH);

        assertThat(lunchReq.get(Pos.D)).isEqualTo(1);
        assertThat(lunchReq.get(Pos.Y)).isEqualTo(1);
        assertThat(lunchReq.get(Pos.A)).isEqualTo(1);
        assertThat(lunchReq.get(Pos.I)).isEqualTo(1);
        assertThat(lunchReq.get(Pos.W)).isEqualTo(0);
    }

    @Test
    @DisplayName("getRequiredCountsByTime の返却値はイミュータブルである")
    void requiredCountsMapShouldBeImmutable() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);
        Map<ShiftTime, Map<Pos, Integer>> result = schedule.getRequiredCountsByTime();

        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> result.put(ShiftTime.LUNCH, Map.of())
        );
    }

    @Test
    @DisplayName("全時間帯・全ポジションに対して必要人数マップが存在する")
    void allShiftTimesAndPositionsShouldHaveRequirementsDefined() {
        Schedule schedule = new Schedule(DayOfWeek.WED, false);
        Map<ShiftTime, Map<Pos, Integer>> all = schedule.getRequiredCountsByTime();

        for (ShiftTime time : ShiftTime.values()) {
            assertThat(all).containsKey(time);
            assertThat(all.get(time)).isNotNull();
            for (Pos p : Pos.values()) {
                assertThat(all.get(time)).containsKey(p);
            }
        }
    }
}
