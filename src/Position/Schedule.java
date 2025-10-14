//曜日
//出勤する従業員のリスト
//祝日かどうか

package Position;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Schedule {
	//各ポジションの必要最低人数を格納するコレクション
	private final Map<Pos, Integer> requiredCounts;
	
	//コンストラクタ
	public Schedule(DayOfWeek day, boolean isNationalHoLiday){
		//用意したコレクションをインスタンス化
		this.requiredCounts = new HashMap<Pos, Integer>();
		
		//入力された日が休日である場合
		if(day.isWeekend() || isNationalHoLiday) {
			//洗い場以外の各ポジション最低1は必要
			for(Pos p : Pos.values()) {
				requiredCounts.put(p, 1);
			}
			//洗い場はいなくてもよい
			requiredCounts.put(Pos.W, 0);
		}
		//平日の場合
		else {
			//焼、揚、板に1人ずついればよい
			for(Pos p : Pos.values()) {
				if(p == Pos.Y || p == Pos.A || p == Pos.I)
					requiredCounts.put(p, 1);
				else
					requiredCounts.put(p, 0);
			}
		}
	}
	
	//平日、休日の場合の各ポジションの最低人数を取得
	public Map<Pos, Integer> getRequiredCounts() {
		return Collections.unmodifiableMap(requiredCounts);
	}
}
