package controller;

import dao.EmailDAO;

public class AutoEmailSenderController {

    // ─── Confirmation Email ─────────────────────────────────────────────────────

    public static String generateEmailSubject() {
        return "Your 5BigGuys Theme Park Booking Confirmation";
    }

    public static String generateEmailBody(String ticketId,
                                           String ride,
                                           String date,
                                           String time,
                                           String adults,
                                           String seniors,
                                           String students,
                                           String families,
                                           String status) {
        return String.format(
                "Dear Guest,\n\n" +
                        "Thank you for your booking!\n\n" +
                        "Ticket Details:\n" +
                        "Ticket ID: %s\n" +
                        "Ride: %s\n" +
                        "Date: %s\n" +
                        "Time: %s\n" +
                        "Adults: %s\n" +
                        "Senior/Oku: %s\n" +
                        "Students: %s\n" +
                        "Family Packages: %s\n" +
                        "Status: %s\n\n" +
                        "We look forward to seeing you!\n\n" +
                        "Best regards,\n" +
                        "5BigGuys Theme Park",
                ticketId, ride, date, time, adults, seniors, students, families, status
        );
    }

    /**
     * Sends a booking confirmation email to the given address.
     * @return true if send succeeded, false otherwise.
     */
    public static boolean sendBookingEmail(String recipientEmail,
                                           String ticketId,
                                           String ride,
                                           String date,
                                           String time,
                                           String adults,
                                           String seniors,
                                           String students,
                                           String families,
                                           String status) {
        EmailDAO emailService = new EmailDAO();
        String subject = generateEmailSubject();
        String body    = generateEmailBody(ticketId, ride, date, time, adults, seniors, students, families, status);

        return emailService.sendConfirmationEmail(recipientEmail, subject, body, recipientEmail);
    }

    // ─── Status-Change Email ────────────────────────────────────────────────────

    /** Generate a subject line for a status-change notification */
    public static String generateStatusChangeSubject(String ticketId) {
        return String.format("Update on Your Booking (Ticket ID %s)", ticketId);
    }

    /**
     * Generate a body for a status-change notification.
     * Emphasizes the ticket ID and old → new status.
     */
    public static String generateStatusChangeBody(String ticketId,
                                                  String ride,
                                                  String oldStatus,
                                                  String newStatus) {
        return String.format(
                "Dear Guest,\n\n" +
                        "We wanted to let you know that your booking has been updated:\n\n" +
                        "Ticket ID: %s\n" +
                        "Ride: %s\n\n" +
                        "Previous status: %s\n" +
                        "Current  status: %s\n\n" +
                        "If you have any questions, feel free to reply to this email.\n\n" +
                        "Thank you,\n" +
                        "5BigGuys Theme Park Team",
                ticketId, ride, oldStatus, newStatus
        );
    }

    /**
     * Sends a status-change notification email.
     * @return true if send succeeded, false otherwise.
     */
    public static boolean sendStatusChangeEmail(String recipientEmail,
                                                String ticketId,
                                                String ride,
                                                String oldStatus,
                                                String newStatus) {
        EmailDAO emailService = new EmailDAO();
        String subject = generateStatusChangeSubject(ticketId);
        String body    = generateStatusChangeBody(ticketId, ride, oldStatus, newStatus);

        return emailService.sendConfirmationEmail(recipientEmail, subject, body, recipientEmail);
    }
}
