// app.js

// dom.js から必要な関数を持つオブジェクトをインポート
import DOMHandler from './dom.js'; 
import API from './api.js';

function init() {
	
    // 1. ボタン要素を取得
    const addButton = document.getElementById('addEmployeeButton');
	const submitButton = document.getElementById('submitButton');
	
    // 2. イベントリスナーを設定
    if(addButton){
        addButton.addEventListener('click', DOMHandler.addEmployeeRow);
    }
    
    if(submitButton){
        submitButton.addEventListener('click', API.sendShiftRequest);
    }


}

// アプリケーション起動時に init を実行
document.addEventListener('DOMContentLoaded', init);
