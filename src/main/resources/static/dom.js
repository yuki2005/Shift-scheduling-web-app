// dom.js

import API from './api.js';

// ==============================
// 定数 & 状態
// ==============================

// フロント側で持つ希望シフト（サーバ送信用）
const employeePreferences = {}; // key: employeeId, value: { employeeId, date, availabilityMap }
let latestShiftResponse = null;

// サーバから読んだ従業員のキャッシュ
let employeeCache = [];

// API エンドポイント
const API_EMP = "http://localhost:8080/api/employees";
const API_PREF = "http://localhost:8080/api/preferences";

// ==============================
// 初期化
// ==============================

document.addEventListener("DOMContentLoaded", async () => {
    await loadEmployeesFromDB();     // 従業員一覧をDBから読み込み
    setupPreferenceInput();         // 希望入力セクション用の初期化
});

// ==============================
// 従業員ロード関連
// ==============================

const SKILL_ORDER = ["D", "Y", "A", "I", "IF", "AF", "W"];

async function loadEmployeesFromDB() {
    const res = await fetch("http://localhost:8080/api/employees");
    const employees = await res.json();

    const tbody = document.getElementById("employeeInputBody");
    tbody.innerHTML = "";

    employees.forEach(emp => {
        const { id, name, skills } = emp;

        const row = document.createElement("tr");

        row.innerHTML = `
			<!-- チェックボックス列 -->
		            <td>
		                <input type="checkbox" class="emp-check" data-id="${id}">
		            </td>
            <td><input type="text" name="employeeId" value="${id}" readonly></td>
            <td><input type="text" name="employeeName" value="${name}" readonly></td>
            ${
                SKILL_ORDER.map(pos => `
                    <td>
                        <span>${skills[pos] ?? 0}</span>
                    </td>
                `).join("")
            }
            
        `;

        tbody.appendChild(row);
    });
}


// スキルセルをまとめて作るヘルパ
function buildSkillCells(skills) {
    // DB上は { "D": 5, "Y": 3, ... } のような構造を想定
    const order = ["D","Y","A","I","IF","AF","W"];

    return order.map(pos => {
        const v = skills && skills[pos] != null ? skills[pos] : 0;
        return `
            <td>
              <input type="range" min="0" max="10" value="${v}" data-pos="${pos}" disabled>
              <span>${v}</span>
            </td>
        `;
    }).join("");
}

// ==============================
// 希望シフト入力セクション
// ==============================

function setupPreferenceInput() {
    const selector = document.getElementById('employeePreferenceSelector');
    const saveButton = document.getElementById('savePreferenceButton');
    const dateInput = document.getElementById("shiftDateInput");

    if (!selector || !saveButton || !dateInput) {
        console.warn("preference input elements not found.");
        return;
    }

    // プルダウン初期化
    selector.innerHTML = '<option value="" disabled selected>--- 従業員を選択 ---</option>';

    employeeCache.forEach(emp => {
        const option = document.createElement('option');
        option.value = emp.id; // employeeNumber
        option.textContent = `${emp.name} (ID: ${emp.id})`;
        selector.appendChild(option);
    });

    // 変更イベント：
    //   - 従業員変更
    //   - 日付変更
    // のどちらかで「その従業員・その日」の希望をDBからロード
    selector.addEventListener('change', handlePreferenceTargetChange);
    dateInput.addEventListener('change', handlePreferenceTargetChange);

    // 希望保存ボタン
    saveButton.addEventListener('click', savePreferenceData);
}

// 従業員 or 日付が変わったときに呼ばれる
async function handlePreferenceTargetChange() {
    const selector = document.getElementById('employeePreferenceSelector');
    const dateInput = document.getElementById("shiftDateInput");

    const employeeId = selector.value;
    const date = dateInput.value;

    // どちらか欠けていたらチェックを全部リセット
    if (!employeeId || !date) {
        applyAvailabilityToCheckboxes({});
        return;
    }

    await loadPreferenceFromDB(parseInt(employeeId, 10), date);
}

