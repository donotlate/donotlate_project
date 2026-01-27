// 현재날씨 JS
function loadWeather(refresh = false) {
  if (!navigator.geolocation) {
    alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
    return;
  }

  renderLoadingState();

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;

      const url = refresh
      ? `/weather/dust?lat=${lat}&lon=${lon}&refresh=true`
      : `/weather/dust?lat=${lat}&lon=${lon}`;

      fetch(url)
        .then(res => {
          if (!res.ok) throw new Error("서버 응답 오류");
          return res.json();
        })
        .then(data => {
          renderWeather(data);
          loadUltraviolet();
        })
        .catch(err => {
          console.error(err);
          alert("날씨 정보를 불러오지 못했습니다.");
        });
    }
  );
}

function syncCurrentIconFromHour(hourList) {
  if (!hourList || hourList.length === 0) return;

  const icon = hourList[0].icon;
  const iconEl = document.getElementById("current-weather-icon");

  iconEl.className = "text-7xl sm:text-8xl lg:text-9xl opacity-90 " + getCurrentIconClass(icon);
}

function getCurrentIconClass(icon) {
  switch (icon) {
    case "sun":
      return "fa-solid fa-sun text-yellow-300";
    case "cloud":
      return "fa-solid fa-cloud text-gray-300";
    case "overcast":
      return "fa-solid fa-cloud text-gray-400";
    case "rain":
      return "fa-solid fa-cloud-rain text-blue-300";
    case "shower":
      return "fa-solid fa-cloud-showers-heavy text-blue-300";
    case "snow":
      return "fa-solid fa-snowflake text-blue-200";
    default:
      return "fa-solid fa-question text-gray-400";
  }
}

function renderWeather(data) {
  document.getElementById("weather-location").innerText = data.location;

  document.getElementById("detail-wind-speed").textContent = `${data.windSpeed} m/s`;

  document.getElementById("detail-humidity").textContent = `${data.humidity}%`;

  document.getElementById("detail-humidity-bar").style.width = `${data.humidity}%`;

  document.getElementById("weather-datetime").innerText = `${data.date} ${data.time}`;

  document.getElementById("weather-temp").innerText = Math.round(data.temperature);

  document.getElementById("weather-condition").innerText = data.condition;

  document.getElementById("weather-humidity").innerText = `${data.humidity}%`;

  document.getElementById("weather-wind").innerText = `${data.windSpeed}m/s`;

  document.getElementById("weather-feelslike").innerText = data.feelsLike + "°";

  if (data.pm25 !== null && data.pm25 !== undefined) {
  const pmEl = document.getElementById("detail-pm");
  const gradeEl = document.getElementById("detail-pm-grade");

  pmEl.innerText = `${data.pm25} ㎍/㎥`;
  gradeEl.innerText = data.pmGrade;

  gradeEl.className = "text-muted";
  if (data.pmGrade.includes("매우")) gradeEl.classList.add("text-blue-600");
  else if (data.pmGrade.includes("좋음")) gradeEl.classList.add("text-green-600");
  else if (data.pmGrade.includes("보통")) gradeEl.classList.add("text-yellow-600");
  else if (data.pmGrade.includes("나쁨")) gradeEl.classList.add("text-orange-600");
  } else {
  document.getElementById("detail-pm").innerText = "- ㎍/㎥";
  document.getElementById("detail-pm-grade").innerText = "정보 없음";
  }
}

function renderLoadingState() {
  document.getElementById("weather-location").innerText = "위치 불러오는 중...";

  document.getElementById("weather-datetime").innerText = "--";

  document.getElementById("weather-temp").innerText = "--";

  document.getElementById("weather-condition").innerText = "--";

  document.getElementById("weather-humidity").innerText = "--%";

  document.getElementById("weather-wind").innerText = "--m/s";

  document.getElementById("weather-feelslike").innerText = "--°";
}

// 시간대 날씨 JS
function loadHourWeather() {
  if (!navigator.geolocation) {
    alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;

      fetch(`/weather/hour?lat=${lat}&lon=${lon}`)
        .then(res => {
          if (!res.ok) throw new Error("시간별 날씨 응답 오류");
          return res.json();
        })
        .then(data => {
          console.log("시간별 날씨:", data);
          renderHourWeather(data);
        })
        .catch(err => {
          console.error(err);
        });
    },
    (error) => {
      console.error(error);
    }
  );
}

function renderHourWeather(list) {
  for (let i = 0; i < 8; i++) {
    const data = list[i];
    if (!data) break;

    document.getElementById(`hour-time-${i}`).innerText =
      i === 0 ? "지금" : data.time;

    document.getElementById(`hour-temp-${i}`).innerText =
      data.temp;

    document.getElementById(`hour-rain-${i}`).innerText =
      `${data.rainProb}%`;

    const iconEl = document.getElementById(`hour-icon-${i}`);
    iconEl.className = getHourIconClass(data.icon);
  }

  syncCurrentIconFromHour(list);
}

