//preference_app.js

// API URL
const API_EMP = "http://localhost:8080/api/employees";
const API_PREF = "http://localhost:8080/api/preferences";

// キャッシュ
let employeeCache = [];
let employeePreferences = {};  // key: employeeId → { employeeId, date, availabilityMap }

// ================================
// 初期化
// ================================
document.addEventListener("DOMContentLoaded", async () => {
    await loadEmployees();      
    setupEvents();              
});

// ================================
// 従業員ロード
// ================================
async function loadEmployees() {
    const res = await fetch(API_EMP);
    employeeCache = await res.json();

    const selector = document.getElementById("prefEmployeeSelector");
    if (!selector) {
        console.error("prefEmployeeSelector が見つかりません！");
        return;
    }

    selector.innerHTML = '<option value="">--- 従業員を選択 ---</option>';

    employeeCache.forEach(emp => {
        const op = document.createElement("option");
        op.value = emp.id;
        op.textContent = `${emp.name} (ID: ${emp.id})`;
        selector.appendChild(op);
    });
}

// ================================
// イベント設定
// ================================
function setupEvents() {
    const selector = document.getElementById("prefEmployeeSelector");
    const dateInput = document.getElementById("prefDateInput");
    const saveBtn = document.getElementById("prefSaveButton");

    if (!selector || !dateInput || !saveBtn) {
        console.error("HTML の ID が一致していません！");
        console.log({ selector, dateInput, saveBtn });
        return;
    }

    selector.addEventListener("change", loadPreference);
    dateInput.addEventListener("change", loadPreference);
    saveBtn.addEventListener("click", savePreference);
}

// ================================
// 希望ロード
// ================================
async function loadPreference() {
    const empId = document.getElementById("prefEmployeeSelector").value;
    const date = document.getElementById("prefDateInput").value;
    const message = document.getElementById("prefMessage");

    if (!empId || !date) {
        applyCheckboxes({});
        message.textContent = "";
        return;
    }

    try {
        const res = await fetch(`${API_PREF}/${empId}/${date}`);
        const text = await res.text();

        if (!text) {
            applyCheckboxes({});
            message.style.color = "gray";
            message.textContent = "保存された希望はありません。";
            return;
        }

        const pref = JSON.parse(text);
        const map = pref.availabilityMap || {};

        applyCheckboxes(map);

        employeePreferences[empId] = {
            employee : { id: parseInt(empId) },
            date,
            availabilityMap: map
        };

        message.style.color = "green";
        message.textContent = "保存済みの希望を読み込みました。";

    } catch (e) {
        console.error("読み込みエラー:", e);
        applyCheckboxes({});
        message.style.color = "red";
        message.textContent = "読み込みに失敗しました。";
    }
}

// ================================
// チェックボックスへ反映
// ================================
function applyCheckboxes(map) {
    document.querySelectorAll('input.pref-check').forEach(cb => {
        const code = cb.dataset.shift.toUpperCase();
        cb.checked = map[code] === 1;
    });
}

// ================================
// 希望保存
// ================================
async function savePreference() {
    const empId = document.getElementById("prefEmployeeSelector").value;
    const date = document.getElementById("prefDateInput").value;
    const message = document.getElementById("prefMessage");

    if (!empId) {
        message.style.color = "red";
        message.textContent = "従業員を選択してください。";
        return;
    }

    if (!date) {
        message.style.color = "red";
        message.textContent = "日付を選択してください。";
        return;
    }

    const map = {};
    let hasChecked = false;

    document.querySelectorAll("input.pref-check").forEach(cb => {
        const code = cb.dataset.shift.toUpperCase();
        const v = cb.checked ? 1 : 0;
        map[code] = v;
        if (v === 1) hasChecked = true;
    });

    if (!hasChecked) {
        message.style.color = "red";
        message.textContent = "少なくとも 1 つチェックしてください。";
        return;
    }

    const dto = {
        employeeId: parseInt(empId),
        date,
        availabilityMap: map
    };

    try {
        const res = await fetch(API_PREF, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        if (!res.ok) throw new Error();

        message.style.color = "green";
        message.textContent = "保存しました！";

    } catch (e) {
        console.error("保存エラー:", e);
        message.style.color = "red";
        message.textContent = "保存に失敗しました。";
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    await loadEmployees();      
    setupEvents();              

    // ★ 履歴ボタン押下で別ページへ遷移
    const historyBtn = document.getElementById("loadHistoryBtn");
    if (historyBtn) {
        historyBtn.addEventListener("click", () => {
            window.location.href = "preference_history.html";
        });
    }
});
