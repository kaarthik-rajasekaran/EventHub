package com.eventhub.eventhubbackend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendTicketEmail(
            String to,
            String eventTitle,
            String venue,
            String eventDate,
            Integer quantity,
            String imageUrl,
            Long bookingId) {

        try {

            String ticketUrl =
                    "http://localhost:4200/ticket/" + bookingId;

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true);

            helper.setTo(to);

            helper.setSubject(
                    "🎟 EventHub Ticket - " + eventTitle);

            String html =
                    """
                    <html>
                    <body style="
                        font-family:Arial,sans-serif;
                        background:#f5f7fb;
                        padding:30px;
                    ">

                    <div style="
                        max-width:700px;
                        margin:auto;
                        background:white;
                        border-radius:18px;
                        overflow:hidden;
                        box-shadow:0 6px 18px rgba(0,0,0,0.12);
                    ">

                        <div style="
                            background:#4f46e5;
                            color:white;
                            padding:25px;
                            text-align:center;
                        ">

                            <h1 style="margin:0;">
                                🎟 EventHub Ticket
                            </h1>

                            <p style="
                                margin-top:10px;
                                opacity:0.9;
                            ">
                                Your booking is confirmed
                            </p>

                        </div>

                        <img
                            src="
                    """
                            + imageUrl +
                            """
                                    "
                                    style="
                                        width:100%;
                                        max-height:300px;
                                        object-fit:cover;
                                    ">

                                <div style="
                                    padding:30px;
                                ">

                                    <div style="
                                        background:#ecfeff;
                                        border:2px solid #06b6d4;
                                        border-radius:12px;
                                        padding:15px;
                                        margin-bottom:20px;
                                        text-align:center;
                                        font-weight:bold;
                                        color:#0f766e;
                                    ">
                                        ✅ VALID TICKET
                                    </div>

                                    <h2>
                            """
                            + eventTitle +
                            """
                                    </h2>

                                    <p>
                                        📍 <strong>Venue:</strong>
                            """
                            + venue +
                            """
                                    </p>

                                    <p>
                                        📅 <strong>Date:</strong>
                            """
                            + eventDate +
                            """
                                    </p>

                                    <p>
                                        🎟 <strong>Quantity:</strong>
                            """
                            + quantity +
                            """
                                    </p>

                                    <p>
                                        🔖 <strong>Booking ID:</strong>
                            """
                            + bookingId +
                            """
                                    </p>

                                    <hr style="
                                        margin:25px 0;
                                    ">

                                    <div style="
                                        text-align:center;
                                    ">

                                        <a href="
                            """
                            + ticketUrl +
                            """
                                        "
                                        style="
                                            display:inline-block;
                                            background:#4f46e5;
                                            color:white;
                                            text-decoration:none;
                                            padding:14px 24px;
                                            border-radius:10px;
                                            font-weight:bold;
                                        ">
                                            View Ticket
                                        </a>

                                    </div>

                                    <p style="
                                        color:#666;
                                        margin-top:30px;
                                        text-align:center;
                                    ">
                                        Please keep this email safe.
                                    </p>

                                </div>

                                <div style="
                                    background:#f3f4f6;
                                    padding:18px;
                                    text-align:center;
                                    color:#666;
                                    font-size:12px;
                                ">
                                    © EventHub • Smart Event Ticketing Platform
                                </div>

                            </div>

                            </body>
                            </html>
                            """;

            helper.setText(
                    html,
                    true);

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Async
    public void sendPasswordResetEmail(
            String to,
            String resetToken) {

        try {

            String resetUrl =
                    "http://localhost:4200/reset-password?token="
                            + resetToken;

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true);

            helper.setTo(to);

            helper.setSubject(
                    "🔐 EventHub Password Reset");

            String html =
                    """
                    <html>
                    <body style="
                        font-family:Arial,sans-serif;
                        background:#f5f7fb;
                        padding:30px;
                    ">

                    <div style="
                        max-width:600px;
                        margin:auto;
                        background:white;
                        border-radius:18px;
                        overflow:hidden;
                        box-shadow:0 6px 18px rgba(0,0,0,0.12);
                    ">

                        <div style="
                            background:#dc2626;
                            color:white;
                            padding:25px;
                            text-align:center;
                        ">
                            <h1>
                                🔐 Password Reset
                            </h1>
                        </div>

                        <div style="
                            padding:30px;
                        ">

                            <p>
                                We received a request to reset your
                                EventHub password.
                            </p>

                            <p>
                                Click the button below to create
                                a new password.
                            </p>

                            <div style="
                                text-align:center;
                                margin:30px 0;
                            ">

                                <a href="
                    """
                            + resetUrl +
                            """
                                "
                                style="
                                    display:inline-block;
                                    background:#dc2626;
                                    color:white;
                                    text-decoration:none;
                                    padding:14px 24px;
                                    border-radius:10px;
                                    font-weight:bold;
                                ">
                                    Reset Password
                                </a>

                            </div>

                            <p>
                                This link expires in 30 minutes.
                            </p>

                            <p>
                                If you didn't request this,
                                simply ignore this email.
                            </p>

                        </div>

                    </div>

                    </body>
                    </html>
                    """;

            helper.setText(
                    html,
                    true);

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}