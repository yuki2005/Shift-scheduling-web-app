const API_BASE = "/api/employees";

/** スライダーの現在値をまとめて返す */
function collectSkills() {
    return {
        D: parseInt(document.getElementById("skill_D").value),
        Y: parseInt(document.getElementById("skill_Y").value),
        A: parseInt(document.getElementById("skill_A").value),
        I: parseInt(document.getElementById("skill_I").value),
        IF: parseInt(document.getElementById("skill_IF").value),
        AF: parseInt(document.getElementById("skill_AF").value),
        W: parseInt(document.getElementById("skill_W").value)
    };
}

async function createEmployee() {
    const dto = {
        id: parseInt(document.getElementById("empNumber").value),
        name: document.getElementById("empName").value,
        skills: collectSkills()
    };

    const res = await fetch(API_BASE, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    });

    if (!res.ok) {
        alert("登録に失敗しました");
        return;
    }

    alert("登録しました");
    loadEmployees();
}

async function deleteEmployee(employeeNumber) {
    if (!confirm("本当に削除しますか？")) return;

    const res = await fetch(`${API_BASE}/${employeeNumber}`, {
        method: "DELETE"
    });

    if (!res.ok) {
        alert("削除に失敗しました");
        return;
    }

    alert("削除しました");
    loadEmployees(); // 最新一覧を再読み込み
}


async function loadEmployees() {
    const res = await fetch(API_BASE);
    const list = await res.json();

    const tbody = document.getElementById("employeeTableBody");
    tbody.innerHTML = "";

    list.forEach(emp => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${emp.id}</td>
            <td>${emp.name}</td>
            <td>${JSON.stringify(emp.skills)}</td>
            <td>
				<button onclick="location.href='employee_edit.html?id=${emp.id}'">編集</button>
			    <button data-id="${emp.id}" class="deleteBtn">削除</button>
            </td>
        `;

        tbody.appendChild(tr);
    });
	
	// 削除ボタンのイベント設定
	document.querySelectorAll(".deleteBtn").forEach(btn => {
	    btn.addEventListener("click", () => {
	        const empNum = parseInt(btn.dataset.id);
	        deleteEmployee(empNum);
	    });
	});
}

/** スライダーの値を常に横の数字へ反映 */
function setupSliderDisplays() {
    const skillKeys = ["D", "Y", "A", "I", "IF", "AF", "W"];

    skillKeys.forEach(key => {
        const slider = document.getElementById(`skill_${key}`);
        const display = document.getElementById(`val_${key}`);

        slider.addEventListener("input", () => {
            display.textContent = slider.value;
        });
    });
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("createEmployeeBtn").addEventListener("click", createEmployee);
    setupSliderDisplays();
    loadEmployees();
});
