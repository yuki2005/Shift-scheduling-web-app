// api.js

const API_ASSIGN = 'http://localhost:8080/api/shift/assign';
const API_SAVE   = 'http://localhost:8080/api/shift-records/save';

import DOMHandler from './dom.js';

// ==============================
// シフト割り当てリクエスト
// ==============================
async function sendShiftRequest() {

    const requestData = DOMHandler.collectFormData();

    try {
        const response = await fetch(API_ASSIGN, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status : ${response.status}`);
        }

        const resultJson = await response.json();
        DOMHandler.renderResult(resultJson);

    } catch (error) {
        console.error("API Request Failed:", error);
    }
}

// ==============================
// シフト保存
// ==============================
async function saveShift(responseData) {
    try {
        const response = await fetch(API_SAVE, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(responseData)
        });

        if (!response.ok) {
            throw new Error(`Save Error: ${response.status}`);
        }

        return await response.text(); // "saved"

    } catch (error) {
        console.error("Shift saving failed:", error);
        throw error;
    }
}

export default {
    sendShiftRequest,
    saveShift
};
