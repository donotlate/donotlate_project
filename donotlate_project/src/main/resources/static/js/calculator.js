let currentRoutes = [];

function setLocation(prefix, address, lat, lng) {
  document.getElementById(`${prefix}-input`).value = address || "";
  document.getElementById(`${prefix}-lat`).value = lat ?? "";
  document.getElementById(`${prefix}-lng`).value = lng ?? "";
}

function swapLocations() {
  const sAddr = document.getElementById("departure-input").value;
  const sLat  = document.getElementById("departure-lat").value;
  const sLng  = document.getElementById("departure-lng").value;

  const aAddr = document.getElementById("arrival-input").value;
  const aLat  = document.getElementById("arrival-lat").value;
  const aLng  = document.getElementById("arrival-lng").value;

  setLocation("departure", aAddr, aLat, aLng);
  setLocation("arrival", sAddr, sLat, sLng);
}

function sample4_execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      const addr = data.roadAddress || data.address;

      geocoder.addressSearch(addr, (result, status) => {
        if (status === kakao.maps.services.Status.OK && result.length > 0) {
          const lat = Number(result[0].y);
          const lng = Number(result[0].x);
          setLocation("departure", addr, lat, lng);
        }
      });
    }
  }).open();
}

function sample5_execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      const addr = data.roadAddress || data.address;

      geocoder.addressSearch(addr, (result, status) => {
        if (status === kakao.maps.services.Status.OK && result.length > 0) {
          const lat = Number(result[0].y);
          const lng = Number(result[0].x);
          setLocation("arrival", addr, lat, lng);
        }
      });
    }
  }).open();
}

let geocoder;

document.addEventListener("DOMContentLoaded", () => {
  geocoder = new kakao.maps.services.Geocoder();
  setDepartureToCurrentLocation();
});

function setDepartureToCurrentLocation() {
  const input = document.getElementById("departure-input");
  input.value = "현재 위치 불러오는 중...";

  if (!navigator.geolocation) {
    input.value = "현재 위치를 사용할 수 없습니다";
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const lat = pos.coords.latitude;
      const lng = pos.coords.longitude;

      geocoder.coord2Address(lng, lat, (result, status) => {
        if (status === kakao.maps.services.Status.OK && result.length > 0) {
          const address =
            result[0].road_address?.address_name ||
            result[0].address?.address_name;

          setLocation("departure", address, lat, lng);
        } else {
          input.value = "주소 변환 실패";
        }
      });
    },
    (err) => {
      console.warn("Geolocation error:", err);
      input.value = "위치 권한이 필요합니다";
    },
    {
      enableHighAccuracy: true,
      timeout: 8000,
      maximumAge: 0
    }
  );
}

let currentMode = null;

document.querySelectorAll(".transport-btn").forEach(btn => {
    btn.addEventListener("click", () => {

        const nextMode = btn.dataset.mode;
        if (currentMode === nextMode) return;

        document.querySelectorAll(".transport-btn").forEach(b => {
            b.classList.remove("bg-blue-50", "border-blue-500");
            b.classList.add("bg-gray-50", "border-gray-200");

            b.querySelector("i").classList.remove("text-blue-600");
            b.querySelector("i").classList.add("text-gray-600");

            b.querySelector("span").classList.remove("text-blue-600");
            b.querySelector("span").classList.add("text-gray-600");
        });

        btn.classList.remove("bg-gray-50", "border-gray-200");
        btn.classList.add("bg-blue-50", "border-blue-500");

        btn.querySelector("i").classList.remove("text-gray-600");
        btn.querySelector("i").classList.add("text-blue-600");

        btn.querySelector("span").classList.remove("text-gray-600");
        btn.querySelector("span").classList.add("text-blue-600");

        currentMode = nextMode;
        loadRoutes();
    });
});

