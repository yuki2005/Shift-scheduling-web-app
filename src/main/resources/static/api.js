// api.js

//サーバのAPIエンドポイントURLを定義
const API_URL = 'http://localhost:8080/api/shift/assign';

const API_BASE = 'http://localhost:8080/api/shift';

//DOMHandlerをインポート
import DOMHandler from './dom.js';

async function sendShiftRequest(event){
    event.preventDefault();

    // DOMHandlerからフォームデータを取得する関数を呼び出す
    const requestData = DOMHandler.collectFormData();

    //サーバへのリクエストを送信
    try{
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type' : 'application/json',
            },
            body: JSON.stringify(requestData),
        });

        if(!response.ok){
            throw new Error(`HTTP error! status : ${response.status}`);
        }

        const resultJson = await response.json();

        DOMHandler.renderResult(resultJson);
		
    } catch(error){
        console.error("API Request Failed:", error);
    }
}

async function saveShift(responseData) {
    try {
        const response = await fetch(`${API_BASE}/save`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(responseData)
        });

        if (!response.ok) {
            throw new Error(`Save Error: ${response.status}`);
        }

        const result = await response.text();
        return result; // "saved"
        
    } catch (error) {
        console.error("Shift saving failed:", error);
        throw error;
    }
}

const APIHandler = {
    sendShiftRequest: sendShiftRequest,
	saveShift: saveShift,

};

export default APIHandler;