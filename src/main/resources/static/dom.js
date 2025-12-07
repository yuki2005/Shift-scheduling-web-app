// dom.js

import API from './api.js';

// ==============================
// 状態
// ==============================
let latestShiftResponse = null;
let employeeCache = [];
let loadedPreferences = [];   // /api/preferences/date/... の結果

// ==============================
// 定数
// ==============================
const SKILL_ORDER = ["D", "Y", "A", "I", "IF", "AF", "W"];
const API_EMP  = "http://localhost:8080/api/employees";
const API_PREF = "http://localhost:8080/api/preferences";
const API_SHIFT = "http://localhost:8080/api/shift-records";

// ==============================
// 初期化（シフト作成ページ用）
// ==============================
async function initShiftPage() {

    await loadEmployeesFromDB();

    const dateInput = document.getElementById("shiftDateInput");
    if (dateInput) {
        dateInput.addEventListener("change", (e) => {
            const dateStr = e.target.value;
            const code  = getDayOfWeekString(dateStr);
            const label = getDayOfWeekLabel(dateStr);

            const info = document.getElementById("autoDetectedDay");
            info.textContent = dateStr ? `判定された曜日: ${label} (${code})` : "";
        });
    }

    const loadBtn = document.getElementById("loadPrefsForShiftButton");
    if (loadBtn) {
        loadBtn.addEventListener("click", loadPreferencesForShift);
    }
}

// ==============================
// DBから従業員ロード
// ==============================
async function loadEmployeesFromDB() {
    const res = await fetch(API_EMP);
    employeeCache = await res.json();

    const tbody = document.getElementById("employeeInputBody");
    if (!tbody) return;

    employeeCache.forEach(emp => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${emp.id}</td>
            <td>${emp.name}</td>
            ${SKILL_ORDER.map(pos => `
                <td>${emp.skills[pos] ?? 0}</td>
            `).join("")}
        `;

        tbody.appendChild(row);
    });
}

// ==============================
// 希望シフトのロード
// ==============================
async function loadPreferencesForShift() {
	loadedPreferences = [];  
    const date = document.getElementById("shiftDateInput").value;
    const msg   = document.getElementById("loadedPrefMessage");
    const table = document.getElementById("prefViewTable");
    const tbody = document.getElementById("prefViewBody");

    if (!date) {
        msg.style.color = "red";
        msg.textContent = "日付を先に選択してください。";
        return;
    }

    try {
        const res = await fetch(`${API_PREF}/date/${date}`);
		loadedPreferences = (await res.json())
		    .filter(pref => pref.employee && Number.isInteger(pref.employee.id));


        // employee が null のデータを除外（重要）
        loadedPreferences = loadedPreferences.filter(pref =>
            pref.employee && Number.isInteger(pref.employee.id)
			
			
        );
		
		console.log("Loaded pref from server:", loadedPreferences); //デバッグ

		
        tbody.innerHTML = "";
        table.style.display = "table";

        loadedPreferences.forEach(pref => {
            const e = pref.employee;
            const a = pref.availabilityMap || {};

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${e?.name ?? "不明"}</td>
                <td>${a.TOP    ?? 0}</td>
                <td>${a.LUNCH  ?? 0}</td>
                <td>${a.IDLE   ?? 0}</td>
                <td>${a.DINNER ?? 0}</td>
                <td>${a.LAST   ?? 0}</td>
            `;
            tbody.appendChild(tr);
        });

        msg.style.color = "green";
        msg.textContent = "希望を読み込みました。";

        console.log("Loaded pref:", loadedPreferences);

    } catch (e) {
        console.error(e);
        msg.style.color = "red";
        msg.textContent = "読み込みに失敗しました。";
    }
}

// ==============================
// 曜日関連
// ==============================
function getDayOfWeekString(dateStr) {
    if (!dateStr) return "";
    const date = new Date(dateStr + "T00:00:00");
    const days = ["SUN","MON","TUE","WED","THR","FRI","SAT"];
    return days[date.getDay()];
}

function getDayOfWeekLabel(dateStr) {
    if (!dateStr) return "";
    const date = new Date(dateStr + "T00:00:00");
    const labels = ["日曜日","月曜日","火曜日","水曜日","木曜日","金曜日","土曜日"];
    return labels[date.getDay()];
}