async function loadRoutes() {

  const sx = document.getElementById("departure-lng").value;
  const sy = document.getElementById("departure-lat").value;
  const ex = document.getElementById("arrival-lng").value;
  const ey = document.getElementById("arrival-lat").value;

  if (!sx || !sy || !ex || !ey) {
    document.getElementById("route-result-container").innerHTML = "";
    return;
  }

  const dt = getDepartureDateTimeOptional();

  let url = `/calculator/routes?sx=${encodeURIComponent(sx)}&sy=${encodeURIComponent(sy)}&ex=${encodeURIComponent(ex)}&ey=${encodeURIComponent(ey)}&mode=${encodeURIComponent(currentMode)}`;

  if (dt) {
    url += `&departureTime=${encodeURIComponent(dt.time)}&dayType=${encodeURIComponent(dt.dayType)}`;
  }

  const container = document.getElementById("route-result-container");
  container.innerHTML = renderLoadingCards();

  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error("server error");

    const routes = await res.json();
    renderRoutes(routes);
  } catch (e) {
    console.error(e);
    container.innerHTML = renderErrorCard();
  }
}


function renderLoadingCards() {
  return `
    <section class="section-card animate-pulse">
      <div class="h-5 w-40 bg-gray-200 rounded mb-4"></div>
      <div class="space-y-3">
        <div class="h-4 bg-gray-200 rounded"></div>
        <div class="h-4 bg-gray-200 rounded"></div>
        <div class="h-4 bg-gray-200 rounded"></div>
      </div>
    </section>
  `;
}

function renderErrorCard() {
  return `
    <section class="section-card">
      <div class="font-semibold text-gray-900">경로를 불러오지 못했습니다.</div>
      <div class="text-sm text-gray-500 mt-1">잠시 후 다시 시도해주세요.</div>
    </section>
  `;
}

function renderRoutes(routes) {
  const container = document.getElementById("route-result-container");
  currentRoutes = routes;

  if (!routes || routes.length === 0) {
    container.innerHTML = `
      <section class="section-card">
        <div class="font-semibold text-gray-900">조건에 맞는 경로가 없습니다.</div>
        <div class="text-sm text-gray-500 mt-1">교통수단을 바꿔보세요.</div>
      </section>
    `;
    return;
  }

  container.innerHTML = routes
    .map((route, idx) => routeCardHtml(route, idx))
    .join("");
}

function routeCardHtml(route, idx) {
  const isExpanded = false;
  const steps = getStepsToRender(route, isExpanded);

  return `
    <section class="section-card route-card cursor-pointer" data-route-idx="${idx}" onclick="selectRoute(${idx})">
      <h2 class="heading-3 flex-center">
        <i class="fa-solid fa-route text-blue-600 mr-2"></i>
        ${idx + 1}번 경로 / 경유 노선 및 환승 정보
      </h2>

      <div class="space-y-4 route-steps">
        ${steps.map((step, stepIdx) =>
          step.type === "SUMMARY"
            ? summaryStepHtml(step)
            : stepHtml({ ...step, _routeIdx: idx }, stepIdx, steps.length, steps)
        ).join("")}
      </div>

      ${
        route.steps.length > 3
          ? `<button
               class="mt-4 text-sm text-blue-600 hover:underline"
               onclick="toggleRoute(${idx})"
               data-expanded="false">
               자세히 보기
             </button>`
          : ""
      }

      <div class="mt-6 p-4 bg-blue-50 rounded-xl">
        <div class="flex-between">
          <div>
            <div class="text-secondary">총 소요시간
              <span class="text-xs text-gray-500 ml-1">(실제 상황 기준 예상 소요시간)
              </span>
            </div>
            <div class="text-2xl font-bold text-blue-600">${route.totalTime}분</div>
          </div>
          <div class="text-right">
            <div class="text-secondary">환승 횟수</div>
            <div class="text-2xl font-bold text-gray-900">${route.transferCount}회</div>
          </div>
        </div>
      </div>
    </section>
  `;
}

function getStepColor(stepIdx, totalSteps) {
  if (stepIdx === 0) return "blue";
  if (stepIdx === totalSteps - 1) return "green";
  return "orange";
}

