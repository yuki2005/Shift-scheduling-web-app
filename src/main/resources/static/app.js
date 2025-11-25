// app.js

import DOMHandler from './dom.js'; 
import API from './api.js';

async function init() {
    // DB から従業員ロード
    await DOMHandler.loadEmployeesFromDB();

    // 希望入力 UI 初期化
    DOMHandler.setupPreferenceInput();

    // ここからイベント設定
    const addButton = document.getElementById('addEmployeeButton');
    const submitButton = document.getElementById('submitButton');
    const saveButton = document.getElementById("saveShiftButton");

    if (addButton) {
        addButton.addEventListener('click', () => {
            DOMHandler.addEmployeeRow();
            DOMHandler.setupPreferenceInput();
        });
    }

    if (submitButton) {
        submitButton.addEventListener('click', API.sendShiftRequest);
    }

    if (saveButton) {
        saveButton.addEventListener("click", DOMHandler.saveCurrentShift);
    }
}

document.addEventListener('DOMContentLoaded', init);
