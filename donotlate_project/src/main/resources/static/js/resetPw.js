document.addEventListener("DOMContentLoaded", () => {
    const resetPwForm = document.getElementById("resetPw-form");
    const emailInput = document.getElementById("email");
    const emailCheck = document.getElementById("email-check");
    const resetPwButton = document.getElementById("resetPw-button");

    // 비밀번호 초기화 처리 함수
    const handleResetPw = () => {
        const email = emailInput.value.trim();

        // 1. 이메일 유효성 검사
        const regExp = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        
        if (email.length === 0) {
            emailCheck.innerText = "이메일을 입력해주세요.";
            emailCheck.classList.add("text-red-500");
            emailCheck.classList.remove("text-blue-600", "text-green-500");
            emailInput.focus();
            return;
        }

        if (!regExp.test(email)) {
            emailCheck.innerText = "유효하지 않은 이메일 형식입니다.";
            emailCheck.classList.add("text-red-500");
            emailCheck.classList.remove("text-blue-600", "text-green-500");
            emailInput.focus();
            return;
        }

        // 2. 버튼 비활성화 및 로딩 상태 표시
        // 중복 클릭을 방지하기 위해 즉시 비활성화합니다.
        resetPwButton.disabled = true;
        resetPwButton.innerText = "메일 발송 중...";
        resetPwButton.classList.replace("from-blue-600", "from-gray-400");
        resetPwButton.classList.replace("to-indigo-600", "to-gray-500");

        emailCheck.innerText = "임시 비밀번호를 생성하여 메일로 발송 중입니다.";
        emailCheck.classList.remove("text-red-500");
        emailCheck.classList.add("text-blue-600");

        // 3. 서버로 POST 요청 (Fetch API)
        fetch("/member/resetPw", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ memberEmail: email })
        })
        .then(resp => resp.text())
        .then(result => {
            if (result.trim() === "1") {
                // 성공 시
                alert("임시 비밀번호가 발송되었습니다.\n메일함을 확인해주세요.");
                emailCheck.innerText = "발송 완료! 임시 비밀번호로 로그인 후 비밀번호를 꼭 변경해주세요.";
                emailCheck.classList.replace("text-blue-600", "text-green-500");
                
                // 입력창 잠금 및 버튼 상태 유지 (재발송 방지)
                emailInput.readOnly = true;
                resetPwButton.innerText = "발송 완료";
            } else {
                // 실패 시 (등록되지 않은 이메일 등)
                alert("등록되지 않은 이메일이거나 발송에 실패했습니다.");
                emailCheck.innerText = "등록되지 않은 이메일이거나 발송에 실패했습니다.";
                emailCheck.classList.replace("text-blue-600", "text-red-500");
                
                // 다시 시도할 수 있도록 버튼 활성화
                resetPwButton.disabled = false;
                resetPwButton.innerText = "비밀번호 초기화";
                resetPwButton.classList.replace("from-gray-400", "from-blue-600");
                resetPwButton.classList.replace("to-gray-500", "to-indigo-600");
            }
        })
        .catch(err => {
            console.error("Fetch Error:", err);
            alert("서버 통신 중 오류가 발생했습니다.");
            resetPwButton.disabled = false;
            resetPwButton.innerText = "비밀번호 초기화";
        });
    };

    // 폼 제출 시 실행 (엔터키 대응)
    resetPwForm.addEventListener("submit", (e) => {
        e.preventDefault(); // 기본 submit 막기
        handleResetPw();
    });
});