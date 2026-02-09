window.addEventListener('load', function() {
    loadTopChartData();
    loadChartData();
    loadMetroData();
});

/*
    작성자 : 유건우
    작성일자 : 2026-02-07
    최상단 차트 로드 및 시각화
*/
function loadTopChartData(){
    fetch("/chart/topChart")
        .then(res => res.json())
        .then(data => {
            const userCount = document.getElementById("userCount");
            const avgPushTime = document.getElementById("avgPushTime");
            const avgPrepareTime = document.getElementById("avgPrepareTime");
            const avgTrafficTime = document.getElementById("avgTrafficTime");

            userCount.innerText = data.userCount + "명";
            avgPushTime.innerText = data.avgPushTime;
            avgPrepareTime.innerText = data.avgPrepareTime + "분";
            avgTrafficTime.innerText = data.avgTrafficTime + "분";
        })
        .catch(err => console.log(err));
}

/*
    작성자 : 유건우
    작성일자 : 2026-02-09
    요일별 지하철 이용자 수 차트 로드 및 시각화
*/
function loadMetroData() {
    const chartDiv = document.getElementById('metro-chart');

    fetch('/chart/subway-weekly')
        .then(res => res.json())
        .then(serverData => {
            const maxVal = Math.max(...serverData);
            const minVal = Math.min(...serverData);

            const trace = {
                x: getFormattedDay(),
                y: serverData,
                type: 'scatter',
                mode: 'lines+markers+text',
                text: serverData.map(v => (v / 10000).toFixed(0) + '만'), // 소수점 제거로 더 깔끔하게
                textposition: 'top center',
                line: { color: '#EF4444', width: 3, shape: 'spline' },
                fill: 'tozeroy',
                fillcolor: 'rgba(239, 68, 68, 0.1)',
                hovertemplate: '%{y:,.0f}명<extra></extra>'
            };

            const layout = {
                margin: { t: 20, r: 30, b: 40, l: 60 }, // 여백 최적화
                xaxis: { 
                    tickmode: 'linear', 
                    range: [-0.3, 6.3], // 잘림 방지 최소 범위
                    fixedrange: true 
                },
                yaxis: { 
                    range: [minVal * 0.85, maxVal * 1.15],
                    tickformat: ',', 
                    automargin: true,
                    fixedrange: true
                },
                plot_bgcolor: '#FFFFFF',
                paper_bgcolor: '#FFFFFF'
            };

            Plotly.newPlot(chartDiv, [trace], layout, { responsive: true, displayModeBar: false });
            document.getElementById('metro-title').innerText = '요일별 지하철 이용자 수' + ' (' + getFormattedDate() + ')';
        })
        .catch(err => {
            chartDiv.innerHTML = `<p style="text-align:center; padding:50px; color:#EF4444;">로딩 에러</p>`;
        });
}

/*
    작성자 : 유건우
    작성일자 : 2026-02-09
    요일 표기
*/
function getFormattedDay() {
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    let result = [];
    let today = new Date();
    for (let i = 6; i >= 0; i--) {
        let d = new Date();
        d.setDate(today.getDate() - 8 - i);
        result.push(dayNames[d.getDay()]);
    }
    return result;
}

/*
    작성자 : 유건우
    작성일자 : 2026-02-09
    일자 표기
*/
function getFormattedDate() {
    let today = new Date();
    
    // 시작일 (8일 전 + 6일 전 = 14일 전)
    let startDate = new Date();
    startDate.setDate(today.getDate() - 14);
    
    // 종료일 (8일 전)
    let endDate = new Date();
    endDate.setDate(today.getDate() - 8);

    // MM.DD 형식으로 변환
    const formatDate = (d) => `${d.getMonth() + 1}.${d.getDate()}`;
    
    return `${formatDate(startDate)} ~ ${formatDate(endDate)}`;
}

