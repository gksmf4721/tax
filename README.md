# 📝 과제 수행 보고서: 세금 TF 시스템 구현 결과

---

## 1. 🏗️ 아키텍처 및 기술 결정

| 구분                | 내용 | 구현 근거 / 설계 의사 결정 |
|-------------------|------|---------------------------|
| **아키텍처**          | 클린/헥사고날 아키텍처 도입 | 향후 MSA(마이크로서비스) 전환을 염두에 두고 비즈니스 로직(도메인)과 인프라(DB, 수집기)를 완전히 분리하여 유연성을 확보함. 도메인 간 결합도 최소화를 위해 JPA 외래 키(FK) 사용을 제한함. |
| **Kotlin/Spring** | JDK 17 (LTS), Spring Boot 3.5.7 | 장기 지원(LTS) 버전 채택 및 최신 Spring 버전 사용으로 안정성과 생산성을 확보함. |
| **DB/ORM**        | MySQL, JPA | 관계형 데이터베이스로 데이터 정합성을 확보하고, JPA를 통해 객체지향적인 영속성 관리를 구현함. |
| **동시성 제어**        | Redis (Redisson 분산 락) | 특정 사업장(`/api/businesses/{registrationNumber}/collections`)에 대한 동시 수집 요청을 방지하기 위해 Redisson 분산 락을 적용하여 데이터 정합성을 보장함. |

---

## 2. ⚡ 구현된 RESTful API 목록

| 엔드포인트 (URI) | HTTP Method | 기능 설명 | 경로/쿼리 파라미터 | 권한         |
|-----------------|-------------|----------|------------------|------------|
| `/api/businesses/{registrationNumber}/collections` | POST | 사업자번호를 받아 매출/매입 수집 요청을 접수합니다. Redis 락으로 동시 요청을 막고, 5분 후 비동기(TaskScheduler)로 수집 작업을 예약합니다. | `registrationNumber` (Path) | MANAGER 이상 |
| `/api/businesses/{registrationNumber}/collections` | GET | 요청된 사업장의 수집 상태를 조회합니다. 수집 상태는 `NOT_REQUESTED`, `COLLECTING`, `COLLECTED` 세 가지입니다. | `registrationNumber` (Path), `year`, `halfType` (Query) | MANAGER 이상 |
| `/api/admin/businesses/{businessId}/authorities` | POST | 새로운 관리자 권한을 추가합니다. (한 사업장 여러 관리자 가능) | `businessId` (Path) | ADMIN 전용   |
| `/api/admin/businesses/{businessId}/authorities` | GET | 특정 사업장의 권한 부여 유저 목록을 조회합니다. | `businessId` (Path) | ADMIN 전용   |
| `/api/admin/businesses/{businessId}/authorities/{userId}` | PUT | 특정 유저의 권한 활성화/비활성화를 처리합니다. | `businessId`, `userId` (Path) | ADMIN 전용   |
| `/api/admin/businesses/{businessId}/authorities/{userId}` | DELETE | 특정 유저의 권한을 삭제합니다. | `businessId`, `userId` (Path) | ADMIN 전용   |
| `/api/vats` | GET | 부가세 계산 결과를 조회합니다. (ADMIN은 전체, MANAGER는 권한 사업장만 조회) | `year`, `halfType` (Query) | MANAGER 이상 |

---

## 3. 📊 핵심 로직 및 설계 상세

### A. 부가세 계산 로직 구현

**계산 공식**
```
(매출 금액 합계 - 매입 금액 합계) × 1/11
```

**반올림 처리**
- 계산된 결과는 1의 자리에서 반올림하여 처리했습니다.
- 예시: `12345.12 → 12350`

**권한별 조회**
- **ADMIN**: 모든 사업장의 부가세 정보를 조회할 수 있습니다.
- **MANAGER**: 자신이 권한이 부여된 사업장의 정보만 조회할 수 있습니다.

---

### B. 수집 요청 / 수집기(CollectorClient) 비동기 처리

#### 1. API 서비스 내 상태 변경 (최초 상태)
- 요청을 받자마자 DB 상태를 `NOT_REQUESTED`(수집기가 요청을 받기 전 - 사용자로부터 API 요청만 받은 상태) 으로 즉시 변경합니다.


#### 2. 상태 변경 (수집기가 요청을 받은 상태)
- 요청을 받자마자 DB 상태를 `COLLECTING`으로 즉시 변경합니다.

#### 3. 논블로킹 예약
- 실제 수집 로직(데이터 읽기/DB 적재)은 `Thread.sleep()` 대신 **TaskScheduler**를 사용하여 5분 후에 실행되도록 예약합니다.
- 이는 API 요청 스레드를 5분 동안 점유하지 않고 효율적으로 자원을 사용하기 위한 설계 결정입니다.

#### 4. 작업 완료
- 작업이 완료되면 DB 상태를 `COLLECTED`으로 변경합니다.

#### + 데이터 소스
- 수집기는 테스트 목적으로 `resources/sample.xlsx` 파일을 읽어 데이터를 파싱하고 DB에 적재합니다.

---

### C. 도메인 간 경계 관리

`VatClient` (Port) & `VatClientAdapter` (Adapter) 패턴을 사용하여 `CollectionService`가 `VatService`에 직접적으로 의존하지 않도록 분리했습니다.

기능적으로 분리된 도메인 간의 깨끗한 통신 경로를 확보하기 위해 설계했습니다.