function stepHtml(step, stepIdx, totalSteps, steps) {
  const currentRouteIdx = step._routeIdx;
  const color = getStepColor(stepIdx, totalSteps);
  const isLast = stepIdx === totalSteps - 1;

  const isWalk = step.type === "WALK";

  const isFirstWalk =
    isWalk &&
    stepIdx === 0 &&
    steps[stepIdx + 1] &&
    steps[stepIdx + 1].type !== "WALK";

  const isLastWalk =
    isWalk &&
    isLast &&
    steps[stepIdx - 1] &&
    steps[stepIdx - 1].type !== "WALK";

  const isTransferWalk =
    isWalk &&
    steps[stepIdx - 1] &&
    steps[stepIdx + 1] &&
    steps[stepIdx - 1].type !== "WALK" &&
    steps[stepIdx + 1].type !== "WALK";

  let title = step.title;
  let desc  = step.description || "도보 이동";

  if (isFirstWalk) {
    title = "출발지 이동";
    desc  = "출발지에서 대중교통까지 이동";
  } else if (isTransferWalk) {
    title = "환승 이동";
    desc  = "환승 이동";
  } else if (isLastWalk) {
    title = "도착지 이동";
    desc  = "대중교통에서 도착지까지 이동";
  }

  if (step.type === "BUS") {
  title = renderBusNames(step);
  desc  = getBusRouteRange(step);
  }

  const circleText = isLast
    ? `<i class="fa-solid fa-flag-checkered"></i>`
    : stepIdx + 1;

  return `
    <div class="flex items-start space-x-4">
      <div class="flex flex-col items-center">
        <div class="w-10 h-10 rounded-full bg-${color}-500 flex items-center justify-center text-white font-bold">
          ${circleText}
        </div>
        ${!isLast ? `<div class="w-0.5 h-16 bg-${color}-300 my-1"></div>` : ""}
      </div>

      <div class="flex-1 pt-2">
        <div class="font-semibold text-gray-900">
          ${safe(title)}
        </div>

        <div class="text-secondary mt-1">
          ${safe(desc)}
        </div>

        <div class="text-xs text-muted mt-1">
          <i class="fa-solid fa-clock mr-1"></i>
          ${step.time ? `${step.time}분` : ""}
        </div>

        ${renderStationToggle(step, currentRouteIdx, stepIdx)}
      </div>
    </div>
  `;
}


