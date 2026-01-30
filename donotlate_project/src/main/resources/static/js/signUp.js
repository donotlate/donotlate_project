/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-22
 * 회원가입
 */
// 유효성 검사 여부를 기록할 객체
const checkObj = {
    "name": false,
    "email": false,
    "authKey": false, 
    "password": false,
    "passwordConfirm": false,
    "terms": false
};

// 시간을 00:00 형태로 만들기 위한 보조 함수
function addZero(num) {
    return num < 10 ? "0" + num : num;
}

// 시간 표시 보조 함수
function displayTime(seconds) {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    const emailAuthMessage = document.getElementById("emailAuthMessage");
    emailAuthMessage.innerText = `${addZero(m)}:${addZero(s)}`;
}

// 에러 시 버튼 복구 함수
function resetAuthBtn() {
    const sendAuthKeyBtn = document.getElementById("sendAuthKeyBtn");
    const emailAuthMessage = document.getElementById("emailAuthMessage");
    sendAuthKeyBtn.disabled = false;
    sendAuthKeyBtn.innerText = "인증번호 받기";
    emailAuthMessage.innerText = "발송 실패";
    emailAuthMessage.classList.add("text-red-500");
    emailAuthMessage.classList.remove("text-blue-600");
}

// ==========================================
// 1. 이름 유효성 검사
// ==========================================
const nameInput = document.getElementById("name");
nameInput.addEventListener("input", function() {
    const val = nameInput.value.trim();
    const regExp = /^[가-힣a-zA-Z]{2,10}$/;

    if (val.length > 0 && regExp.test(val)) {
        checkObj.name = true;
        nameInput.classList.remove("border-red-500");
        nameInput.classList.add("border-green-500");
    } else {
        checkObj.name = false;
        nameInput.classList.add("border-red-500");
        nameInput.classList.remove("border-green-500");
    }
});

// ==========================================
// 2. 이메일 유효성 검사 및 중복 체크
// ==========================================
const emailInput = document.getElementById("email");
const emailCheck = document.getElementById("email-check");
const sendAuthKeyBtn = document.getElementById("sendAuthKeyBtn");

emailInput.addEventListener("input", function() {
    const val = emailInput.value.trim();
    
    checkObj.email = false;
    checkObj.authKey = false; 
    emailCheck.innerText = "";
    emailCheck.classList.remove("text-green-500", "text-red-500");
    sendAuthKeyBtn.disabled = true;

    if (val.length === 0) {
        emailCheck.innerText = "이메일을 입력해주세요.";
        emailCheck.classList.add("text-red-500");
        return;
    }

    const regExp = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!regExp.test(val)) {
        emailCheck.innerText = "유효하지 않은 이메일 형식입니다.";
        emailCheck.classList.add("text-red-500");
        return;
    }

    // 중복 검사 (서버 응답이 0이면 사용 가능)
    fetch("/member/checkId", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: val
    })
    .then(resp => resp.text())
    .then(result => {
        if (Number(result) === 0) { 
            emailCheck.innerText = "사용 가능한 이메일입니다. 인증번호를 받아주세요.";
            emailCheck.classList.add("text-green-500");
            emailCheck.classList.remove("text-red-500");
            checkObj.email = true;
            sendAuthKeyBtn.disabled = false;
        } else {
            emailCheck.innerText = "이미 사용 중인 이메일입니다.";
            emailCheck.classList.add("text-red-500");
            emailCheck.classList.remove("text-green-500");
            checkObj.email = false;
            sendAuthKeyBtn.disabled = true;
        }
    })
    .catch(err => console.error("중복 체크 에러:", err));
});

// ==========================================
// 3. 인증번호 전송 및 타이머
// ==========================================
const emailAuthMessage = document.getElementById("emailAuthMessage");
const authKeyInput = document.getElementById("inputAuthKey");
let authTimer;
const authMaxSeconds = 300; 

sendAuthKeyBtn.addEventListener("click", function() {
    if(!checkObj.email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    checkObj.authKey = false;
    authKeyInput.disabled = false;
    authKeyInput.value = "";
    clearInterval(authTimer); 
    
    sendAuthKeyBtn.disabled = true;
    sendAuthKeyBtn.innerText = "전송 중..."; 
    emailAuthMessage.innerText = "메일을 발송 중입니다...";
    emailAuthMessage.classList.add("text-blue-600");
    emailAuthMessage.classList.remove("text-red-500", "text-green-500");

    fetch("/member/sendAuthKey", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: emailInput.value })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result.trim() == "1") { 
            alert("인증번호가 발송되었습니다.");
            
            let remainingTime = authMaxSeconds;
            displayTime(remainingTime);

            authTimer = setInterval(function() {
                remainingTime--;
                displayTime(remainingTime);

                if (remainingTime <= 0) {
                    clearInterval(authTimer);
                    emailAuthMessage.innerText = "인증 시간 만료";
                    emailAuthMessage.classList.replace("text-blue-600", "text-red-500");
                    authKeyInput.disabled = true;
                    sendAuthKeyBtn.disabled = false;
                    sendAuthKeyBtn.innerText = "인증번호 재전송";
                }
            }, 1000);
        } else {
            alert("메일 발송 실패 (서버 응답 오류)");
            resetAuthBtn();
        }
    })
    .catch(err => {
        console.error("Fetch 에러:", err);
        alert("서버 통신 중 오류가 발생했습니다.");
        resetAuthBtn();
    });
});

