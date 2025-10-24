// dom.js

//希望シフトデータを保持するマップ
//キー: 社員番号, 値: ShiftPreferenceのオブジェクト構造
const employeePreferences = {};

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

function renderAssignmentResult(result) {
    console.log("Rendering results:", result);

    const resultSection = document.getElementById("result-section");
    const tbody = document.getElementById("resultTableBody");
    const messageContainer = document.getElementById("resultMessage");
    const selectedStaffListDiv = document.getElementById("selectedStaffList");

    resultSection.style.display = 'block';
    tbody.innerHTML = '';

    // --- メッセージ表示 ---
    messageContainer.textContent = result.message || "（メッセージなし）";

    // --- 出勤従業員一覧 ---
    const workingStaff = result.workingStaff || {};
    const allNames = Object.entries(workingStaff)
        .flatMap(([time, list]) => list.map(e => `${e.name} (${time})`));
    selectedStaffListDiv.textContent =
        allNames.length > 0 ? `選定されたスタッフ: ${allNames.join(", ")}` : "選定されたスタッフはいません。";

    // --- 割り当て結果 ---
    const finalAssignment = result.finalAssignment || {};
    if (Object.keys(finalAssignment).length === 0) {
        tbody.innerHTML = '<tr><td colspan="2">割り当て結果が見つかりませんでした。</td></tr>';
        return;
    }

    for (const [timeCode, posMap] of Object.entries(finalAssignment)) {
        // 時間帯ヘッダー行
        const timeHeaderRow = tbody.insertRow();
        const headerCell = timeHeaderRow.insertCell();
        headerCell.colSpan = 2;
        headerCell.textContent = `【時間帯】${timeCode}`;
        headerCell.style.textAlign = 'center';
        headerCell.style.backgroundColor = '#e0e0e0';
        headerCell.style.fontWeight = 'bold';

        // 各ポジションの割り当て
        for (const [posCode, employees] of Object.entries(posMap || {})) {
            const row = tbody.insertRow();

            const posCell = row.insertCell();
            posCell.textContent = posCode;

            const staffCell = row.insertCell();
            if (employees && employees.length > 0) {
                staffCell.textContent = employees.map(e => e.name).join(", ");
            } else {
                staffCell.textContent = "---担当者なし---";
                staffCell.style.color = "gray";
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
	setupPreferenceInput: setupPreferenceInput,
};

function setupPreferenceInput() {
    const selector = document.getElementById('employeePreferenceSelector');
    const saveButton = document.getElementById('savePreferenceButton');
    const employeeRows = document.getElementById('employeeInputBody');
    
    if (!selector || !saveButton || !employeeRows) {
        console.warn("Preference input elements not found.");
        return;
    }
    
    // 1. 既存の従業員データを取得し、プルダウンにロード
    const currentEmployees = collectEmployeeData();
    selector.innerHTML = '<option value="" disabled selected>--- 従業員を選択 ---</option>';
    
    currentEmployees.forEach(emp => {
        const option = document.createElement('option');
        option.value = emp.id;
        option.textContent = `${emp.name} (ID: ${emp.id})`;
        selector.appendChild(option);
    });
    
    // 2. 希望シフト保存ボタンにイベントを設定
    saveButton.addEventListener('click', savePreferenceData);

    // 3. 従業員選択が変更されたときのイベント (希望シフト表示の反映)
    selector.addEventListener('change', loadExistingPreference);
}

// 保存ボタンが押されたときの処理
function savePreferenceData() {
    const selector = document.getElementById('employeePreferenceSelector');
    const preferenceGroup = document.querySelector('.shift-time-group');
    const messageDiv = document.getElementById('preferenceMessage');

    const employeeId = selector.value;
    if (!employeeId) {
        messageDiv.textContent = "従業員を選択してください。";
        return;
    }

    const availabilityMap = {};
    let isChecked = false;
    
    // チェックボックスの状態を収集 (1: 可, 0: 不可)
    preferenceGroup.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        const timeCode = checkbox.getAttribute('data-shift').toUpperCase();
        const isAvailable = checkbox.checked ? 1 : 0;
        availabilityMap[timeCode] = isAvailable;
        if (isAvailable === 1) isChecked = true;
    });

    if (!isChecked) {
         messageDiv.textContent = "警告: 少なくとも一つの時間帯を選択してください。";
         return;
    }
    
    // グローバルマップに保存
    employeePreferences[employeeId] = {
        employeeId: parseInt(employeeId, 10), // IDは整数として保存
        availabilityMap: availabilityMap
    };

    messageDiv.style.color = 'green';
    messageDiv.textContent = `${selector.options[selector.selectedIndex].textContent} の希望シフトを保存しました。`;
}

// 既存の希望シフトをチェックボックスに反映させるロジック
function loadExistingPreference() {
    const selector = document.getElementById('employeePreferenceSelector');
    const employeeId = selector.value;
    const currentPref = employeePreferences[employeeId];
    const preferenceGroup = document.querySelector('.shift-time-group');

    // 既存の希望があれば、チェックボックスを反映
    preferenceGroup.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        const timeCode = checkbox.getAttribute('data-shift');
        if (currentPref && currentPref.availabilityMap[timeCode] === 1) {
            checkbox.checked = true;
        } else {
            checkbox.checked = false;
        }
    });
}

function collectFormData(){
    const shiftConditions = collectShiftConditions();

    const employeeCandidates = collectEmployeeData();
	
    // 🔴 修正: グローバルマップ employeePreferences の値をリストとして返す
    const shiftPreferences = Object.values(employeePreferences);

    const shiftRequest = {
        dayOfWeekString: shiftConditions.dayOfWeekString,
        isHoliday: shiftConditions.isHoliday,
        employeeCandidates: employeeCandidates,
		shiftPreferences: shiftPreferences
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
            skills[posCode.toUpperCase()] = parseInt(slider.value, 10);      // 値を整数に変換
        });

        // 2. 社員番号と名前の入力値を取得
        const idInput = row.querySelector('input[name="employeeId"]');
        const nameInput = row.querySelector('input[name="employeeName"]');
		
		const rawId = idInput.value.trim();
		
		if(rawId === "" || isNaN(parseInt(rawId, 10))) {
			//IDが必須なので、この行のデータは無視
			console.warn("Warning: Skipped row due to empty or invalid Employee ID.");
			return; //この行の処理をスキップ
		}

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

function collectPreferences() {
    const preferencesList = [];
    
    // 💡 現状のフォームでは、保存されたデータがないため、
    // 従業員データ入力フォームのデータを使って、全従業員が出勤可能というダミーの希望を生成します。
    // (データベース接続後、このロジックは、保存された希望データを読み込むロジックに置き換えられます)

    const employeeData = collectEmployeeData(); // 従業員IDを取得するために既存関数を流用

    // ShiftTime のコードを取得 (HTMLの data-shift 属性に対応)
    const shiftTimes = ["TOP", "LUNCH", "IDLE", "DINNER", "LAST"];

    if (employeeData.length === 0) {
        return [];
    }

    // 各従業員に対して、ShiftPreferenceDto を生成
    employeeData.forEach(employee => {
        const availabilityMap = {};

        // 全て '1' (出勤可) として初期設定
        shiftTimes.forEach(time => {
            availabilityMap[time] = 1; 
        });

        // ShiftPreferenceDto の構造に合わせてオブジェクトを作成
        preferencesList.push({
            // EmployeeDtoからIDを流用
            employeeId: employee.id, 
            availabilityMap: availabilityMap
        });
    });

    return preferencesList;
}

export default DOMHandler;