function safe(str) {
  return String(str ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;");
}

function renderBusNames(step) {
  if (!step.busNames || step.busNames.length === 0) return "";
  return `버스 ${step.busNames.join(", ")}번`;
}

function renderStationCount(step) {
  if (!step.stationCount) return "";

  return step.type === "SUBWAY"
    ? `${step.stationCount}개 역 이동`
    : step.type === "BUS"
    ? `${step.stationCount}개 정거장 이동`
    : "";
}

function getWalkDescription(steps, idx, step) {
  const prev = steps[idx - 1];
  const next = steps[idx + 1];

  if (prev && next && prev.type !== "WALK" && next.type !== "WALK" && prev.type !== next.type) {
    return "환승 이동";
  }

  return step.description || "도보 이동";
}

function getFirstTransferStation(steps) {
  for (let i = 1; i < steps.length - 1; i++) {
    const prev = steps[i - 1];
    const curr = steps[i];
    const next = steps[i + 1];

    const isTransferWalk =
      curr.type === "WALK" &&
      prev.type !== "WALK" &&
      next.type !== "WALK";

    if (isTransferWalk) {
      return (
        next.startStation ||
        next.fromStation ||
        next.title ||
        null
      );
    }
  }
  return null;
}

function countTransfers(steps) {
  let count = 0;

  for (let i = 1; i < steps.length - 1; i++) {
    if (
      steps[i].type === "WALK" &&
      steps[i - 1].type !== "WALK" &&
      steps[i + 1].type !== "WALK"
    ) {
      count++;
    }
  }
  return count;
}

function buildCollapsedSteps(steps) {
  if (steps.length <= 3) return steps;

  const first = steps[0];
  const last = steps[steps.length - 1];

  const transferCount = countTransfers(steps);
  const firstTransferStation = getFirstTransferStation(steps);

  let desc = "대중교통 이용";

  if (transferCount > 0 && firstTransferStation) {
    desc =
      transferCount === 1
        ? `대중교통 이용 · ${firstTransferStation} 환승`
        : `대중교통 이용 · ${firstTransferStation} 첫 환승 외 ${
            transferCount - 1
          }회`;
  }

  return [
    first,
    {
      type: "SUMMARY",
      title: "대중교통 이용",
      description: desc,
      time: calcTransitTime(steps),
    },
    last,
  ];
}


function getStepsToRender(route, isExpanded) {
  return isExpanded
    ? route.steps
    : buildCollapsedSteps(route.steps);
}

function calcTransitTime(steps) {
  return steps
    .filter(s => s.type !== "WALK")
    .reduce((sum, s) => sum + (s.time || 0), 0);
}

function toggleRoute(routeIdx) {
  const section = document.querySelector(
    `section[data-route-idx="${routeIdx}"]`
  );

  const btn = section.querySelector("button");
  const stepsEl = section.querySelector(".route-steps");

  const isExpanded = btn.dataset.expanded === "true";
  const route = currentRoutes[routeIdx];

  const steps = getStepsToRender(route, !isExpanded);

  stepsEl.innerHTML = steps
  .map((step, stepIdx) =>
    step.type === "SUMMARY"
      ? summaryStepHtml(step)
      : stepHtml(
          { ...step, _routeIdx: routeIdx },
          stepIdx,
          steps.length,
          steps
        )
  )
  .join("");

  btn.textContent = isExpanded ? "자세히 보기" : "접기";
  btn.dataset.expanded = (!isExpanded).toString();
}

function summaryStepHtml(step) {
  return `
    <div class="flex items-start space-x-4">
      <div class="w-10 h-10 rounded-full bg-orange-500 flex items-center justify-center text-white font-bold">
        …
      </div>

      <div class="flex-1 pt-2">
        <div class="font-semibold text-gray-900">
          ${safe(step.title)}
        </div>
        <div class="text-secondary mt-1">
          ${safe(step.description)}
        </div>
        <div class="text-xs text-muted mt-1">
          <i class="fa-solid fa-clock mr-1"></i>
          ${step.time}분
        </div>
      </div>
    </div>
  `;
}

function getBusRouteRange(step) {

  if (step.description && step.description.includes("→")) {
    return step.description;
  }

  if (step.title && step.title.includes("→")) {
    return step.title;
  }

  if (step.from && step.to) {
    return `${step.from} → ${step.to}`;
  }

  if (step.startStation && step.endStation) {
    return `${step.startStation} → ${step.endStation}`;
  }

  return "";
}

function renderStationToggle(step, routeIdx, stepIdx) {
  if (!step.stations || step.stations.length < 2) return "";

  const id = `stations-${routeIdx}-${stepIdx}`;

  return `
    <div class="mt-1">
      <button
        class="text-xs text-blue-600 hover:underline flex items-center gap-1"
        onclick="toggleStations('${id}', this)">
        ▶ ${step.stationCount}개 ${
          step.type === "SUBWAY" ? "역" : "정거장"
        } 이동
      </button>

      <div id="${id}" class="hidden mt-2 pl-4 border-l border-gray-200 space-y-0.5">
        ${step.stations
          .map(
            (s, i) =>
              `<div class="text-xs text-gray-700">${i + 1}. ${safe(s)}</div>`
          )
          .join("")}
      </div>
    </div>
  `;
}

function toggleStations(id, btn) {
  const el = document.getElementById(id);
  const opened = !el.classList.contains("hidden");

  el.classList.toggle("hidden");
  btn.innerText = opened
    ? btn.innerText.replace("▼", "▶")
    : btn.innerText.replace("▶", "▼");
}

function getDepartureDateTime() {
    const date = document.getElementById("departure-date").value;
    const time = document.getElementById("departure-time").value;

    if (!date || !time) {
        alert("출발 희망 일시를 선택해주세요.");
        return null;
    }

    const base = new Date(`${date}T${time}:00`);

    return {
        date,
        time,
        dayType: resolveDayType(base)
    };
}

function resolveDayType(dateObj) {
    const day = dateObj.getDay();

    if (day === 0) return "HOLIDAY";
    if (day === 6) return "SATURDAY";
    return "WEEKDAY";
}

function getDepartureDateTimeOptional() {
  const date = document.getElementById("departure-date")?.value;
  const time = document.getElementById("departure-time")?.value;

  if (!date || !time) return null;

  const base = new Date(`${date}T${time}:00`);

  return {
    time,
    dayType: resolveDayType(base)
  };
}

function trimSeconds(timeStr) {
  if (!timeStr) return "";

  return timeStr.length >= 5 ? timeStr.slice(0, 5) : timeStr;
}

async function callAiPushTime(selectedRoute) {

    const prepareTime = parseInt(document.getElementById("prepare-time").value || 0);
    const bufferTime = parseInt(document.getElementById("transport-time").value || 0);

    const date = document.getElementById("departure-date").value;
    const time = document.getElementById("departure-time").value;

    if (!date || !time) {
        alert("출발 일시를 입력해주세요.");
        return;
    }

    const departureDateTime = `${date}T${time}:00`;

    const weather = "맑음";

    const request = {
        route: selectedRoute,
        prepareTime: prepareTime,
        bufferTime: bufferTime,
        weather: weather,
        departureDateTime: departureDateTime
    };

    try {
        const response = await fetch("/push/ai", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(request)
        });

        if (!response.ok) {
            throw new Error("AI 호출 실패");
        }

        const result = await response.text();

        updateAiPushUI(result, prepareTime, bufferTime, selectedRoute.totalTime);

    } catch (error) {
        console.error(error);
        alert("AI 추천 시간 계산 실패");
    }
}

