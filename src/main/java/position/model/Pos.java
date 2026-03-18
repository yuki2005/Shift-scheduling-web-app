package position.model;

/**
 * シフトにおける担当ポジションを表す ENUM。
 *
 * ・飲食店の厨房業務における役割区分を定義する
 * ・各ポジションは表示名（日本語）と重み（スキル評価用）を持つ
 *
 * 重み（weight）は自動シフト生成時における
 * スキル評価・割り当て優先度の計算に使用される。
 */
public enum Pos{
	D("デシャップ", 10),
	Y("焼", 6),
	A("揚", 7),
	I("板", 8),
	AF("全体フォロー", 6),
	IF("麺", 8),
	W("洗", 2);
	
	// 表示用のポジション名
	private final String displayName;
	
	// ポジションの重み（スキル評価用）
	private final int weight;
	
	Pos(String displayName, int weight){
		this.displayName = displayName;
		this.weight = weight;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getWeight() {
		return weight;
	}
	
	/**
     * 指定された文字列が有効なポジションかを判定する。
     *
     * @param p 判定対象の文字列
     * @return 有効なポジションの場合は true
     */
	public static boolean Poscheck(String p) {
		try {
			Pos.valueOf(p.toUpperCase());
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
}
