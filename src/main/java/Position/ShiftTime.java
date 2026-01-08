package Position;

/**
 * シフトの時間帯区分を表す ENUM。
 *
 * 従業員の希望シフト入力および
 * 自動シフト生成処理における基本単位として使用される。
 */
public enum ShiftTime {
	TOP, //トップ
	LUNCH, //ランチ
	IDLE, //アイドル
	DINNER, //ディナー
	LAST; //ラスト
}
