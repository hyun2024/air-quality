import React, { useEffect } from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
    useEffect(() => {
        // 네이버 지도 API 스크립트 로드
        const script = document.createElement('script');
        script.src = "https://oapi.map.naver.com/openapi/v3/maps.js?ncpClientId=32cw0xhbvo";
        script.async = true;
        script.onload = () => {
            console.log("Naver Map script loaded");
            initMap(); // 스크립트 로드 후 지도 초기화
        };
        document.head.appendChild(script);
    }, []);

    const initMap = () => {
        // 초기 위치 설정
        const position = new window.naver.maps.LatLng(37.3595704, 127.105399);
        console.log("Initial position:", position);

        // 지도 옵션 설정
        const mapOptions = {
            center: position,
            zoom: 15,
            scaleControl: false,
            logoControl: false,
            mapDataControl: false,
            minZoom: 6,
            mapTypeControl: true,
            mapTypeControlOptions: {
                style: window.naver.maps.MapTypeControlStyle.BUTTON,
                position: window.naver.maps.Position.TOP_RIGHT
            },
            zoomControl: true,
            zoomControlOptions: {
                style: window.naver.maps.ZoomControlStyle.SMALL,
                position: window.naver.maps.Position.TOP_RIGHT
            }
        };

        // 지도 생성
        const map = new window.naver.maps.Map('map', mapOptions);
        console.log("Map initialized:", map);

        // 대기질 데이터와 측정소 데이터 가져오기
        Promise.all([
            fetch('http://localhost:8945/api/air-quality/latest')
                .then(response => {
                    console.log("Air quality response:", response);
                    return response.text();
                })
                .then(text => {
                    console.log("Air quality text:", text);
                    return JSON.parse(text);
                })
                .then(data => {
                    console.log("Air quality data:", data);
                    return data;
                })
                .catch(error => {
                    console.error('Error fetching air quality data:', error);
                }),
            fetch('http://localhost:8945/api/stations')
                .then(response => {
                    console.log("Station response:", response);
                    return response.json();
                })
                .then(data => {
                    console.log("Station data:", data);
                    return data;
                })
                .catch(error => {
                    console.error('Error fetching station data:', error);
                })
        ])
        .then(data => {
            const airQualityData = data[0];
            const stationData = data[1];
            console.log("Air Quality Data:", airQualityData);
            console.log("Station Data:", stationData);

            // PM10 값에 따른 색상 결정 함수 추가
            const getPM10Color = (pm10Value) => {
                if (!pm10Value) return '#808080';  // 데이터 없음
                const value = parseInt(pm10Value);
                if (value <= 30) return '#2196F3';      // 좋음 - 파랑
                if (value <= 80) return '#21B6A8';      // 보통 - 초록
                if (value <= 150) return '#FFB300';     // 나쁨 - 노랑
                return '#FF5252';                       // 매우나쁨 - 빨강
            };

            // 마커 스타일 생성 함수
            const createMarkerIcon = (pm10Value) => {
                const color = getPM10Color(pm10Value);
                return {
                    content: `
                        <div style="
                            position: relative;
                            width: 24px;
                            height: 24px;
                        ">
                            <div style="
                                position: absolute;
                                top: 0;
                                left: 0;
                                width: 24px;
                                height: 24px;
                                background-color: ${color};
                                border: 2px solid white;
                                border-radius: 50%;
                                box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                                cursor: pointer;
                                transition: transform 0.2s;
                            "></div>
                        </div>
                    `,
                    anchor: new window.naver.maps.Point(12, 12)
                };
            };

            // 측정소 마커 추가
            stationData.forEach(station => {
                if (!station.dmX || !station.dmY) {
                    console.log(`Skipping station ${station.stationName} due to missing coordinates`);
                    return;
                }
                
                // 해당 측정소의 대기질 데이터 찾기
                const airQualityInfo = airQualityData.find(item => 
                    item.station.stationName === station.stationName
                );
                
                const stationMarker = new window.naver.maps.Marker({
                    position: new window.naver.maps.LatLng(parseFloat(station.dmY), parseFloat(station.dmX)),
                    map: map,
                    icon: createMarkerIcon(airQualityInfo?.pm10Value)
                });

                const stationInfoWindow = new window.naver.maps.InfoWindow({
                    content: `
                        <div style="
                            padding: 15px;
                            min-width: 200px;
                            text-align: center;
                            border-radius: 8px;
                            background-color: white;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
                        ">
                            <h3 style="
                                margin: 0 0 10px 0;
                                color: #333;
                                font-size: 16px;
                            ">${station.stationName}</h3>
                            ${airQualityInfo ? `
                                <div style="
                                    margin: 8px 0;
                                    padding: 8px;
                                    background-color: ${getPM10Color(airQualityInfo.pm10Value)}20;
                                    border-radius: 4px;
                                ">
                                    <div style="color: #666;">미세먼지 (PM10)</div>
                                    <div style="
                                        font-size: 18px;
                                        font-weight: bold;
                                        color: ${getPM10Color(airQualityInfo.pm10Value)};
                                    ">${airQualityInfo.pm10Value} ㎍/㎥</div>
                                </div>
                                <div style="
                                    margin: 8px 0;
                                    padding: 8px;
                                    background-color: #f5f5f5;
                                    border-radius: 4px;
                                ">
                                    <div style="color: #666;">초미세먼지 (PM2.5)</div>
                                    <div style="font-size: 16px;">${airQualityInfo.pm25Value} ㎍/㎥</div>
                                </div>
                            ` : '<p style="color: #666;">데이터 없음</p>'}
                            <div style="
                                margin-top: 10px;
                                font-size: 12px;
                                color: #888;
                            ">${station.addr}</div>
                        </div>
                    `,
                    backgroundColor: "transparent",
                    borderWidth: 0,
                    disableAnchor: true,
                    pixelOffset: new window.naver.maps.Point(0, -10)
                });

                window.naver.maps.Event.addListener(stationMarker, 'click', function() {
                    stationInfoWindow.open(map, stationMarker);
                });
            });

            // 대기질 데이터 마커 추가
            airQualityData.forEach(item => {
                const airQualityMarker = new window.naver.maps.Marker({
                    position: new window.naver.maps.LatLng(item.station.dmY, item.station.dmX),
                    map: map
                });

                const airQualityInfoWindow = new window.naver.maps.InfoWindow({
                    content: `<div style="width:150px;text-align:center;padding:10px;">
                                <strong>${item.station.stationName}</strong><br>
                                PM10: ${item.pm10Value}<br>
                                PM2.5: ${item.pm25Value}
                               </div>`
                });

                window.naver.maps.Event.addListener(airQualityMarker, 'click', function() {
                    airQualityInfoWindow.open(map, airQualityMarker.getPosition());
                });
            });
        })
        .catch(error => {
            console.error('Error processing data:', error);
        });
    };

    return (
        <div>
            <h1>대기질 예보통보 조회</h1>
            <div id="map" style={{ width: '100%', height: '1000px' }}></div>
        </div>
    );
}

export default App;
