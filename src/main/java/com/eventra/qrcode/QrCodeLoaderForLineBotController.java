package com.eventra.qrcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@RestController
@RequestMapping("/")
public class QrCodeLoaderForLineBotController {

	@GetMapping("/load-qrcode/{ticketCode}")
	public ResponseEntity<byte[]> getQrCode(@PathVariable String ticketCode) throws Exception {
	    String qrContent = "https://eventra.ddns.net/back-end/exhibitor/qrcode-validation?ticketCode=" + ticketCode;

	    QRCodeWriter writer = new QRCodeWriter();
	    BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

	    BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
	    for (int x = 0; x < 300; x++) {
	        for (int y = 0; y < 300; y++) {
	            qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
	        }
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(qrImage, "png", baos);

	    return ResponseEntity.ok()
	            .contentType(org.springframework.http.MediaType.IMAGE_PNG)
	            .body(baos.toByteArray());
	}

}
