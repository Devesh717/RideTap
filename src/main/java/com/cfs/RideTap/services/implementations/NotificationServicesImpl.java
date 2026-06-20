package com.cfs.RideTap.services.implementations;

import com.cfs.RideTap.dtos.eventDTO.BookingCancelledEvent;
import com.cfs.RideTap.dtos.eventDTO.BookingCreatedEvent;
import com.cfs.RideTap.services.interfaces.NotificationServices;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServicesImpl implements NotificationServices {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendBookingConfirmation(BookingCreatedEvent bookingCreatedEvent) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setTo(bookingCreatedEvent.getEmail());

            mimeMessageHelper.setSubject("RideTap Booking Confirmation");

            mimeMessageHelper.setText("""
                Hello %s,
                
                Your Booking is Confirmed.
                
                Booking Id: %d
                Route     : %s -> %s
                Fare      : ₹%.2f
                
                Your QR ticket is attached.
                
                Thank you for using RideTap.
                """
                    .formatted(
                            bookingCreatedEvent.getHolderName(),
                            bookingCreatedEvent.getBookingId(),
                            bookingCreatedEvent.getOriginStation(),
                            bookingCreatedEvent.getDestinationStation(),
                            bookingCreatedEvent.getFare()
                    ));

            FileSystemResource qrFile = new FileSystemResource(bookingCreatedEvent.getQrCodePath());

            mimeMessageHelper.addAttachment("RideTap-QR.png", qrFile);

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send booking email");
        }
    }

    @Override
    public void sendBookingCancellation(BookingCancelledEvent bookingCancelledEvent) {

        MimeMessage message =
                javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(
                    bookingCancelledEvent.getEmail());

            helper.setSubject(
                    "RideTap Booking Cancelled");

            helper.setText(
                    """
                    Hello %s,
    
                    Your booking has been cancelled.
    
                    Booking Id : %d
                    Route      : %s -> %s
    
                    Refunded Amount : ₹%.2f
    
                    The amount has been credited
                    back to your metro card balance.
    
                    Thank you for using RideTap.
                    """
                            .formatted(
                                    bookingCancelledEvent.getHolderName(),
                                    bookingCancelledEvent.getBookingId(),
                                    bookingCancelledEvent.getOriginStation(),
                                    bookingCancelledEvent.getDestinationStation(),
                                    bookingCancelledEvent.getRefundedAmount()
                            ));

            javaMailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException("Failed to send cancellation email");
        }
    }
}
