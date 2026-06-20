package com.cfs.RideTap.services.implementations;

import com.cfs.RideTap.services.interfaces.QRServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
@Log4j2
public class QRServicesImpl implements QRServices {

    private static final String QR_DIRECTORY = "qrCodes/";

    public String generateQRCode(Long bookingId) {
        try {
            String data = "{\"bookingId\":" + bookingId + "}";

            int width = 300;
            int height = 300;

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            File directory = new File(QR_DIRECTORY);

            if(!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = QR_DIRECTORY + "booking_" + bookingId + ".png";

            MatrixToImageWriter.writeToPath(
                    bitMatrix,
                    "PNG",
                    Paths.get(filePath)
            );

            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("QR Generation Failed");
        }
    }
}
