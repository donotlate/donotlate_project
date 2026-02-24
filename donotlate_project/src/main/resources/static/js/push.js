const VAPID_PUBLIC_KEY = "BH_VAfm6nG3QdYpDhsWkXNieAc_ZPIt33Ghk-BuHgVL-tnmTR-l8ePSTnRAQhSDrKRwcqC0cmEV-oeBhR_7IgE0";

async function subscribePush() {

  if (!("serviceWorker" in navigator)) {
    alert("이 브라우저는 푸시를 지원하지 않습니다.");
    return;
  }

  const permission = await Notification.requestPermission();
  if (permission !== "granted") {
    alert("알림 권한이 필요합니다.");
    return;
  }

  const registration = await navigator.serviceWorker.register("/service-worker.js");

  const subscription = await registration.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY)
  });

  await fetch("/push/subscribe", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(subscription)
  });

  alert("푸시 구독 완료");
}

function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding)
    .replace(/-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  return Uint8Array.from([...rawData].map(c => c.charCodeAt(0)));
}