// app.js

// dom.js から必要な関数を持つオブジェクトをインポート
import DOMHandler from './dom.js'; 
import API from './api.js';

function init() {
	
    // 1. ボタン要素を取得
    const addButton = document.getElementById('addEmployeeButton');
	const submitButton = document.getElementById('submitButton');
	const saveButton = document.getElementById("saveShiftButton");
	
	//キーボード入力イベントをテーブル全体に設定
	const employeeTable = document.getElementById('employeeTable');
	
	if(employeeTable){
		employeeTable.addEventListener('keyup', (event) => {
		
			if(event.target.tagName === 'INPUT') {
				DOMHandler.setupPreferenceInput();
			}
		});
	}
	
    // 2. イベントリスナーを設定
    if(addButton){
        addButton.addEventListener('click', () => {
			DOMHandler.addEmployeeRow();
			DOMHandler.setupPreferenceInput();
		});
    }
    
    if(submitButton){
        submitButton.addEventListener('click', API.sendShiftRequest);
    }
	
	if (saveButton) {
	    saveButton.addEventListener("click", DOMHandler.saveCurrentShift);
	}

	DOMHandler.setupPreferenceInput();
}

// アプリケーション起動時に init を実行
document.addEventListener('DOMContentLoaded', () -> {
	DOMHandler.loadEmployeeFromDB(); 
	DOMHandler.setupPreferenceInput();
});
