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

  // ★ 必ず JSON.parse
  const assignment = JSON.parse(record.finalAssignmentJson);

  for (const [time, posMap] of Object.entries(assignment)) {
    const h4 = document.createElement("h4");
    h4.textContent = `【${time}】`;
    container.appendChild(h4);

    const table = document.createElement("table");
    table.className = "assignment-table";
    table.innerHTML = `
      <thead>
        <tr><th>ポジション</th><th>担当者</th></tr>
      </thead>
      <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    for (const [pos, staffList] of Object.entries(posMap)) {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${pos}</td>
        <td>${staffList.length ? staffList.map(s => s.name).join(", ") : "―"}</td>
      `;
      tbody.appendChild(tr);
    }

    container.appendChild(table);
  }

  // ★ 編集UIを呼ぶ
  renderEditableShift(record);
}


let editableFinalAssignment = null;
let recordId = null;

function renderEditableShift(record) {
  recordId = record.id;
  editableFinalAssignment = JSON.parse(record.finalAssignmentJson);

  const tbody = document.getElementById("editShiftBody");
  tbody.innerHTML = "";

  for (const [time, posMap] of Object.entries(editableFinalAssignment)) {

    const staffPool = collectStaffForTime(posMap);

    for (const [pos, staffList] of Object.entries(posMap)) {
      const tr = document.createElement("tr");

      tr.innerHTML = `
        <td>${time}</td>
        <td>${pos}</td>
        <td></td>
      `;

      const td = tr.children[2];
      const currentId = staffList[0]?.id ?? "";

      const select = createStaffSelect(
        staffPool,
        currentId
      );

      select.addEventListener("change", e => {
        handleEdit(time, pos, e.target, staffPool);
      });

      td.appendChild(select);
      tbody.appendChild(tr);
    }
  }
}

function collectStaffForTime(posMap) {
  const map = new Map();

  Object.values(posMap).forEach(list => {
    list.forEach(staff => {
      map.set(staff.id, staff);
    });
  });

  return Array.from(map.values());
}

function createStaffSelect(staffPool, selectedId) {
  const select = document.createElement("select");

  // 未割り当て
  const empty = document.createElement("option");
  empty.value = "";
  empty.textContent = "---";
  select.appendChild(empty);

  staffPool.forEach(staff => {
    const opt = document.createElement("option");
    opt.value = staff.id;
    opt.textContent = staff.name;
    if (String(staff.id) === String(selectedId)) {
      opt.selected = true;
    }
    select.appendChild(opt);
  });

  return select;
}

function handleEdit(time, pos, selectElem, staffPool) {
  const newId = selectElem.value;
  const prev = editableFinalAssignment[time][pos][0] ?? null;

  // 未割り当て
  if (newId === "") {
    editableFinalAssignment[time][pos] = [];
    return;
  }

  // 重複チェック
  if (isDuplicate(time, pos, newId)) {
    alert("同じ時間帯で同じ人を複数ポジションに割り当てることはできません");

    // 元に戻す
    selectElem.value = prev?.id ?? "";
    return;
  }

  const staff = staffPool.find(s => String(s.id) === newId);
  if (!staff) return;

  editableFinalAssignment[time][pos] = [{
    id: staff.id,
    name: staff.name
  }];
}

function isDuplicate(time, currentPos, employeeId) {
  const posMap = editableFinalAssignment[time];

  for (const [pos, staffList] of Object.entries(posMap)) {
    if (pos === currentPos) continue;
    if (staffList.length === 0) continue;

    if (String(staffList[0].id) === String(employeeId)) {
      return true;
    }
  }
  return false;
}

document.getElementById("saveEditBtn").addEventListener("click", async () => {
  await fetch(`${API_BASE}/update`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      recordId,
      finalAssignment: editableFinalAssignment
    })
  });

  alert("修正を保存しました");
});



// 初期設定
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loadHistoryButton")
        .addEventListener("click", loadHistory);

    document.getElementById("searchButton")
        .addEventListener("click", searchHistoryByDate);
});