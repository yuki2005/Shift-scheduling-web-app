package Position;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Schedule {
    // 🔴 修正: フィールド名に ByTime を追加し、多次元構造であることを明確にする
	private final Map<ShiftTime, Map<Pos, Integer>> requiredCountsByTime;
	
	//コンストラクタ
	public Schedule(DayOfWeek day, boolean isNationalHoLiday){
		// 二次元マップをインスタンス化
		this.requiredCountsByTime = new HashMap<>();
		
		// 1. ShiftTime の全時間帯に対してループ
		for (ShiftTime time : ShiftTime.values()) {
		    Map<Pos, Integer> posRequirements = new HashMap<>();
		    
		    // 2. 曜日・休日による基本ルールを設定
		    boolean isBusyDay = day.isWeekend() || isNationalHoLiday;
            
            // 3. Pos (ポジション) ごとに必要人数を設定
		    for(Pos p : Pos.values()) {
                
                int required = 0; // デフォルトは 0
                
                // === アイドル時間帯 (TOP, IDLE, LAST) の共通ルール ===
                if (time == ShiftTime.TOP || time == ShiftTime.IDLE || time == ShiftTime.LAST) {
                    // Y (焼) と I (板) にのみ1人ずつ配置
                    if (p == Pos.Y || p == Pos.I) {
                        required = 1;
                    }
                } 
                // === ピーク時間帯 (LUNCH, DINNER) のルール ===
                else if (time == ShiftTime.LUNCH || time == ShiftTime.DINNER) {
                    
                    if (isBusyDay) {
                        // 🔴 休日ピーク時: 洗い場(W)以外すべて1人
                        if (p != Pos.W) { 
                            required = 1;
                        }
                    } else {
                        // 🔴 平日ピーク時: 焼(Y), 揚(A), 板(I)に1人ずつ
                        if (p == Pos.Y || p == Pos.A || p == Pos.I) {
                            required = 1;
                        }
                    }
                }
		        
                posRequirements.put(p, required); // 設定した人数をマップに格納
		    }
		    
		    // 4. 時間帯ごとに Pos-人数マップを格納
		    this.requiredCountsByTime.put(time, Collections.unmodifiableMap(posRequirements));
		}
	}
	
	/**
	 * 各時間帯の各ポジションの最低人数を取得する (二次元マップを返す)
	 */
	public Map<ShiftTime, Map<Pos, Integer>> getRequiredCountsByTime() {
		return Collections.unmodifiableMap(requiredCountsByTime);
	}
}