function getHourIconClass(icon) {
  switch (icon) {
    case "sun":
      return "fa-solid fa-sun text-3xl text-yellow-500 my-3";
    case "cloud":
      return "fa-solid fa-cloud text-3xl text-gray-400 my-3";
    case "overcast":
      return "fa-solid fa-cloud text-3xl text-gray-500 my-3";
    case "rain":
      return "fa-solid fa-cloud-rain text-3xl text-blue-400 my-3";
    case "shower":
      return "fa-solid fa-cloud-showers-heavy text-3xl text-blue-400 my-3";
    case "snow":
      return "fa-solid fa-snowflake text-3xl text-blue-200 my-3";
    default:
      return "fa-solid fa-question text-3xl text-gray-400 my-3";
  }
}

function getIconName(icon) {
  switch (icon) {
    case "sun": return "fa-sun";
    case "cloud": return "fa-cloud";
    case "overcast": return "fa-cloud";
    case "rain": return "fa-cloud-rain";
    case "shower": return "fa-cloud-showers-heavy";
    case "snow": return "fa-snowflake";
    default: return "fa-question";
  }
}

function loadWeekWeather() {
  if (!navigator.geolocation) {
    alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;

      fetch(`/weather/week?lat=${lat}&lon=${lon}`)
        .then(res => {
          if (!res.ok) throw new Error("주간 날씨 응답 오류");
          return res.json();
        })
        .then(data => {
          console.log("주간 날씨:", data);
          renderWeekWeather(data);
        })
        .catch(err => {
          console.error(err);
        });
    },
    (error) => {
      console.error(error);
    }
  );
}

function renderWeekWeather(list) {
  for (let i = 0; i < 8; i++) {
    const data = list[i];
    if (!data) break;
    
    const conditionText = data.condition && data.condition.includes("/")? data.condition.replace(" / ", " → ") : data.condition ?? "";

    console.log(`[WEEK ${i}]`, {
      day: data.dayLabel,
      condition_raw: data.condition,
      condition_render: conditionText,
      icon: data.icon,
      rain: data.rainProb
    });
    
    document.getElementById(`week-day-${i}`).innerText = data.dayLabel;

    document.getElementById(`week-condition-${i}`).innerText = conditionText;

    document.getElementById(`week-rain-${i}`).innerText = `${data.rainProb}%`;

    document.getElementById(`week-min-${i}`).innerText = `${data.minTemp}°`;

    document.getElementById(`week-max-${i}`).innerText = `${data.maxTemp}°`;

    const iconEl = document.getElementById(`week-icon-${i}`);

    iconEl.className ="fa-solid text-2xl w-8 text-center " + getWeekIconClass(data.icon);
  }
}

function getWeekIconClass(icon) {
  switch (icon) {
    case "sun":
      return "fa-sun text-yellow-500";
    case "cloud":
      return "fa-cloud text-gray-400";
    case "overcast":
      return "fa-cloud text-gray-500";
    case "rain":
      return "fa-cloud-rain text-blue-400";
    case "shower":
      return "fa-cloud-showers-heavy text-blue-400";
    case "snow":
      return "fa-snowflake text-blue-300";
    default:
      return "fa-question text-gray-400";
  }
}

function loadUltraviolet() {

  const areaNo = "1100000000";

  fetch(`/weather/ultraviolet?areaNo=${areaNo}`)
    .then(res => {
      if (!res.ok) throw new Error("자외선 응답 오류");
      return res.json();
    })
    .then(data => {
      const levelEl = document.getElementById("detail-uv-level");
      const indexEl = document.getElementById("detail-uv-index");

      if (!levelEl || !indexEl) return;

      if (data.uvIndex < 0) {
        levelEl.innerText = "-";
        indexEl.innerText = "UV 지수 -";
        return;
      }

      levelEl.innerText = data.level;
      indexEl.innerText = `UV 지수 ${data.uvIndex}`;

      levelEl.className = "text-3xl font-bold mb-1";
      if (data.level === "낮음") levelEl.classList.add("text-green-600");
      else if (data.level === "보통") levelEl.classList.add("text-yellow-600");
      else if (data.level === "높음") levelEl.classList.add("text-orange-600");
      else levelEl.classList.add("text-red-600");
    })
    .catch(err => {
      console.error("자외선 지수 조회 실패", err);
      const levelEl = document.getElementById("detail-uv-level");
      const indexEl = document.getElementById("detail-uv-index");
      if (levelEl) levelEl.innerText = "-";
      if (indexEl) indexEl.innerText = "UV 지수 -";
    });
}

document.addEventListener("DOMContentLoaded", () => {
  loadWeather(false);
  loadHourWeather();
  loadWeekWeather(); 
});