async function loadDashboard() {

    const res = await fetch("/ui/dashboard");
    const data = await res.json();

    renderPushList(data.pushList);
    renderAverage(data.averagePushTime, data.rankPercent);

}

function renderPushList(list) {

    const container = document.getElementById("push-list-container");
    container.innerHTML = "";

    if (!list || list.length === 0) {
        container.innerHTML =
            "<div class='text-gray-400 text-center py-8'>저장된 Push가 없습니다.</div>";
        return;
    }

    list.forEach(push => {

        const timeStr24 = formatTime(push.pushTime);
        const timeStr = formatToAmPm(timeStr24);
        const dayText = convertDayText(push.dayOfWeek);

        const isActive = push.isActive === "Y";

        let badgeClass = "";
        let badgeText = "";

        if (push.transportType === "SUBWAY") {
            badgeClass = "bg-blue-100 text-blue-700";
            badgeText = "지하철";
        }
        else if (push.transportType === "BUS") {
            badgeClass = "bg-green-100 text-green-700";
            badgeText = "버스";
        }
        else {
            badgeClass = "bg-yellow-100 text-yellow-700";
            badgeText = "혼합";
        }

        const routeText = push.startStation && push.endStation
        ? `${push.startStation} ~ ${push.endStation}`
        : `${push.startName} ~ ${push.endName}`;

        const opacityClass = isActive ? "" : "opacity-60";

        const checkedAttr = isActive ? "checked" : "";

        const card = `
        <div class="relative flex-between p-4 bg-gray-50 rounded-xl border border-gray-200 ${opacityClass}">

            <button 
                onclick="deletePush(${push.pushNo}, this)"
                class="absolute top-2 right-2 text-gray-400 hover:text-red-500 text-xs font-bold">
                ✕
            </button>

            <div class="flex-1">
                <div class="font-semibold text-gray-900">
                    ${push.pushName}
                </div>

                <div class="text-secondary mt-1">
                    ${dayText} | Push 알람 시간 : ${timeStr}
                </div>

                <div class="flex-center-gap-2 mt-2">
                  <span class="px-3 py-1 rounded-full text-xs font-semibold ${badgeClass}">
                      ${badgeText}
                  </span>

                  <span class="text-xs text-muted">
                      ${routeText}
                  </span>
                </div>
            </div>

            <label class="relative inline-flex items-center cursor-pointer ml-4">
              <input type="checkbox" ${checkedAttr}
                  onchange="togglePushActive(${push.pushNo}, this)"
                  class="sr-only peer">
                
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none
                  peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer
                  peer-checked:after:translate-x-full
                  peer-checked:after:border-white after:content-['']
                  after:absolute after:top-[2px] after:left-[2px]
                  after:bg-white after:border-gray-300 after:border
                  after:rounded-full after:h-5 after:w-5 after:transition-all
                  peer-checked:bg-blue-600">
              </div>
          </label>
        </div>
        `;

        container.innerHTML += card;
    });
}

function renderAverage(avgMinutes, rankPercent) {

    if (avgMinutes == null) return;

    let hour24 = Math.floor(avgMinutes / 60);
    const minute = avgMinutes % 60;

    const ampm = hour24 >= 12 ? "오후" : "오전";

    let hour12 = hour24 % 12;
    if (hour12 === 0) hour12 = 12;

    const timeStr =
        hour12.toString().padStart(2, "0") + ":" +
        minute.toString().padStart(2, "0");

    document.getElementById("avg-push-time").innerText = timeStr;
    document.getElementById("avg-push-ampm").innerText = ampm;

    if (rankPercent != null) {
        document.getElementById("avg-rank-text").innerText =
            `당신은 상위 ${rankPercent}%의 일찍 일어나는 사람입니다.`;
    }
}

function formatTime(num) {

    const str = num.toString().padStart(4, "0");
    return str.slice(0, 2) + ":" + str.slice(2);
}

function convertDayText(dayStr) {

    if (!dayStr) return "";

    const map = {
        1: "월",
        2: "화",
        3: "수",
        4: "목",
        5: "금",
        6: "토",
        7: "일"
    };

    const cleaned = String(dayStr).match(/[1-7]/g);
    if (!cleaned) return "";

    const days = cleaned.map(Number).sort((a,b)=>a-b);

    if (days.length === 1) {
        return map[days[0]];
    }

    if (days.join("") === "1234567") return "매일";

    let isContinuous = true;
    for (let i = 1; i < days.length; i++) {
        if (days[i] !== days[i-1] + 1) {
            isContinuous = false;
            break;
        }
    }

    if (isContinuous) {
        return `${map[days[0]]}~${map[days[days.length - 1]]}`;
    }

    return days.map(d => map[d]).join(",");
}

function formatToAmPm(time24) {

    const [hourStr, minute] = time24.split(":");
    let hour = parseInt(hourStr);

    const ampm = hour >= 12 ? "PM" : "AM";

    hour = hour % 12;
    if (hour === 0) hour = 12;

    return `${hour.toString().padStart(2, "0")}:${minute} ${ampm}`;
}

async function togglePushActive(pushNo, checkbox) {

    const isActive = checkbox.checked ? 1 : 0;

    const card = checkbox.closest(".flex-between");

    try {
        await fetch(`/ui/push/${pushNo}/active`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ isActive })
        });

        if (isActive === 0) {
            card.classList.add("opacity-60");
        } else {
            card.classList.remove("opacity-60");
        }

    } catch (e) {
        alert("상태 변경 실패");
        checkbox.checked = !checkbox.checked;
    }
}

async function deletePush(pushNo, btn) {

    if (!confirm("정말 삭제하시겠습니까?")) return;

    try {
        const res = await fetch(`/ui/push/${pushNo}`, {
            method: "DELETE"
        });

        if (!res.ok) throw new Error();

        const card = btn.closest(".relative");
        card.remove();

    } catch (e) {
        alert("삭제 실패");
    }
}

async function loadDashboardAi(lat, lon) {

    try {

        const res = await fetch(
            `/ui/dashboard/ai?lat=${lat}&lon=${lon}`,
            { method: "POST" }
        );

        if (!res.ok) throw new Error();

        const text = await res.text();

        document.getElementById("ai-title").innerText =
            "오늘의 출근 전략";

        document.getElementById("ai-message").innerText = text;

    } catch (e) {
        showAiFallback();
    }
}

function showAiFallback() {
    document.getElementById("ai-title").innerText =
        "오늘도 좋은 하루 보내세요!";

    document.getElementById("ai-message").innerText =
        "출근 준비를 차분히 진행해보세요.";
}

document.addEventListener("DOMContentLoaded", async () => {

    await loadDashboard();

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((pos) => {

            const lat = pos.coords.latitude;
            const lon = pos.coords.longitude;

            loadDashboardAi(lat, lon);
        });
    }
});