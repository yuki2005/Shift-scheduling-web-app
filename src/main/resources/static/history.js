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

//日付で検索
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
    const detailArea = document.getElementById("detailView");
	
	let assignmentPretty = "";
	let workingPretty = "";

	try {
        assignmentPretty = JSON.stringify(JSON.parse(record.finalAssignmentJson), null, 2);
	} catch (e) {
        // JSON が壊れている場合はそのまま表示
        assignmentPretty = record.finalAssignmentJson;
    }
	
    const formatted = `
■ 日付: ${record.date}
■ 曜日: ${record.dayOfWeek}
■ 休日: ${record.holiday ? "はい" : "いいえ"}
■ メッセージ: ${record.message}

■ 最終割り当て結果:
${record.finalAssignmentJson}

    detailArea.textContent = formatted;
}

// 初期設定
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loadHistoryButton")
            .addEventListener("click", loadHistory);

    document.getElementById("searchButton")
            .addEventListener("click", searchHistoryByDate);
});
