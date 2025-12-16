// history.js
const API_BASE = "http://localhost:8080/api/shift-records";

async function loadHistory() {
    const tbody = document.getElementById("historyBody");
    tbody.innerHTML = "<tr><td colspan='5'>読み込み中...</td></tr>";

    try {
        const res = await fetch(`${API_BASE}/all`);
        const list = await res.json();

        tbody.innerHTML = "";

        if (list.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5'>保存されたシフトはありません。</td></tr>";
            return;
        }

        list.forEach(record => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${record.id}</td>
                <td>${record.date}</td>
                <td>${record.dayOfWeek}</td>
                <td>${record.holiday ? "はい" : "いいえ"}</td>
                <td>${record.message}</td>
            `;

            // 行をクリックしたら詳細を表示
            row.addEventListener("click", () => showDetail(record));

            tbody.appendChild(row);
        });

    } catch (err) {
        console.error(err);
        tbody.innerHTML = "<tr><td colspan='5'>読み込みエラー</td></tr>";
    }
}

// 日付で検索
async function searchHistoryByDate() {
    const dateInput = document.getElementById("searchDateInput");
    const date = dateInput.value;

    const tbody = document.getElementById("historyBody");
    tbody.innerHTML = "<tr><td colspan='5'>検索中...</td></tr>";

    if (!date) {
        tbody.innerHTML = "<tr><td colspan='5'>日付を入力してください。</td></tr>";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/search?date=${date}`);
        const list = await res.json();

        tbody.innerHTML = "";

        if (list.length === 0) {
            tbody.innerHTML = `<tr><td colspan='5'>${date} のシフトはありません。</td></tr>`;
            return;
        }

        list.forEach(record => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${record.id}</td>
                <td>${record.date}</td>
                <td>${record.dayOfWeek}</td>
                <td>${record.holiday ? "はい" : "いいえ"}</td>
                <td>${record.message}</td>
            `;

            row.addEventListener("click", () => showDetail(record));
            tbody.appendChild(row);
        });

    } catch (err) {
        console.error(err);
        tbody.innerHTML = "<tr><td colspan='5'>検索エラー</td></tr>";
    }
}

// 詳細表示
function showDetail(record) {
    document.getElementById("detailTitle").textContent =
        `${record.date}（${record.dayOfWeek}） ${record.holiday ? "祝日" : "通常"}`;

    const container = document.getElementById("assignmentTables");
    container.innerHTML = "";

    if (!record.finalAssignmentJson) {
        container.textContent = "割り当てデータなし";
        return;
    }

    const assignment = record.finalAssignmentJson; // ← @JsonRawValue により既に Object

    for (const [time, posMap] of Object.entries(assignment)) {
        // 時間帯タイトル
        const h4 = document.createElement("h4");
        h4.textContent = `【${time}】`;
        container.appendChild(h4);

        // テーブル作成
        const table = document.createElement("table");
        table.className = "assignment-table";

        table.innerHTML = `
            <thead>
                <tr>
                    <th>ポジション</th>
                    <th>担当者</th>
                </tr>
            </thead>
            <tbody></tbody>
        `;

        const tbody = table.querySelector("tbody");

        for (const [pos, staffList] of Object.entries(posMap)) {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${pos}</td>
                <td>${
                    staffList.length > 0
                        ? staffList.map(s => s.name).join(", ")
                        : "―"
                }</td>
            `;
            tbody.appendChild(tr);
        }

        container.appendChild(table);
    }
}


// 初期設定
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loadHistoryButton")
        .addEventListener("click", loadHistory);

    document.getElementById("searchButton")
        .addEventListener("click", searchHistoryByDate);
});