// DBから該当従業員・日付の希望をロードしてチェックボックスに反映
async function loadPreferenceFromDB(employeeId, dateString) {
    const messageDiv = document.getElementById('preferenceMessage');
    const url = `${API_PREF}/${employeeId}/${dateString}`;

    try {
        const res = await fetch(url);

        if (!res.ok) {
            // 404など → 希望なしとして扱う
            applyAvailabilityToCheckboxes({});
            employeePreferences[employeeId] = {
                employeeId,
                date: dateString,
                availabilityMap: {}
            };
            messageDiv.style.color = 'gray';
            messageDiv.textContent = "保存された希望はありません。新規に入力できます。";
            return;
        }

        // 空レスポンス対策として、一度テキストで受ける
        const text = await res.text();
        if (!text) {
            applyAvailabilityToCheckboxes({});
            employeePreferences[employeeId] = {
                employeeId,
                date: dateString,
                availabilityMap: {}
            };
            messageDiv.style.color = 'gray';
            messageDiv.textContent = "保存された希望はありません。新規に入力できます。";
            return;
        }

        const pref = JSON.parse(text); // ShiftPreference
        const availability = pref.availabilityMap || {};

        applyAvailabilityToCheckboxes(availability);

        // フロント側のキャッシュも更新
        employeePreferences[employeeId] = {
            employeeId,
            date: dateString,
            availabilityMap: availability
        };

        messageDiv.style.color = 'green';
        messageDiv.textContent = "保存済みの希望を読み込みました。";

    } catch (e) {
        console.error("希望シフトのロード中にエラー:", e);
        applyAvailabilityToCheckboxes({});
        messageDiv.style.color = 'red';
        messageDiv.textContent = "希望シフトの読み込みに失敗しました。";
    }
}

// checkbox 群に availabilityMap を反映する
// availabilityMap: { "TOP":1, "LUNCH":0, ... }
function applyAvailabilityToCheckboxes(availabilityMap) {
    const preferenceGroup = document.querySelector('.shift-time-group');
    if (!preferenceGroup) return;

    preferenceGroup.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        const timeCode = checkbox.getAttribute('data-shift').toUpperCase();
        checkbox.checked = (availabilityMap[timeCode] === 1);
    });
}

// 「この従業員の希望を保存」ボタン押した時
async function savePreferenceData() {
    const selector = document.getElementById('employeePreferenceSelector');
    const preferenceGroup = document.querySelector('.shift-time-group');
    const messageDiv = document.getElementById('preferenceMessage');
    const dateInput = document.getElementById("shiftDateInput");

    const employeeIdStr = selector.value;
    const date = dateInput.value;

    if (!employeeIdStr) {
        messageDiv.style.color = 'red';
        messageDiv.textContent = "従業員を選択してください。";
        return;
    }
    if (!date) {
        messageDiv.style.color = 'red';
        messageDiv.textContent = "シフト日付を入力してください。";
        return;
    }

    const employeeId = parseInt(employeeIdStr, 10);

    const availabilityMap = {};
    let isChecked = false;

    preferenceGroup.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        const timeCode = checkbox.getAttribute('data-shift').toUpperCase();
        const isAvailable = checkbox.checked ? 1 : 0;
        availabilityMap[timeCode] = isAvailable;
        if (isAvailable === 1) isChecked = true;
    });

    if (!isChecked) {
        messageDiv.style.color = 'red';
        messageDiv.textContent = "警告: 少なくとも一つの時間帯を選択してください。";
        return;
    }

    // DTO 形式に合わせて送信
    const dto = {
        employeeId: employeeId,
        date: date,
        availabilityMap: availabilityMap
    };

    try {
        const res = await fetch(API_PREF, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        if (!res.ok) {
            throw new Error("HTTP " + res.status);
        }

        // フロント側のキャッシュも更新
        employeePreferences[employeeId] = {
            employeeId,
            date,
            availabilityMap
        };

        messageDiv.style.color = 'green';
        messageDiv.textContent = "希望シフトを保存しました。";

    } catch (e) {
        console.error("希望シフト保存中のエラー:", e);
        messageDiv.style.color = 'red';
        messageDiv.textContent = "希望シフトの保存に失敗しました。";
    }
}

// ==============================
// シフト作成関連
// ==============================