// ==============================
// シフト作成フォームデータ収集
// ==============================
function collectFormData() { 
	const dateStr = document.getElementById("shiftDateInput").value; if (!dateStr) throw new Error("シフト日付を入力してください。"); 
	
	const selectedHoliday = document.querySelector('input[name="isHoliday"]:checked');
	 
	if (!selectedHoliday) throw new Error("祝日かどうかが選択されていません。"); 
	
	console.log("loadedPreferences before mapping:", loadedPreferences); // 🔥 employee が null のデータを除外（これが最重要） 
	
	const prefDtos = loadedPreferences .filter(pref => pref.employee && Number.isInteger(pref.employee.id)) .map(pref => ({ 
	
		employeeId: pref.employee.id, date: pref.date, availabilityMap: pref.availabilityMap 
	})); 
	
	console.log("shiftPreferences to send:", prefDtos); 
	return { 
		date: dateStr, 
		dayOfWeekString: getDayOfWeekString(dateStr), 
		isHoliday: selectedHoliday.value === 'true', 
		employeeCandidates: employeeCache, 
		shiftPreferences: prefDtos 
	};
}


// ==============================
// 結果表示
// ==============================
function renderResult(result) {
    latestShiftResponse = result;

    const section = document.getElementById("result-section");
    if (!section) return;
    section.style.display = "block";

    document.getElementById("resultMessage").textContent = result.message || "";

    const tbody = document.getElementById("resultTableBody");
    tbody.innerHTML = "";

    const final = result.finalAssignment || {};

    for (const [time, posMap] of Object.entries(final)) {
        const headerRow = tbody.insertRow();
        const cell = headerRow.insertCell();
        cell.colSpan = 2;
        cell.textContent = `【時間帯】${time}`;
        cell.style.fontWeight = "bold";
        cell.style.backgroundColor = "#e0e0e0";

        for (const [pos, employees] of Object.entries(posMap || {})) {
            const row = tbody.insertRow();
            row.insertCell().textContent = pos;
            row.insertCell().textContent =
                (employees && employees.length > 0)
                    ? employees.map(e => e.name).join(", ")
                    : "---";
        }
    }
}

// ==============================
// シフト保存
// ==============================
async function saveCurrentShift() {
    const msg = document.getElementById("saveResultMessage");

    if (!latestShiftResponse) {
        msg.style.color = "red";
        msg.textContent = "保存するシフト結果がありません。";
        return;
    }

    // 1️⃣ 保存する日付を取得
    const date = document.getElementById("shiftDateInput").value;
    if (!date) {
        msg.style.color = "red";
        msg.textContent = "日付が不明のため保存できません。";
        return;
    }

    // 2️⃣ まず日付が既に存在するかチェック
    const existsRes = await fetch(`http://localhost:8080/api/shift-records/exists/${date}`);
    const existsJson = await existsRes.json();

    let overwrite = false;

    if (existsJson.exists) {
        // 3️⃣ 上書き確認ダイアログ
        const ok = confirm(`${date} のシフトは既に保存されています。\n上書きしてよいですか？`);

        if (!ok) {
            msg.style.color = "red";
            msg.textContent = "保存をキャンセルしました。";
            return;
        }

        overwrite = true;
    }

    // 4️⃣ 保存リクエスト送信
    const body = {
        ...latestShiftResponse,
        overwrite: overwrite
    };

    try {
        const response = await fetch("http://localhost:8080/api/shift-records/save", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });

        if (!response.ok) {
            msg.style.color = "red";
            msg.textContent = "保存に失敗しました（サーバーエラー）。";
            throw new Error(`Save Error: ${response.status}`);
        }

        msg.style.color = "green";
        msg.textContent = "シフトを保存しました！";

        console.log("保存成功:", await response.text());

    } catch (error) {
        console.error("Shift saving failed:", error);
        msg.style.color = "red";
        msg.textContent = "保存に失敗しました（通信エラー）。";
    }
}


// ==============================
// Export
// ==============================
const DOMHandler = {
    initShiftPage,
    collectFormData,
    renderResult,
    saveCurrentShift
};

export default DOMHandler;
