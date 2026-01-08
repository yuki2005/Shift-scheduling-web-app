package Position.mapper;

import Position.dto.FinalShiftRecordDto;
import Position.entity.FinalShiftRecordEntity;

/**
 * 確定シフト履歴に関する Entity → DTO の変換を行う Mapper クラス。
 *
 * ・Entity は DB 永続化用の内部モデル
 * ・DTO は Controller からフロントエンドへ返却するデータ構造
 *
 * 業務ロジックは Service 層に集約し、
 * 本クラスはデータ転送用オブジェクトへの変換のみに責務を限定する。
 */
public class FinalShiftRecordMapper {

    /**
     * FinalShiftRecordEntity を FinalShiftRecordDto に変換する。
     *
     * ・日付はフロントエンドで扱いやすいように String 形式へ変換
     * ・確定シフトの割り当て情報は JSON 文字列としてそのまま DTO に設定する
     *
     * @param e 変換元の確定シフト履歴Entity
     * @return 変換後の確定シフト履歴DTO
     */
    public static FinalShiftRecordDto toDto(FinalShiftRecordEntity e) {
        FinalShiftRecordDto dto = new FinalShiftRecordDto();
        dto.setId(e.getId());
        dto.setDate(e.getDate().toString());
        dto.setDayOfWeek(e.getDayOfWeek());
        dto.setHoliday(e.isHoliday());
        dto.setMessage(e.getMessage());
        dto.setFinalAssignmentJson(e.getFinalAssignmentJson());
        return dto;
    }
}
