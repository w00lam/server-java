# 성능 테스트 결과 및 배포 스펙 제안 보고서

## 1. 테스트 스크립트 (k6)

좌석 예약 시나리오를 바탕으로 작성된 부하 테스트 스크립트입니다. 50명의 가상 유저(VU)가 1~50번 좌석을 30초 동안 순회하며 예약을 시도합니다.

```JavaScript
import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
    stages: [
        {duration: '10s', target: 50},
        {duration: '20s', target: 50},
        {duration: '10s', target: 0},
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
        headers: {'Content-Type': 'application/json'},
    };

    const res = http.post(url, payload, params);

    check(res, {
        'is status 200 or 201': (r) => r.status === 200 || r.status === 201,
        'is status 409 (conflict)': (r) => r.status === 409,
    });

    sleep(1);
}
```

---

## 2. 테스트 수행 결과 요약애플리케이션에 할당된 리소스(CPU, Memory)를 단계적으로 늘려가며 성능 지표를 측정하였습니다.

| 테스트 항목           | Case 1 (0.5C/512M) | Case 2 (1.0C/1G) | Case 3 (2.0C/2G) |
|:-----------------|:-------------------|:-----------------|:-----------------|
| TPS (req/s)      | 36.71              | 37.06            | 36.78            |
| 성공 건수            | 50 / 1500          | 50 / 1495        | 50 / 1500        |
| 평균 응답 시간         | 19.34ms            | 24.02ms          | 18.48ms          |
| P95 응답 시간        | 27.72ms            | 28.78ms          | 23.63ms          |
| 실패율 (Status 500) | 96.66%             | 96.65%           | 96.66%           |

---

## 3. 결과 분석 및 적정 배포 스펙

### 🔍 결과 분석

- **성능 향상 미비 :** CPU와 메모리 자원을 4배(0.5 → 2.0) 증설했음에도 불구하고 TPS(초당 처리량)가 약 37/s 수준에서 유지되었습니다. 이는 현재 애플리케이션의 병목 지점이 시스템 자원보다는
  **DB 커넥션 풀(I/O)** 이나 좌석 선점을 위한 **DB Lock 대기 시간**에 있음을 나타냅니다.
- **Case 2의 지연 현상 :** Case 2에서 최대 응답 시간이 1.52s까지 튀는 현상이 관찰되었습니다. 이는 자원 할당량 변경 직후의 일시적 스파이크 혹은 JVM의 GC(Garbage Collection)
  영향으로 판단됩니다.
- **리소스 효율성 :** 가장 낮은 사양인 **Case 1**에서도 평균 19ms라는 매우 빠른 응답 속도로 50개의 예약 처리를 완료했습니다.
-

### 💡 최적 배포 스펙 제안: [CPU 0.5 / Memory 512M]

- **선정 근거 :** 테스트 결과 자원 증설에 따른 성능(TPS) 향상이 전혀 없었습니다. 따라서 현재 비즈니스 로직 수준에서는 불필요한 비용을 지출하지 않고, 최소 사양인 **0.5 Core / 512MB**
  환경에서 운영하는 것이 가장 효율적입니다.
- **향후 개선안 :** 자원을 늘리는 하드웨어적 접근보다는, 예외 처리 로직을 개선(500 Error → 409 Conflict)하고 DB 인덱스나 Lock 범위를 튜닝하여 비즈니스 로직 레벨의 병목을 해소하는
  것이 성능 향상에 더 효과적일 것으로 보입니다.