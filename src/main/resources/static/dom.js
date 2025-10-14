function updateSkillDisplay(event){
	const slider = event.currentTarget;
	const valueSpan = slider.nextElementSibling;
	
	valueSpan.textContent = slider.value;
}


function addEmployeeRow(){
    //ロジック本体
    const template = document.getElementById("employeeRowTemplate");
    const tbody = document.getElementById("employeeInputBody");
	
	if(!template || !tbody){
	        console.error("Error: Template or TBody element not found. Cannot add row.");
	        return;
	}
	
	const newRow = template.content.cloneNode(true).querySelector('tr');
	
	if (!newRow) {
	      console.error("Error: Could not find the <tr> element in the template.");
	      return;
	}

	 // 3. 削除ボタンを取得し、イベントリスナーを設定
	    const removeButton = newRow.querySelector('.remove-employee');
		
		if (removeButton) {
		    // 💡 removeEmployeeRow はこのファイル内に定義されているため、そのまま参照
		    removeButton.addEventListener('click', removeEmployeeRow);
		} else {
		    console.warn("Warning: Could not find the remove button in the template."); 
		}
		
		// 複製された行内の全ての range スライダーを取得
		newRow.querySelectorAll('input[type="range"]').forEach(slider => {
		   // 'input' イベント (ドラッグ中も発火) に updateSkillDisplay を設定
		   slider.addEventListener('input', updateSkillDisplay); 
		});
		
	    // 4. 🔴 DOMに一度だけ挿入
		tbody.appendChild(newRow); 

	    console.log("Employee row added successfully");
}

function removeEmployeeRow(event){
    //クリックされたボタンを取得
    const button = event.currentTarget;

    const rowToRemove = button.closest('tr');

    if(rowToRemove){
        rowToRemove.remove();
        console.log("Employee row removed.");
    }
}

function renderAssignmentResult(result){
    //ロジック本体
    console.log("Rendering results:", result);

    const resultSection = document.getElementById("result-section");
    const tbody = document.getElementById("resultTableBody");
    resultSection.style.display = 'block';

    tbody.innerHTML = '';

    for(const [posCode, employees] of Object.entries(result)){
        if(result.hasOwnProperty(posCode)){
            const employees = result[posCode];
            const row = tbody.insertRow();

            const posCell = row.insertCell();
            posCell.textContent = posCode;

            const staffCell = row.insertCell();
            if(employees && employees.length > 0){
                const names = employees.map(e => e.name).join(', ');
                staffCell.textContent = names;
            } else{
                staffCell.textContent = "---担当者なし---";
            }
        }
    }
}

//外部ファイルからアクセスできるようにオブジェクトとしてエクスポート
const DOMHandler = {
    addEmployeeRow: addEmployeeRow,
    removeEmployeeRow: removeEmployeeRow,
    renderResult: renderAssignmentResult,
	collectFormData: collectFormData,
};

function collectFormData(){
    const shiftConditions = collectShiftConditions();

    const employeeCandidates = collectEmployeeData();

    const shiftRequest = {
        dayOfWeekString: shiftConditions.dayOfWeekString,
        isHoliday: shiftConditions.isHoliday,
        employeeCandidates: employeeCandidates
    };

    return shiftRequest;
}

function collectShiftConditions() {
    // name属性が "dayOfWeekString" のラジオボタンから選択された値を取得
    const selectedDay = document.querySelector('select[name = "dayOfWeekString"]');
    
    // name属性が "isHoliday" のラジオボタンから選択された値を取得
    const selectedHoliday = document.querySelector('input[name="isHoliday"]:checked');
	
	const dayValue = selectedDay ? selectedDay.value : null;

    // ラジオボタンが選択されていない場合の処理 (エラーハンドリング)
    if (!selectedDay || dayValue ==="") {
        throw new Error("曜日が選択されていません。");
    }
    if (!selectedHoliday) {
        throw new Error("祝日かどうかが選択されていません。");
    }

    return {
        // value属性 ("MON", "TUE"など) をそのまま使用
        dayOfWeekString: dayValue,
        // value属性 ("true", "false") をブール値に変換
        isHoliday: selectedHoliday.value === 'true' 
    };
}

function collectEmployeeData() {
    const tbody = document.getElementById("employeeInputBody");
    const employeeList = [];

    // tbody の各行 (<tr>) をループ
    // children で子要素 (<tr>) のコレクションを取得
    Array.from(tbody.children).forEach(row => { 
        
        // 1. 各従業員の能力値マップを収集
        const skills = {};
        
        // data-pos 属性を持つすべてのスライダー（能力値入力）を収集
        row.querySelectorAll('input[type="range"]').forEach(slider => {
            const posCode = slider.getAttribute('data-pos'); // 例: "D", "Y"
            skills[posCode] = parseInt(slider.value, 10);      // 値を整数に変換
        });

        // 2. 社員番号と名前の入力値を取得
        const idInput = row.querySelector('input[name="employeeId"]');
        const nameInput = row.querySelector('input[name="employeeName"]');

        // 3. EmployeeDto の構造に合わせてオブジェクトを作成
        employeeList.push({
            // フィールド名が EmployeeDto と一致している必要があります
            id: parseInt(idInput.value, 10),
            name: nameInput.value.trim(),
            skills: skills 
        });
    });

    if (employeeList.length === 0) {
        throw new Error("従業員データが入力されていません。");
    }

    return employeeList;
}

export default DOMHandler;