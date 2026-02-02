
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
