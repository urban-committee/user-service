//package com.blogs.userservice.security.service;
//
//import com.blogs.userservice.repository.BlogUserRepository;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.util.Base64;
//
//@Service
//public class QRCodeService {
//
//    @Autowired
//    private BlogUserRepository userRepository;
//
//    public String generateQRCodeImage(String secret, String username) {
//        try {
//            String qrCodeContent = "otpauth://totp/" + username + "?secret=" + secret + "&issuer=YourApp";
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 250, 250);
//
//            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//            byte[] pngData = pngOutputStream.toByteArray();
//
//            return Base64.getEncoder().encodeToString(pngData);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