/*
    작성자 : 유건우
    작성일자 : 2026-02-09
    추후 수정 - 하드코딩 영역
*/
function loadChartData(){
    try {
        var busData = [{
            type: 'scatter',
            mode: 'lines',
            x: ['      월', '화', '수', '목', '금', '토', '일'],
            y: [12, 22, 25, 12, 23, 15, 12],
            line: { color: '#7877C6', width: 3 },
            fill: 'tozeroy',
            fillcolor: 'rgba(120, 119, 198, 0.1)'
        }];
        
        var busLayout = {
            margin: { t: 20, r: 20, b: 40, l: 50 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { title: '요일', showgrid: false },
            yaxis: { title: '위험도 (%)', showgrid: true, gridcolor: '#F3F4F6' },
            showlegend: false
        };
        
        Plotly.newPlot('bus-chart', busData, busLayout, {responsive: true, displayModeBar: false, displaylogo: false});

        var transferRoutesData = [{
            type: 'bar',
            orientation: 'h',
            y: ['2호선', '9호선', '1호선', '3호선', '4호선', '7호선', '5호선', '6호선', '8호선', '경의선'],
            x: [1847, 1632, 1489, 1356, 1243, 1128, 987, 856, 734, 623],
            marker: { color: ['#3B82F6', '#10B981', '#EF4444', '#F59E0B', '#8B5CF6', '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1'] }
        }];
        
        var transferRoutesLayout = {
            margin: { t: 20, r: 20, b: 40, l: 80 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { title: '환승 횟수', showgrid: true, gridcolor: '#F3F4F6' },
            yaxis: { showgrid: false },
            showlegend: false
        };
        
        Plotly.newPlot('transfer-routes-chart', transferRoutesData, transferRoutesLayout, {responsive: true, displayModeBar: false, displaylogo: false});

        var distanceData = [{
            type: 'bar',
            x: ['강남-양재', '서울역-용산', '신도림-구로', '잠실-송파', '홍대-신촌', '광화문-종로', '역삼-선릉', '사당-방배'],
            y: [3.2, 2.8, 2.6, 2.4, 2.2, 2.0, 1.9, 1.7],
            marker: { color: '#F59E0B' }
        }];
        
        var distanceLayout = {
            margin: { t: 20, r: 20, b: 80, l: 50 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { tickangle: -45, showgrid: false },
            yaxis: { title: '거리 (km)', showgrid: true, gridcolor: '#F3F4F6' },
            showlegend: false
        };
        
        Plotly.newPlot('distance-chart', distanceData, distanceLayout, {responsive: true, displayModeBar: false, displaylogo: false});

        var stationUsersData = [{
            type: 'bar',
            x: ['강남역', '서울역', '잠실역', '신도림역', '홍대입구역', '사당역', '왕십리역', '구로디지털단지역'],
            y: [2847, 2456, 2134, 1987, 1845, 1723, 1598, 1467],
            marker: { color: '#3B82F6' }
        }];
        
        var stationUsersLayout = {
            margin: { t: 20, r: 20, b: 80, l: 50 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { tickangle: -45, showgrid: false },
            yaxis: { title: '출근 인원', showgrid: true, gridcolor: '#F3F4F6' },
            showlegend: false
        };
        
        Plotly.newPlot('attendance-chart', stationUsersData, stationUsersLayout, {responsive: true, displayModeBar: false, displaylogo: false});

        var leaveWorkData = [{
            type: 'bar',
            x: ['강남역', '서울역', '잠실역', '신도림역', '홍대입구역', '사당역', '왕십리역', '구로디지털단지역'],
            y: [2847, 2456, 2134, 1987, 1845, 1723, 1598, 1467],
            marker: { color: '#EF4444' }
        }];
        
        var leaveWorkLayout = {
            margin: { t: 20, r: 20, b: 80, l: 50 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { tickangle: -45, showgrid: false },
            yaxis: { title: '퇴근 인원', showgrid: true, gridcolor: '#F3F4F6' },
            showlegend: false
        };
        
        Plotly.newPlot('leave-work-chart', leaveWorkData, leaveWorkLayout, {responsive: true, displayModeBar: false, displaylogo: false});

        var hourlyPatternData = [
            {
                type: 'scatter',
                mode: 'lines',
                name: '월요일',
                x: ['05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00'],
                y: [234, 567, 1234, 2456, 1987, 845, 456, 234],
                line: { color: '#3B82F6', width: 2 }
            },
            {
                type: 'scatter',
                mode: 'lines',
                name: '화요일',
                x: ['05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00'],
                y: [245, 589, 1289, 2512, 2034, 867, 478, 256],
                line: { color: '#10B981', width: 2 }
            },
            {
                type: 'scatter',
                mode: 'lines',
                name: '수요일',
                x: ['05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00'],
                y: [256, 612, 1345, 2589, 2123, 901, 501, 278],
                line: { color: '#F59E0B', width: 2 }
            },
            {
                type: 'scatter',
                mode: 'lines',
                name: '목요일',
                x: ['05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00'],
                y: [267, 634, 1401, 2634, 2178, 934, 523, 289],
                line: { color: '#EF4444', width: 2 }
            },
            {
                type: 'scatter',
                mode: 'lines',
                name: '금요일',
                x: ['05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00'],
                y: [278, 656, 1456, 2712, 2234, 967, 545, 301],
                line: { color: '#8B5CF6', width: 2 }
            }
        ];
        
        var hourlyPatternLayout = {
            margin: { t: 20, r: 20, b: 60, l: 60 },
            plot_bgcolor: '#FFFFFF',
            paper_bgcolor: '#FFFFFF',
            xaxis: { title: '시간', showgrid: false },
            yaxis: { title: '출근 인원', showgrid: true, gridcolor: '#F3F4F6' },
            showlegend: true,
            legend: { x: 1, xanchor: 'right', y: 1 }
        };
        
        Plotly.newPlot('hourly-pattern-chart', hourlyPatternData, hourlyPatternLayout, {responsive: true, displayModeBar: false, displaylogo: false});

    } catch(e) {
        console.error('Chart rendering error:', e);
    }
}