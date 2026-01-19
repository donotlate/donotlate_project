/* =========================
    Tailwind Config
========================= */
if (typeof tailwind !== 'undefined') {
    tailwind.config = {
        theme: {
            extend: {
                fontFamily: { sans: ['Noto Sans KR', 'sans-serif'] },
                colors: { primary: '#4F46E5', secondary: '#06B6D4' }
            }
        }
    };
}

/* =========================
    사이드바 처리
========================= */
function initSidebarToggle() {
    function isMobile() {
        return window.innerWidth <= 768;
    }

    function toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        if (!sidebar) return;

        if (isMobile()) {
            sidebar.classList.toggle('sidebar-open');
            overlay?.classList.toggle('active');
            document.body.style.overflow =
                sidebar.classList.contains('sidebar-open') ? 'hidden' : '';
        } else {
            sidebar.classList.toggle('sidebar-closed');
        }
    }

    function closeSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        if (!sidebar) return;

        sidebar.classList.remove('sidebar-open');
        overlay?.classList.remove('active');
        document.body.style.overflow = '';
    }

    document.addEventListener('click', (e) => {
        if (e.target.closest('#sidebarToggle')) {
            e.preventDefault();
            toggleSidebar();
        }
        if (e.target.id === 'sidebarOverlay') {
            closeSidebar();
        }
        if (e.target.closest('.sidebar-link') && isMobile()) {
            setTimeout(closeSidebar, 100);
        }
    });
}

/* =========================
    모달처리
========================= */
function initModal() {
    function openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        modal.classList.add('hidden');
        document.body.style.overflow = '';
    }

    // 전역 노출 (HTML onclick용)
    window.openModal = openModal;
    window.closeModal = closeModal;

    // ESC 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        document.querySelectorAll('[id^="modal-"]:not(.hidden)')
            .forEach(modal => closeModal(modal.id));
    });

    // 로그인 모달 버튼 바인딩
    const authModal = document.getElementById('authModal');
    const loginForm = document.getElementById('loginForm');
    const loginBtns = ['loginBtn', 'loginBtn2']
        .map(id => document.getElementById(id))
        .filter(Boolean);

    loginBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            authModal?.classList.remove('hidden');
            loginForm?.classList.remove('hidden');
        });
    });

    // 로그인 모달 닫기 버튼
    const closeBtn = document.getElementById('closeModal');
    closeBtn?.addEventListener('click', () => {
        authModal?.classList.add('hidden');
        document.body.style.overflow = '';
    });

    // footer 약관
    document.getElementById('btn-terms')
        ?.addEventListener('click', e => {
            e.preventDefault();
            openModal('modal-terms');
        });

    document.getElementById('btn-privacy')
        ?.addEventListener('click', e => {
            e.preventDefault();
            openModal('modal-privacy');
        });
}

/* =========================
    회원가입 -> 로그인
========================= */
function initAuthRedirect() {
    const showSignup = document.getElementById('showSignup');
    const signupBtn = document.getElementById('signupBtn');

    showSignup?.addEventListener('click', () => {
        location.href = '/signUp';
    });

    signupBtn?.addEventListener('click', () => {
        location.href = '/signUp';
    });

    // ?login=true 처리
    const params = new URLSearchParams(location.search);
    if (params.get('login') === 'true') {
        document.getElementById('authModal')?.classList.remove('hidden');
        document.getElementById('loginForm')?.classList.remove('hidden');
    }
}

/* =========================
    파라미터로 넘겨졌을 경우, 초기화
========================= */
document.addEventListener('DOMContentLoaded', () => {
    initSidebarToggle();
    initModal();
    initAuthRedirect();
});


// 마이페이지 스크롤 활성 메뉴 기능
function initMyPageScrollMenu() {
    function init() {
        const HEADER_HEIGHT = 64;
        const toggleBtn = document.getElementById('sidebarToggle');
        const sidebar = document.getElementById('sidebar');
        const navLinks = document.querySelectorAll('#sidebar nav a');
        const sections = document.querySelectorAll('[id$="-section"]');

        // 사이드바 토글은 initSidebarToggle에서 이벤트 위임으로 처리됨

        // navLinks와 sections가 없으면 스크롤 메뉴 기능은 건너뛰기
        if (navLinks.length === 0 || sections.length === 0) {
            return;
        }

        // 클릭 시 스크롤 이동
        navLinks.forEach(link => {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                const targetId = this.getAttribute('href').substring(1) + '-section';
                const target = document.getElementById(targetId);
                if (!target) return;

                const y = window.pageYOffset + target.getBoundingClientRect().top - HEADER_HEIGHT;
                window.scrollTo({ top: y, behavior: 'smooth' });
            });
        });

        // 스크롤 위치에 따른 활성 메뉴 표시
        let ticking = false;
        function updateActiveMenu() {
            const scrollPos = window.scrollY + HEADER_HEIGHT + 8;
            const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
            let currentSection = null;

            sections.forEach(section => {
                if (section.offsetTop <= scrollPos) {
                    currentSection = section;
                }
            });

            if (window.scrollY >= maxScroll - 2) {
                currentSection = sections[sections.length - 1];
            }

            if (!currentSection) return;
            const activeId = currentSection.id.replace('-section', '');

            navLinks.forEach(link => {
                link.classList.remove('bg-primary', 'text-white');
                link.classList.add('text-gray-700');
                if (link.getAttribute('href') === `#${activeId}`) {
                    link.classList.add('bg-primary', 'text-white');
                    link.classList.remove('text-gray-700');
                }
            });
        }

        window.addEventListener('scroll', () => {
            if (!ticking) {
                window.requestAnimationFrame(() => {
                    updateActiveMenu();
                    ticking = false;
                });
                ticking = true;
            }
        });
        updateActiveMenu();
    }

    // DOM이 준비되면 즉시 실행
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        // DOM이 이미 로드된 경우 약간의 지연을 두어 완전히 준비되도록 함
        setTimeout(init, 0);
    }
}