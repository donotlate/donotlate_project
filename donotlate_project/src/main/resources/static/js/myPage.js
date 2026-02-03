/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-29
 * 마이페이지 - 이름변경
 */
function changeName(){
    const changedName = document.getElementById("changedName").value;

    if(changedName.trim().length == 0) {
        alert("이름을 입력해주세요.");
        return;
    }
    
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
    .catch(err => {
        console.error("Error:", err);
        alert("서버 통신 중 오류가 발생했습니다.");
    });
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-02
 * 마이페이지 - 이름 변경 취소
 */
function cancelNameChange(){
    document.getElementById("changedName").value = "";
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

//2026-02-03 유건우 수정 - 소셜 계정은 비밀번호 변경 비활성화
if (currentPw) {
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
}

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
    })
    .catch(err => {
        console.error("Error:", err);
        alert("서버 통신 중 오류가 발생했습니다.");
    });
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-02
 * 마이페이지 - 비밀번호 취소
 */
function cancelPasswordChange(){
    currentPw.value = "";
    newPw.value = "";
    newPwConfirm.value = "";
    pwMessage.innerText = "";
    pwConfirmMessage.innerText = "";
    checkObj.currentPw = false;
    checkObj.newPw = false;
    checkObj.newPwConfirm = false;
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
    })
    .catch(err => {
        console.error("Error:", err);
        alert("서버 통신 중 오류가 발생했습니다.");
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

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-03
 * 마이페이지 - 최초 로딩 시, 프로필 사진이 있다면 삭제 버튼 활성화
 */
window.onload = function() {    
    if (document.getElementById("profilePreview").style.visibility == '') {
        document.getElementsByClassName("profile-delete-icon")[0].style.display = "block";
    }
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-02
 * 마이페이지 - 프로필 사진 미리보기
 */
function previewImage(input){
    const file = input.files[0];
    if (!file) return;

    // 이미지 파일인지 확인
    if (!file.type.startsWith("image/")) {
        alert("이미지 파일만 선택 가능합니다.");
        input.value = "";
        return;
    }
    
    // 구분값 변경 (변경)
    document.getElementById("profileImgStatus").value = "change";

    const preview = document.getElementById("profilePreview");
    const imageUrl = URL.createObjectURL(file);
    preview.src = imageUrl;

    //이미지 활성화
    document.getElementById("profilePreview").style.visibility = "visible";
    //삭제 버튼 활성화
    document.getElementsByClassName("profile-delete-icon")[0].style.display = "block";
    //기본 이미지 비활성화
    document.getElementById("default-profile").style.visibility = "hidden"
}

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-02
 * 마이페이지 - 프로필 이미지 삭제
 */
function deleteImage(){
    const preview = document.getElementById("profilePreview");
    preview.setAttribute("src", "");

    // 구분값 변경 (삭제)
    document.getElementById("profileImgStatus").value = "delete";

    //해당 영역이 보이면 흰 배경이 보이기에, 비활성화
    document.getElementById("profilePreview").style.visibility = "hidden";
    //삭제 버튼 비활성화
    document.getElementsByClassName("profile-delete-icon")[0].style.display = "none";
    //기본 이미지 활성화
    document.getElementById("default-profile").style.visibility = "visible";
};

/**
 * 작성자 : 유건우
 * 작성일 : 2026-02-02
 * 마이페이지 - 프로필 이미지 저장
 */
function saveProfileImage(){
    const formData = new FormData();
    
    const status = document.getElementById("profileImgStatus").value;
    const fileInput = document.getElementById("imageInput");
    const profileImg = fileInput.files[0];

    formData.append("status", status);
    if (profileImg) {
        formData.append("profileImg", profileImg);
    }
    
    fetch("/myPage/saveProfileImage", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if(data.status === "success"){
            alert("프로필 이미지가 성공적으로 저장되었습니다.");
            location.reload();
        }
        else {
            alert("프로필 이미지 저장에 실패하였습니다.\n사유 : " + data.message);
        }
    })
    .catch(err => {
        console.error("Error:", err);
        alert("서버 통신 중 오류가 발생했습니다.");
    });
}