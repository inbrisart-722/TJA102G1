package com.eventra.qrcode;

public class QrCodeValidationResponse {

    private final QrCodeValidationResult result;
    private final QrCodeDTO data;

    public QrCodeValidationResponse(QrCodeValidationResult result, QrCodeDTO data) {
        this.result = result;
        this.data = data;
    }

    public QrCodeValidationResult getResult() {
        return result;
    }

    public QrCodeDTO getData() {
        return data;
    }

    public static QrCodeValidationResponse of(QrCodeValidationResult result) {
        return new QrCodeValidationResponse(result, null);
    }

    public static QrCodeValidationResponse success(QrCodeDTO data) {
        return new QrCodeValidationResponse(QrCodeValidationResult.SUCCESS, data);
    }
}
