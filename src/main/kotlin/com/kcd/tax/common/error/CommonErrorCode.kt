package com.kcd.tax.common.error

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    val code: Int,
    val message: String,
    val status: HttpStatus
) {

    // common
    UNKNOWN_INTERNAL_ERROR(10000, "알 수 없는 서버 에러.", HttpStatus.INTERNAL_SERVER_ERROR),

    // db
    DATA_TOO_LONG(10001, "데이터 허용 범위를 벗어났습니다.", HttpStatus.CONFLICT),

    // service error
    NOT_FOUND_USER(11001, "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_AUTHORITY(11002, "권한 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_HAVE_AUTHORITY(11003, "사업장 권한 조회 및 변경 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND_BUSINESS(11004, "사업장 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_VAT_PERIOD(11005, "부가세 조회 기간을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_COLLECTION_REQUEST(11006, "수집 요청 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_COLLECTING(11007, "이미 수집 요청에 따라 작업 진행 중입니다.", HttpStatus.CONFLICT),
    FAIL_COLLECTION(11008, "매출/매입 정보 수집에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
}