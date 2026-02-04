
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

let currentMode = "SUBWAY";

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

  const url = `/calculator/routes?sx=${encodeURIComponent(sx)}&sy=${encodeURIComponent(sy)}&ex=${encodeURIComponent(ex)}&ey=${encodeURIComponent(ey)}&mode=${encodeURIComponent(currentMode)}`;

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
  return `
    <section class="section-card">
      <h2 class="heading-3 flex-center">
        <i class="fa-solid fa-route text-blue-600 mr-2"></i>
        ${idx + 1}번 경로 / 경유 노선 및 환승 정보
      </h2>

      <div class="space-y-4">
        ${route.steps
          .map((step, stepIdx) =>
            stepHtml(step, stepIdx, route.steps.length, route.steps)
          )
          .join("")}
      </div>

      <div class="mt-6 p-4 bg-blue-50 rounded-xl">
        <div class="flex-between">
          <div>
            <div class="text-secondary">총 소요시간</div>
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
  const color = getStepColor(stepIdx, totalSteps);
  const isLast = stepIdx === totalSteps - 1;

  const isTransferWalk = step.type === "WALK" && 
  steps[stepIdx - 1] &&
  steps[stepIdx + 1] &&
  steps[stepIdx - 1].type !== "WALK" &&
  steps[stepIdx + 1].type !== "WALK";

  const title = isTransferWalk ? "환승 이동" : step.title;
  const desc  = isTransferWalk ? "환승 이동" : (step.description || "도보 이동");

  const circleText = isLast ? `<i class="fa-solid fa-flag-checkered"></i>` : stepIdx + 1;

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

        ${
          step.type === "BUS"
            ? `<div class="text-sm text-gray-700 mt-1">${renderBusNames(step)}</div>`
            : ""
        }

        <div class="text-xs text-muted mt-1">
          <i class="fa-solid fa-clock mr-1"></i>
          ${step.time ? `${step.time}분` : ""}
          ${renderStationCount(step) ? ` · ${renderStationCount(step)}` : ""}
        </div>
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