package Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * 従業員を表すドメインモデルクラス。
 *
 * ・社員番号（業務上の一意キー）を識別子として持つ
 * ・従業員名および各ポジションに対する能力値を管理する
 *
 * 本クラスは業務ロジックで使用されるモデルであり、
 * 永続化や通信の責務は持たない。
 */
public class Employee {
	
	// 社員番号（業務上の一意キー）
	private final int id;
	
	// 従業員名
	private final String name;
	
	// 各ポジションに対する能力値（スキル）
	private final Map<Pos, Integer> skills;
	
	//コンストラクタ
	public Employee(int id, String name, Map<Pos, Integer> initialskills){
		this.id = id;
		this.name = name;
		this.skills = (initialskills == null) 
				? new HashMap<>()
				: new HashMap<>(initialskills);
	}
	
	/**
	 * 指定したポジションの能力値を取得する。
	 *
	 * @param p     ポジション
	 * @return skills.getOrDefault(p, 0)   ポジションの能力値
	 */
	public int getSkill(Pos p) {
		return skills.getOrDefault(p, 0);
	}
	
	/**
	 * 全てのポジションの能力値を取得する。
	 *
	 * @return skills   全てのポジションの能力値
	 */
	public Map<Pos, Integer> getSkills(){
		return Collections.unmodifiableMap(skills);
	}
	
	/**
	 * 指定したポジションの能力値を更新する。
	 *
	 * @param p     ポジション
	 * @param value 能力値（0〜100）
	 * @throws IllegalArgumentException 能力値が範囲外の場合
	 */
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
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Employee)) return false;
	    Employee e = (Employee) o;
	    return id == e.id;
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}

}