function updateAiPushUI(aiTime, prepareTime, bufferTime, moveTime) {

    document.getElementById("ai-push-time").innerText = aiTime;

    document.getElementById("ai-prepare-time").innerText = prepareTime + "분";
    document.getElementById("ai-move-time").innerText = moveTime + "분";
    document.getElementById("ai-buffer-time").innerText = bufferTime + "분";

}

let selectedRouteIndex = null;

function selectRoute(idx) {

    console.log("선택됨:", idx);

    selectedRouteIndex = idx;

    document.querySelectorAll(".route-card").forEach(card => {
        card.classList.remove("ring-4", "ring-indigo-600", "bg-indigo-50");
    });

    const selectedCard = document.querySelector(
        `.route-card[data-route-idx="${idx}"]`
    );

    selectedCard.classList.add("ring-4", "ring-indigo-600", "bg-indigo-50");

    const route = currentRoutes[idx];

    document.getElementById("selected-route-label-ai").innerText = `${idx + 1}번 경로 사용`;

    document.getElementById("selected-route-label-recommend").innerText = `${idx + 1}번 경로 사용`;

    callAiPushTime(route);
    updateManualPushTime(route);
}


function isRushHour(date) {
  const day = date.getDay();
  const hour = date.getHours();

  const isWeekday = day >= 1 && day <= 5;
  if (!isWeekday) return false;

  const morning = hour >= 7 && hour < 10;
  const evening = hour >= 17 && hour < 20;
  return morning || evening;
}

function calcArrivalTime({
  departureAt,
  prepMinutes = 0,
  travelMinutes = 0,
  transportBuffer = 0,
  transferCount = 0,
  rushHour = null
}) {

  const departure = new Date(departureAt);

  const rush = rushHour !== null ? rushHour : isRushHour(departure);

  const perTransfer = rush ? 3 : 5;
  const transferBuffer = transferCount * perTransfer;

  const totalMinutes = prepMinutes + travelMinutes + transportBuffer + transferBuffer;

  const arrival = new Date(departure.getTime() + totalMinutes * 60000);

  return {
    arrival,
    breakdown: {
      prepMinutes,
      travelMinutes,
      transportBuffer,
      transferBuffer,
      rush
    }
  };
}

function fmtHHmm(d) {
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  return `${hh}:${mm}`;
}

function updateManualPushTime(route) {

  const prepareTime = parseInt(document.getElementById("prepare-time").value || 0);
  const bufferTime  = parseInt(document.getElementById("transport-time").value || 0);

  const date = document.getElementById("departure-date").value;
  const time = document.getElementById("departure-time").value;

  if (!date || !time) return;

  const arrivalAt = new Date(`${date}T${time}:00`);

  const rush = isRushHour(arrivalAt);
  const perTransfer = rush ? 3 : 5;
  const transferBuffer = route.transferCount * perTransfer;

  const totalMinutes = route.totalTime + prepareTime + bufferTime + transferBuffer;

  const pushTime = new Date(
      arrivalAt.getTime() - totalMinutes * 60000
  );

  document.getElementById("recommend-push-time").innerText = fmtHHmm(pushTime);

  document.getElementById("recommend-prepare-time").innerText = prepareTime + "분";

  document.getElementById("recommend-move-time").innerText = route.totalTime + "분";

  document.getElementById("recommend-buffer-time").innerText = bufferTime + "분";

  if (selectedRouteIndex !== null) {
      document.getElementById("selected-route-label-recommend").innerText = `${selectedRouteIndex + 1}번 경로 사용`;
  }
}
