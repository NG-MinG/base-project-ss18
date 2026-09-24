package com.shopmart.payment.exception;

/** Lỗi nghiệp vụ (vd: không đủ tồn kho, không đủ số dư, trạng thái không hợp lệ). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
