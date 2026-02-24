self.addEventListener("push", function (event) {

  console.log("🔥 PUSH 이벤트 수신");

  let data = {
    title: "기본 제목",
    body: "기본 메시지"
  };

  if (event.data) {
    try {
      data = event.data.json();
    } catch (e) {
      data = {
        title: "Test Push",
        body: event.data.text()
      };
    }
  }

  event.waitUntil(
    self.registration.showNotification(data.title, {
      body: data.body,
      icon: "/favicon.ico"
    })
  );
});