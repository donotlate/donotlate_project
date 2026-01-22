
function loadWeather() {
  if (!navigator.geolocation) {
    alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;

      console.log("위도:", lat);
      console.log("경도:", lon);

      fetch(`/weather/main?lat=${lat}&lon=${lon}`)
        .then(res => {
          if (!res.ok) throw new Error("서버 응답 오류");
          return res.json();
        })
        .then(data => {
          console.log("날씨 데이터:", data);
          renderWeather(data);
        })
        .catch(err => {
          console.error(err);
          alert("날씨 정보를 불러오지 못했습니다.");
        });
    },
    (error) => {
      console.error(error);
      alert("위치 권한을 허용해야 날씨를 볼 수 있습니다.");
    }
  );
}

function renderWeather(data) {
  document.getElementById("weather-location").innerText = data.location;

  document.getElementById("weather-datetime").innerText = `${data.date} ${data.time}`;

  document.getElementById("weather-temp").innerText = Math.round(data.temperature);

  document.getElementById("weather-condition").innerText = data.condition;

  document.getElementById("weather-humidity").innerText = `${data.humidity}%`;

  document.getElementById("weather-wind").innerText = `${data.windSpeed}m/s`;

  document.getElementById("weather-feelslike").innerText = data.feelsLike + "°";
}

document.addEventListener("DOMContentLoaded", loadWeather);