// ==========================================
// 4. 인증번호 확인 로직
// ==========================================
const authKeyCheckMessage = document.getElementById("inputAuthKey-check"); 

authKeyInput.addEventListener("input", function() {
    const val = authKeyInput.value.trim();
    checkObj.authKey = false;
    authKeyCheckMessage.innerText = ""; // 메시지 초기화
    
    // 6글자가 아니면 서버 요청을 보내지 않음
    if (val.length < 6) {
        authKeyCheckMessage.innerText = "인증번호 6자리를 입력해주세요.";
        authKeyCheckMessage.classList.add("text-red-500");
        authKeyCheckMessage.classList.remove("text-green-500");
        return;
    }

    // 서버로 인증 확인 요청
    fetch("/member/checkAuthKey", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: emailInput.value,
            authKey: val
        })
    })
    .then(resp => resp.text())
    .then(result => {
        if (result.trim() === "1") { 
            // 인증 성공 시
            authKeyCheckMessage.innerText = "인증되었습니다.";
            authKeyCheckMessage.classList.add("text-green-500");
            
            clearInterval(authTimer); // 타이머 정지
            emailAuthMessage.innerText = "인증 완료"; 
            emailAuthMessage.classList.replace("text-blue-600", "text-green-500");

            sendAuthKeyBtn.innerText = "인증 완료"; 
            
            checkObj.authKey = true;   // 유효성 체크 객체 업데이트
            authKeyInput.readOnly = true; // 입력창 잠금
            emailInput.readOnly = true; // 이메일 입력창 잠금
            sendAuthKeyBtn.disabled = true; // 전송 버튼 잠금
        } else {
            // 인증 실패 시
            authKeyCheckMessage.innerText = "인증번호가 일치하지 않거나 만료되었습니다.";
            authKeyCheckMessage.classList.replace("text-green-500", "text-red-500");
            checkObj.authKey = false;
        }
    })
    .catch(err => {
        console.error("인증 확인 에러:", err);
    });
});

// ==========================================
// 5. 비밀번호 유효성 및 일치 검사
// ==========================================
const pwInput = document.getElementById("password");
const pwConfirmInput = document.getElementById("password-confirm");
const pwCheck = document.getElementById("password-check");
const pwConfirmCheck = document.getElementById("password-confirm-check");

function checkPwMatch() {
    const pwVal = pwInput.value.trim();
    const confirmVal = pwConfirmInput.value.trim();

    if (confirmVal.length === 0) {
        checkObj.passwordConfirm = false;
        pwConfirmCheck.innerText = "";
        return; 
    }

    if (pwVal === confirmVal) {
        pwConfirmCheck.innerText = "비밀번호가 일치합니다.";
        pwConfirmCheck.classList.add("text-green-500");
        pwConfirmCheck.classList.remove("text-red-500");
        checkObj.passwordConfirm = true;
    } else {
        pwConfirmCheck.innerText = "비밀번호가 일치하지 않습니다.";
        pwConfirmCheck.classList.add("text-red-500");
        pwConfirmCheck.classList.remove("text-green-500");
        checkObj.passwordConfirm = false;
    }
}

pwInput.addEventListener("input", function() {
    const val = pwInput.value.trim();
    checkObj.password = false;
    const regExp = /^[A-Za-z0-9!@#$%^&*]{8,15}$/;

    if (regExp.test(val)) {
        pwCheck.innerText = "사용 가능한 비밀번호입니다.";
        pwCheck.classList.replace("text-red-500", "text-green-500");
        checkObj.password = true;
    } else {
        pwCheck.innerText = "8~15자 영문, 숫자, 특수문자(!, @, #, $, %, ^, &, *)로 조합해주세요.";
        pwCheck.classList.remove("text-green-500");
        pwCheck.classList.add("text-red-500");
        checkObj.password = false;
    }
    if(pwConfirmInput.value.length > 0) checkPwMatch();
});

pwConfirmInput.addEventListener("input", checkPwMatch);

// ==========================================
// 7. 약관 동의 및 최종 가입 버튼
// ==========================================
const termsCheckboxes = document.querySelectorAll("input[type='checkbox']");
const signupForm = document.getElementById("signup-form");

signupForm.addEventListener("submit", function(e) {
    const allTermsChecked = Array.from(termsCheckboxes).every(box => box.checked);
    checkObj.terms = allTermsChecked;

    for (let key in checkObj) {
        if (!checkObj[key]) {
            e.preventDefault();
            let msg = "";
            switch (key) {
                case "name": msg = "이름을 확인해주세요."; break;
                case "email": msg = "이메일 중복 검사를 완료해주세요."; break;
                case "authKey": msg = "이메일 인증을 완료해주세요."; break;
                case "password": msg = "비밀번호 형식을 확인해주세요."; break;
                case "passwordConfirm": msg = "비밀번호가 일치하지 않습니다."; break;
                case "terms": msg = "모든 약관에 동의해야 합니다."; break;
            }
            alert(msg);
            return;
        }
    }
});

/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-29
 * 비밀번호 보임 & 숨김 (구조 변경 대응)
 */
document.querySelectorAll('.pw-toggle').forEach(button => {
    button.addEventListener('click', function() {
        // 현재 버튼과 가장 가까운 relative 박스 안의 input 찾기
        const input = this.closest('.relative').querySelector('input');
        const icon = this.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.replace('fa-eye', 'fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.replace('fa-eye-slash', 'fa-eye');
        }
    });
});