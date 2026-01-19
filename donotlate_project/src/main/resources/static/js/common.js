// Tailwind Config 설정
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

// 사이드바 토글 기능 (이벤트 위임 사용)
function initSidebarToggle() {
    function isMobile() {
        return window.innerWidth <= 768;
    }

    function toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        
        if (!sidebar) return;

        if (isMobile()) {
            // 모바일: 오버레이와 함께 토글
            sidebar.classList.toggle('sidebar-open');
            if (overlay) {
                overlay.classList.toggle('active');
            }
            // 사이드바가 열릴 때 body 스크롤 방지
            if (sidebar.classList.contains('sidebar-open')) {
                document.body.style.overflow = 'hidden';
            } else {
                document.body.style.overflow = '';
            }
        } else {
            // 데스크톱: 기존 방식 유지
            sidebar.classList.toggle('sidebar-closed');
        }
    }

    function closeSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        
        if (!sidebar) return;

        if (isMobile()) {
            sidebar.classList.remove('sidebar-open');
            if (overlay) {
                overlay.classList.remove('active');
            }
            document.body.style.overflow = '';
        }
    }

    // 토글 버튼 클릭 이벤트
    document.addEventListener('click', function(e) {
        const toggleBtn = e.target.closest('#sidebarToggle');
        if (toggleBtn) {
            e.preventDefault();
            toggleSidebar();
        }
    });

    // 오버레이 클릭 시 사이드바 닫기
    document.addEventListener('click', function(e) {
        const overlay = document.getElementById('sidebarOverlay');
        if (overlay && e.target === overlay) {
            closeSidebar();
        }
    });

    // 사이드바 링크 클릭 시 모바일에서 닫기
    document.addEventListener('click', function(e) {
        const sidebarLink = e.target.closest('.sidebar-link');
        if (sidebarLink && isMobile()) {
            setTimeout(closeSidebar, 100); // 약간의 지연으로 부드러운 전환
        }
    });

    // 창 크기 변경 시 사이드바 상태 초기화
    let resizeTimer;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('sidebarOverlay');
            
            if (isMobile()) {
                // 모바일로 변경 시 데스크톱 상태 초기화
                if (sidebar) {
                    sidebar.classList.remove('sidebar-closed');
                    sidebar.classList.remove('sidebar-open');
                    // 모바일에서는 기본적으로 닫힌 상태
                }
                if (overlay) {
                    overlay.classList.remove('active');
                }
                document.body.style.overflow = '';
            } else {
                // 데스크톱으로 변경 시 모바일 상태 초기화
                if (sidebar) {
                    sidebar.classList.remove('sidebar-open');
                    sidebar.style.transform = '';
                }
                if (overlay) {
                    overlay.classList.remove('active');
                }
                document.body.style.overflow = '';
            }
        }, 250);
    });
}

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

// 모달 열기/닫기 기능
function initModal() {
    function openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
            document.body.style.overflow = 'hidden';
        }
    }

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
            document.body.style.overflow = '';
        }
    }

    // 전역 함수로 등록
    window.openModal = openModal;
    window.closeModal = closeModal;

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function (event) {
        if (event.key === "Escape") {
            const modals = document.querySelectorAll('[id^="modal-"]');
            modals.forEach(modal => {
                if (!modal.classList.contains('hidden')) {
                    closeModal(modal.id);
                }
            });
        }
    });
}

// 초기화 함수들 실행
initSidebarToggle();
initModal();

// 마이페이지는 별도로 호출 필요 (initMyPageScrollMenu)
