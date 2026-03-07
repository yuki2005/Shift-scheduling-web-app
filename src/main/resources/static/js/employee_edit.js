const API_BASE = "http://localhost:8080/api/employees";

// URLパラメータから ../employee_edit.html?id=1 の id を取得
function getEmployeeIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

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

async function loadEmployee(id) {
    const res = await fetch(`${API_BASE}/${id}`);
    const emp = await res.json();

    document.getElementById("empNumber").value = emp.id;
    document.getElementById("empName").value = emp.name;

    // 各スキルを反映
    for (const [key, value] of Object.entries(emp.skills)) {
        const slider = document.getElementById(`skill_${key}`);
        const display = document.getElementById(`val_${key}`);

        if (slider) {
            slider.value = value;
            display.textContent = value;
        }
    }
}

async function saveEmployee(id) {
    const dto = {
        id: parseInt(document.getElementById("empNumber").value),
        name: document.getElementById("empName").value,
        skills: collectSkills()
    };

    const res = await fetch(`${API_BASE}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    });

    if (!res.ok) {
        alert("更新に失敗しました");
        return;
    }

    alert("更新しました！");
    location.href = "employee.html";
}

document.addEventListener("DOMContentLoaded", async () => {
    setupSliderDisplays();

    const id = getEmployeeIdFromUrl();
    await loadEmployee(id);

    document.getElementById("saveEmployeeBtn")
        .addEventListener("click", () => saveEmployee(id));
});
