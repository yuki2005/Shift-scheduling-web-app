package Position;

public enum Pos{
	D("デシャップ", 10),
	Y("焼", 6),
	A("揚", 7),
	I("板", 8),
	AF("全体フォロー", 6),
	IF("麺", 8),
	W("洗", 2);
	
	private final String displayName;
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

	public static boolean Poscheck(String p) {
		try {
			Pos.valueOf(p.toUpperCase());
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
}
