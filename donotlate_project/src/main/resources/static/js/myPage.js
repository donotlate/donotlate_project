/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-29
 * 마이페이지 - 이름변경
 */
function changeName(){
    const changedName = document.getElementById("changedName").value;
    
    fetch("/myPage/nameChange?changedName=" + changedName)
    .then(response => response.json())
    .then(data => {
        if(data.status === "success"){
            alert(data.message);
            location.reload();
        }
        else {
            alert("이름 변경에 실패하였습니다.\n사유 : " + data.message);
        }
    })
    .catch(err => console.error("에러 발생:", err));
}
/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-29
 * 마이페이지 - 비밀번호 보임 & 숨김
 */
document.querySelectorAll('.relative button').forEach(button => {
    button.addEventListener('click', function(e) {
        //버튼 클릭 시 폼 submit 방지 / 안쓰기는 하는데 button이라
        e.preventDefault();

        const input = this.parentElement.querySelector('input');
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

/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-29
 * 마이페이지 - 비밀번호 변경 검증 로직
 */
const checkObj = {
    "currentPw": false,
    "newPw": false,
    "newPwConfirm": false
};

const currentPw = document.getElementById("currentPw");
const newPw = document.getElementById("newPw");
const newPwConfirm = document.getElementById("newPw-confirm");
const pwMessage = document.getElementById("pwMessage");
const pwConfirmMessage = document.getElementById("pwConfirmMessage");

//공백 제거 로직 추가
currentPw.addEventListener("input", function() {
    this.value = this.value.replace(/\s/g, "");
})
newPw.addEventListener("input", function() {
    this.value = this.value.replace(/\s/g, "");
});
newPwConfirm.addEventListener("input", function() {
    this.value = this.value.replace(/\s/g, "");
})

// 1. 새 비밀번호 유효성 검사
newPw.addEventListener("input", function() {
    
    this.value = this.value.replace(/\s/g, "");

    const val = newPw.value.trim();
    const regExp = /^[A-Za-z0-9!@#$%^&*]{8,15}$/;

    if (val.length === 0) {
        pwMessage.innerText = "비밀번호를 입력해주세요.";
        pwMessage.classList.add("text-red-500");
        pwMessage.classList.remove("text-green-500");
        checkObj.newPw = false;
        return;
    }

    if (regExp.test(val)) {
        pwMessage.innerText = "사용 가능한 비밀번호입니다.";
        pwMessage.classList.add("text-green-500");
        pwMessage.classList.remove("text-red-500");
        checkObj.newPw = true;
    } else {
        pwMessage.innerText = "8~15자 영문, 숫자, 특수문자 조합으로 입력해주세요.";
        pwMessage.classList.add("text-red-500");
        pwMessage.classList.remove("text-green-500");
        checkObj.newPw = false;
    }

    // 새 비밀번호를 입력할 때마다 '확인' 필드와 일치하는지도 체크
    if (newPwConfirm.value.length > 0) checkPwMatch();
});

// 2. 비밀번호 일치 확인 함수
function checkPwMatch() {
    if (newPw.value.length === 0) {
        pwConfirmMessage.innerText = "비밀번호를 입력해주세요.";
        pwConfirmMessage.classList.add("text-red-500");
        pwConfirmMessage.classList.remove("text-green-500");
        checkObj.newPwConfirm = false;
        return;
    }
    if (newPw.value === newPwConfirm.value) {
        pwConfirmMessage.innerText = "비밀번호가 일치합니다.";
        pwConfirmMessage.classList.add("text-green-500");
        pwConfirmMessage.classList.remove("text-red-500");
        checkObj.newPwConfirm = true;
    } else {
        pwConfirmMessage.innerText = "비밀번호가 일치하지 않습니다.";
        pwConfirmMessage.classList.add("text-red-500");
        pwConfirmMessage.classList.remove("text-green-500");
        checkObj.newPwConfirm = false;
    }
}

// 3. 비밀번호 확인 필드 입력 시 실행
newPwConfirm.addEventListener("input", checkPwMatch);

function changePassword() {    
    // 현재 비밀번호 입력 여부만 체크 (실제 일치 여부는 서버에서 bcrypt로 체크)
    checkObj.currentPw = currentPw.value.trim().length > 0;

    if (!checkObj.currentPw) { alert("현재 비밀번호를 입력해주세요."); return; }
    if (!checkObj.newPw) { alert("새 비밀번호 형식이 올바르지 않습니다."); return; }
    if (!checkObj.newPwConfirm) { alert("새 비밀번호 확인이 일치하지 않습니다."); return; }

    fetch("/myPage/changePw", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            "currentPw": currentPw.value,
            "newPw": newPw.value
        })
    })
    .then(res => res.json())
    .then(data => {
        if(data.status === "success") {
            alert("비밀번호가 성공적으로 변경되었습니다.");
            location.reload();
        } else {
            alert("비밀번호 변경에 실패하였습니다.\n 사유 : " + data.message);
        }
    });
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-30
 * 마이페이지 - 회원탈퇴
 */
function deleteAccount(){
    if(!document.getElementById("agree-delete").checked){
        alert("위 내용에 동의하셔야 회원탈퇴가 가능합니다.");
        return;
    }

    const deletePW = document.getElementById("deletePW").value;

    if(deletePW.trim().length == 0) {
        alert("비밀번호를 입력해주세요.");
        return;
    }

    // 데이터를 담을 폼 객체 생성
    const formData = new FormData();
    formData.append("deletePW", deletePW); 

    fetch("/myPage/deleteMember", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if(data.status === "success"){
            alert("회원탈퇴가 성공적으로 완료되었습니다.\n이용해주셔서 감사합니다.");
            location.href = "/";
        } else {
            alert("회원탈퇴에 실패하였습니다.\n사유 : " + data.message);
        }
    });
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-30
 * 마이페이지 - 회원탈퇴 취소
 */
function cancelDeleteAccount() {
    document.getElementById("agree-delete").checked = false;
    document.getElementById("deletePW").value = "";
}