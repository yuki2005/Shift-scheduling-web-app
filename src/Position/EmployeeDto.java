//社員番号
//名前
//従業員の能力値

package Position;

import java.util.Map;

public class EmployeeDto {
	//社員番号を記録する変数
	private int id;
	//従業員の名前を記録する変数
	private String name;
	//従業員の各能力値を記録するコレクション
	private Map<Pos, Integer> skills;
	
	//コンストラクタ
	public EmployeeDto(){
		
	}
	
	
	//社員番号を取り出す
	public int getId() {return id;}
	//社員番号のセット
	public void setId(int id) {this.id = id;}
	
	//名前を取り出す
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public Map<Pos, Integer> getSkills(){return skills;}
	public void setSkills(Map<Pos, Integer> skills) {this.skills = skills;}
	
	// 3. DTOからビジネスモデルへの変換メソッド
	/**
	 * DTOをコアビジネスオブジェクトである　Employeeに変換する
	 */
	public Employee toEmployee() {
		return new Employee(this.id, this.name, this.skills);
	}
}
