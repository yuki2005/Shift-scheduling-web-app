import DOMHandler from './dom.js';
import API from './api.js';

document.addEventListener('DOMContentLoaded', () => {

    // ★ シフトページの初期化
    DOMHandler.initShiftPage();

    // 希望読込
    const loadPrefBtn = document.getElementById("loadPrefsForShiftButton");
    if (loadPrefBtn) {
        loadPrefBtn.addEventListener("click", DOMHandler.loadPreferencesForShift);
    }

    // シフト割り当て実行
    const submitButton = document.getElementById('submitButton');
    if (submitButton) {
        submitButton.addEventListener('click', API.sendShiftRequest);
    }

    // シフト保存
    const saveButton = document.getElementById("saveShiftButton");
    if (saveButton) {
        saveButton.addEventListener("click", DOMHandler.saveCurrentShift);
    }
});
