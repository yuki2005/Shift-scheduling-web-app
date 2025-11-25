//社員番号
//名前
//従業員の能力値

package Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class Employee {
	//社員番号を記録する変数
	private final int id;
	//従業員の名前を記録する変数
	private final String name;
	//従業員の各能力値を記録するコレクション
	private final Map<Pos, Integer> skills;
	
	//コンストラクタ
	public Employee(int id, String name, Map<Pos, Integer> initialskills){
		this.id = id;
		this.name = name;
		this.skills = (initialskills == null) 
				? new HashMap<>()
				: new HashMap<>(initialskills);
	}
	
	//従業員の能力値を取り出す
	public int getSkill(Pos p) {
		return skills.getOrDefault(p, 0);
	}
	
	public Map<Pos, Integer> getSkills(){
		return Collections.unmodifiableMap(skills);
	}
	
	//従業員の能力値を更新する
	public void setSkills(Pos p, int value) {
		//入力で与えられた数値が0以上100以下である必要がある
		if(value < 0 || value > 100) {
			throw new IllegalArgumentException("能力は0以上100以下の整数で入力して下さい。");
		}
		//コレクションに格納する
		skills.put(p, value);
	}
	
	//社員番号を取り出す
	public int getId() {
		return id;
	}
	
	//名前を取り出す
	public String getName() {
		return name;
	}
}
