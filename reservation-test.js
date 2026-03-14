import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 50 },
        { duration: '20s', target: 50 },
        { duration: '10s', target: 0 },
    ],
};

export default function () {
    const url = 'http://localhost:8080/reservations';

    // 가상 사용자(101~150)와 좌석(1~50) UUID 패턴 생성
    const userSuffix = String(__VU + 100).padStart(12, '0');
    const seatSuffix = String(__VU).padStart(12, '0');

    const payload = JSON.stringify({
        // DTO 필드명에 정확히 맞춤
        userId: `00000000-0000-0000-0000-${userSuffix}`,
        concertId: "00000000-0000-0000-0000-000000000002", // ConcertDate ID
        seatId: `00000000-0000-0000-0001-${seatSuffix}`    // 좌석 UUID
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'is status 200 or 201': (r) => r.status === 200 || r.status === 201,
        'is status 409 (conflict)': (r) => r.status === 409,
    });

    sleep(1);
}