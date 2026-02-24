const VAPID_PUBLIC_KEY = "BH_VAfm6nG3QdYpDhsWkXNieAc_ZPIt33Ghk-BuHgVL-tnmTR-l8ePSTnRAQhSDrKRwcqC0cmEV-oeBhR_7IgE0";

document.addEventListener("DOMContentLoaded", initPushButton);

async function initPushButton() {
  if (!("serviceWorker" in navigator)) return;

  const registration = await navigator.serviceWorker.register("/service-worker.js");
  const subscription = await registration.pushManager.getSubscription();

  updateButtonUI(!!subscription);
}

async function togglePush() {
  if (!("serviceWorker" in navigator)) {
    alert("이 브라우저는 푸시를 지원하지 않습니다.");
    return;
  }

  const registration = await navigator.serviceWorker.register("/service-worker.js");
  const subscription = await registration.pushManager.getSubscription();

  if (subscription) {
    await subscription.unsubscribe();

    await fetch("/push/unsubscribe", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(subscription)
    });

    updateButtonUI(false);
  } else {
    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      alert("알림 권한이 필요합니다.");
      return;
    }

    const newSubscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY)
    });

    await fetch("/push/subscribe", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newSubscription)
    });

    updateButtonUI(true);
  }
}

function updateButtonUI(isSubscribed) {
  const btn = document.getElementById("push-toggle-btn");
  const text = document.getElementById("push-btn-text");

  btn.classList.remove(
    "bg-blue-600", "hover:bg-blue-700",
    "bg-blue-600", "hover:bg-blue-700",
    "bg-gray-400", "opacity-60"
  );

  if (isSubscribed) {
    btn.classList.add("bg-green-600", "hover:bg-green-700");
    text.innerText = "알림 활성화중";
  } else {
    btn.classList.add("bg-gray-400", "opacity-60");
    text.innerText = "알림 비활성화중";
  }
}

function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding)
    .replace(/-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  return Uint8Array.from([...rawData].map(c => c.charCodeAt(0)));
}