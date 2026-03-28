// preference_history.js

const API_PREF_BY_DATE = "/api/preferences/date";

document.addEventListener("DOMContentLoaded", () => {
    const loadBtn = document.getElementById("loadPrefByDateBtn");
    loadBtn.addEventListener("click", loadPreferencesByDate);
});

async function loadPreferencesByDate() {
    const dateInput = document.getElementById("historyDate");
    const date = dateInput.value;
    const tbody = document.getElementById("prefHistoryBody");

    tbody.innerHTML = "";

    if (!date) {
        alert("日付を選択してください。");
        return;
    }

    try {
        const res = await fetch(`${API_PREF_BY_DATE}/${date}`);
        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        const prefs = await res.json(); // List<ShiftPreference> を想定

        if (!prefs || prefs.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6'>この日に保存された希望シフトはありません。</td></tr>";
            return;
        }

        prefs.forEach(pref => {
            const tr = document.createElement("tr");

            // pref.employee.name / pref.employee.id / pref.availabilityMap.TOP などを想定
            const nameCell = `<td>${pref.employee.name} (ID: ${pref.employee.id})</td>`;

            const av = pref.availabilityMap || {};
            const cell = code => `<td>${av[code] === 1 ? "◯" : ""}</td>`;

            tr.innerHTML = `
                ${nameCell}
                ${cell("TOP")}
                ${cell("LUNCH")}
                ${cell("IDLE")}
                ${cell("DINNER")}
                ${cell("LAST")}
            `;

            tbody.appendChild(tr);
        });

    } catch (e) {
        console.error(e);
        tbody.innerHTML = "<tr><td colspan='6'>読み込み中にエラーが発生しました。</td></tr>";
    }
}