function renderAssignmentResult(result) {
    console.log("Rendering results:", result);
    latestShiftResponse = result;

    const resultSection = document.getElementById("result-section");
    const tbody = document.getElementById("resultTableBody");
    const messageContainer = document.getElementById("resultMessage");
    const selectedStaffListDiv = document.getElementById("selectedStaffList");

    resultSection.style.display = 'block';
    tbody.innerHTML = '';

    messageContainer.textContent = result.message || "（メッセージなし）";

    const workingStaff = result.workingStaff || {};
    const allNames = Object.entries(workingStaff)
        .flatMap(([time, list]) => list.map(e => `${e.name} (${time})`));
    selectedStaffListDiv.textContent =
        allNames.length > 0 ? `選定されたスタッフ: ${allNames.join(", ")}` : "選定されたスタッフはいません。";

    const finalAssignment = result.finalAssignment || {};
    if (Object.keys(finalAssignment).length === 0) {
        tbody.innerHTML = '<tr><td colspan="2">割り当て結果が見つかりませんでした。</td></tr>';
        return;
    }

    for (const [timeCode, posMap] of Object.entries(finalAssignment)) {
        const timeHeaderRow = tbody.insertRow();
        const headerCell = timeHeaderRow.insertCell();
        headerCell.colSpan = 2;
        headerCell.textContent = `【時間帯】${timeCode}`;
        headerCell.style.textAlign = 'center';
        headerCell.style.backgroundColor = '#e0e0e0';
        headerCell.style.fontWeight = 'bold';

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

// シフト条件 + 従業員 + 希望をまとめてサーバへ送る用
function collectFormData() {
    const shiftConditions = collectShiftConditions();

    // 従業員情報は DB からロードしているので
    // employeeCache をベースに送る
    const employeeCandidates = employeeCache.map(emp => ({
        id: emp.id,
        name: emp.name,
        skills: emp.skills
    }));

    const shiftPreferences = Object.values(employeePreferences);

    return {
        date: shiftConditions.date,
        dayOfWeekString: shiftConditions.dayOfWeekString,
        isHoliday: shiftConditions.isHoliday,
        employeeCandidates: employeeCandidates,
        shiftPreferences: shiftPreferences
    };
}

function collectShiftConditions() {
    const dateInput = document.getElementById("shiftDateInput");
    const selectedDay = document.querySelector('select[name = "dayOfWeekString"]');
    const selectedHoliday = document.querySelector('input[name="isHoliday"]:checked');

    const dayValue = selectedDay ? selectedDay.value : null;

    if (!selectedDay || dayValue === "") {
        throw new Error("曜日が選択されていません。");
    }
    if (!selectedHoliday) {
        throw new Error("祝日かどうかが選択されていません。");
    }
    if (!dateInput.value) {
        throw new Error("シフト日付を入力してください。");
    }

    return {
        date: dateInput.value,
        dayOfWeekString: dayValue,
        isHoliday: selectedHoliday.value === 'true'
    };
}

function collectEmployeeData() {
    const tbody = document.getElementById("employeeInputBody");
    const employeeList = [];

    tbody.querySelectorAll("tr").forEach(row => {
        const check = row.querySelector(".emp-check");
        if (!check.checked) return; // 未選択なら skip

        const id = parseInt(check.dataset.id);

        // 名前とスキルを取得（表示形式なので innerText から取得）
        const cols = row.querySelectorAll("td");

        const name = cols[2].innerText;

        const skillKeys = ["D","Y","A","I","IF","AF","W"];
        const skills = {};

        skillKeys.forEach((key, index) => {
            skills[key] = parseInt(cols[3 + index].innerText);
        });

        employeeList.push({
            id: id,
            name: name,
            skills: skills
        });
    });

    if (employeeList.length === 0) {
        throw new Error("出勤する従業員が選択されていません。");
    }

    return employeeList;
}


// シフト結果保存
async function saveCurrentShift() {
    const messageDiv = document.getElementById("saveResultMessage");

    if (!latestShiftResponse) {
        messageDiv.style.color = "red";
        messageDiv.textContent = "保存するシフト結果がありません。";
        return;
    }

    try {
        await API.saveShift(latestShiftResponse);
        messageDiv.style.color = "green";
        messageDiv.textContent = "シフトを保存しました！";
    } catch (err) {
        messageDiv.style.color = "red";
        messageDiv.textContent = "保存に失敗しました。";
    }
}

// 既存のadd/removeEmployeeRowはとりあえず残しておく（今後不要なら削除でOK）
function addEmployeeRow() {
    console.warn("現在はDBから従業員を読み込む運用のため、手動追加は非推奨です。");
}
function removeEmployeeRow() {
    console.warn("従業員の削除は従業員管理ページ（employee.html）から行ってください。");
}

// ==============================
// DOMHandler としてエクスポート
// ==============================

const DOMHandler = {
    addEmployeeRow,
    removeEmployeeRow,
    renderResult: renderAssignmentResult,
    collectFormData,
    setupPreferenceInput,   // 使うなら app.js からも呼べるようにしておく
    saveCurrentShift,
	loadEmployeesFromDB
};

export default DOMHandler;
