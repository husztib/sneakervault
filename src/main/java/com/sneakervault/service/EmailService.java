package com.sneakervault.service;

import com.sneakervault.model.Customer;
import com.sneakervault.model.OrderItem;
import com.sneakervault.model.OrderStatus;
import com.sneakervault.model.ShoeOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${sneakervault.mail.from:}")
    private String fromAddress;

    @Value("${sneakervault.mail.notify:}")
    private String notifyAddresses;

    @Value("${sneakervault.admin.url:}")
    private String adminUrl;

    @Value("${sneakervault.base.url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOrderConfirmation(ShoeOrder order) {
        boolean hu = "hu".equals(order.getLanguage());
        String customerEmail = order.getCustomerEmail();
        if (customerEmail != null && !customerEmail.isBlank()) {
            String subject = hu
                    ? "BotiX - #" + order.getId() + " Rendelés visszaigazolás"
                    : "BotiX - Order #" + order.getId() + " Confirmation";
            sendEmail(customerEmail, subject, buildHtml(order, hu));
        }
        sendNotification(order);
    }

    private void sendNotification(ShoeOrder order) {
        if (notifyAddresses == null || notifyAddresses.isBlank()) return;
        String subject = "BotiX - New Order #" + order.getId();
        String html = buildHtml(order, false);

        if (adminUrl != null && !adminUrl.isBlank()) {
            html = html.replace("BotiX &copy; 2026",
                    "<a href=\"" + adminUrl + "\" style=\"color:#ff4d00;text-decoration:none;font-weight:600;\">Open Admin Panel</a>"
                    + "<br>BotiX &copy; 2026");
        }

        for (String addr : notifyAddresses.split(",")) {
            String trimmed = addr.trim();
            if (!trimmed.isBlank()) {
                sendEmail(trimmed, subject, html);
            }
        }
    }

    private void sendEmail(String to, String subject, String html) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Email not sent (no from address configured): '{}' to {}", subject, to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Email not sent (no recipient): '{}'", subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Email sent: '{}' to {}", subject, to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(Customer customer, String activationBaseUrl) {
        String email = customer.getEmail();
        if (email == null || email.isBlank()) return;

        String link = activationBaseUrl + "/api/customers/activate?token=" + customer.getActivationToken();
        String subject = "BotiX - Fiók aktiválás / Account Activation";

        String html = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f5f5f5;\">"
                + "<div style=\"max-width:600px;margin:0 auto;background:#ffffff;\">"
                + "<div style=\"background:#0a0a0a;padding:24px 32px;text-align:center;\">"
                + "<h1 style=\"color:#fff;margin:0;font-size:24px;\">Boti<span style=\"color:#ff4d00;\">X</span></h1>"
                + "</div>"
                + "<div style=\"padding:32px;text-align:center;\">"
                + "<h2 style=\"color:#0a0a0a;margin:0 0 16px;\">Fi\u00f3k aktiv\u00e1l\u00e1s / Account Activation</h2>"
                + "<p style=\"color:#666;margin:0 0 24px;font-size:14px;\">Kattints az al\u00e1bbi gombra a fi\u00f3kod aktiv\u00e1l\u00e1s\u00e1hoz.<br>Click the button below to activate your account.</p>"
                + "<a href=\"" + esc(link) + "\" style=\"display:inline-block;padding:14px 32px;background:#ff4d00;color:#fff;text-decoration:none;border-radius:10px;font-weight:700;font-size:15px;\">Aktiv\u00e1l\u00e1s / Activate</a>"
                + "<p style=\"color:#999;margin-top:24px;font-size:12px;\">Ha nem te regisztr\u00e1lt\u00e1l, hagyd figyelmen k\u00edv\u00fcl ezt az emailt.<br>If you didn't register, please ignore this email.</p>"
                + "</div>"
                + "<div style=\"background:#f8f9fa;padding:16px 32px;text-align:center;color:#999;font-size:12px;\">"
                + "BotiX &copy; 2026"
                + "</div>"
                + "</div></body></html>";

        sendEmail(email, subject, html);
    }

    @Async
    public void sendOrderStatusEmail(ShoeOrder order) {
        String email = order.getCustomerEmail();
        if (email == null || email.isBlank()) return;

        boolean hu = "hu".equals(order.getLanguage());
        OrderStatus status = order.getStatus();

        String subject;
        String heading;
        String message;

        if (status == OrderStatus.CONFIRMED) {
            subject = hu ? "BotiX - #" + order.getId() + " Rendel\u00e9s meger\u0151s\u00edtve"
                         : "BotiX - Order #" + order.getId() + " Confirmed";
            heading = hu ? "Rendel\u00e9sed meger\u0151s\u00edtve!" : "Your order has been confirmed!";
            message = hu ? "A #" + order.getId() + " sz\u00e1m\u00fa rendel\u00e9sedet meger\u0151s\u00edtett\u00fck."
                         : "Your order #" + order.getId() + " has been confirmed.";
        } else if (status == OrderStatus.SHIPPED) {
            subject = hu ? "BotiX - #" + order.getId() + " Rendel\u00e9s kisz\u00e1ll\u00edtva"
                         : "BotiX - Order #" + order.getId() + " Shipped";
            heading = hu ? "Rendel\u00e9sed kisz\u00e1ll\u00edtva!" : "Your order has been shipped!";
            message = hu ? "A #" + order.getId() + " sz\u00e1m\u00fa rendel\u00e9sed \u00faton van hozz\u00e1d."
                         : "Your order #" + order.getId() + " is on its way to you.";
        } else {
            return;
        }

        String total = formatPrice(order.getTotalHUF(), order.getTotalEUR(), order.getCurrency());

        String html = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f5f5f5;\">"
                + "<div style=\"max-width:600px;margin:0 auto;background:#ffffff;\">"
                + "<div style=\"background:#0a0a0a;padding:24px 32px;text-align:center;\">"
                + "<h1 style=\"color:#fff;margin:0;font-size:24px;\">Boti<span style=\"color:#ff4d00;\">X</span></h1>"
                + "</div>"
                + "<div style=\"padding:32px;text-align:center;\">"
                + "<h2 style=\"color:#0a0a0a;margin:0 0 8px;\">" + heading + "</h2>"
                + "<p style=\"color:#666;margin:0 0 24px;font-size:14px;\">" + message + "</p>"
                + "<div style=\"background:#f8f9fa;border-radius:8px;padding:16px;display:inline-block;\">"
                + "<span style=\"font-size:14px;color:#666;\">" + (hu ? "\u00d6sszesen: " : "Total: ") + "</span>"
                + "<span style=\"font-size:20px;font-weight:800;color:#ff4d00;\">" + total + "</span>"
                + "</div>"
                + "</div>"
                + "<div style=\"background:#f8f9fa;padding:16px 32px;text-align:center;color:#999;font-size:12px;\">"
                + "BotiX &copy; 2026"
                + "</div>"
                + "</div></body></html>";

        sendEmail(email, subject, html);
    }

    private String buildHtml(ShoeOrder order, boolean hu) {
        String lblConfirmation = hu ? "Rendel\u00e9s visszaigazol\u00e1s" : "Order Confirmation";
        String lblOrder = hu ? "Rendel\u00e9s #" : "Order #";
        String lblProduct = hu ? "Term\u00e9k" : "Product";
        String lblSize = hu ? "M\u00e9ret" : "Size";
        String lblPrice = hu ? "\u00c1r" : "Price";
        String lblTotal = hu ? "\u00d6sszesen: " : "Total: ";
        String lblThankYou = hu ? "K\u00f6sz\u00f6nj\u00fck a rendel\u00e9sedet!" : "Thank you for your order!";

        StringBuilder items = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            items.append("<tr>")
                 .append("<td style=\"padding:10px 12px;border-bottom:1px solid #eee;\">").append(esc(item.getName())).append("</td>")
                 .append("<td style=\"padding:10px 12px;border-bottom:1px solid #eee;text-align:center;\">EU ").append(item.getSizeEUR()).append("</td>")
                 .append("<td style=\"padding:10px 12px;border-bottom:1px solid #eee;text-align:right;\">").append(formatPrice(item.getPrice(), item.getPriceEUR(), order.getCurrency())).append("</td>")
                 .append("</tr>");
        }

        String address = esc(order.getCustomerZip()) + " " + esc(order.getCustomerCity()) + ", " + esc(order.getCustomerStreet());
        String total = formatPrice(order.getTotalHUF(), order.getTotalEUR(), order.getCurrency());

        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f5f5f5;\">"
             + "<div style=\"max-width:600px;margin:0 auto;background:#ffffff;\">"
             + "<div style=\"background:#0a0a0a;padding:24px 32px;text-align:center;\">"
             + "<h1 style=\"color:#fff;margin:0;font-size:24px;\">Boti<span style=\"color:#ff4d00;\">X</span></h1>"
             + "</div>"
             + "<div style=\"padding:32px;\">"
             + "<h2 style=\"color:#0a0a0a;margin:0 0 8px;\">" + lblConfirmation + "</h2>"
             + "<p style=\"color:#666;margin:0 0 24px;\">" + lblOrder + order.getId() + "</p>"
             + "<div style=\"background:#f8f9fa;border-radius:8px;padding:16px;margin-bottom:24px;\">"
             + "<p style=\"margin:0 0 4px;font-weight:600;color:#0a0a0a;\">" + esc(order.getCustomerName()) + "</p>"
             + "<p style=\"margin:0 0 4px;color:#666;font-size:14px;\">" + address + "</p>"
             + "<p style=\"margin:0;color:#666;font-size:14px;\">" + esc(order.getCustomerPhone()) + "</p>"
             + "</div>"
             + "<table style=\"width:100%;border-collapse:collapse;margin-bottom:24px;\">"
             + "<thead><tr style=\"background:#f8f9fa;\">"
             + "<th style=\"padding:10px 12px;text-align:left;font-size:12px;color:#666;text-transform:uppercase;\">" + lblProduct + "</th>"
             + "<th style=\"padding:10px 12px;text-align:center;font-size:12px;color:#666;text-transform:uppercase;\">" + lblSize + "</th>"
             + "<th style=\"padding:10px 12px;text-align:right;font-size:12px;color:#666;text-transform:uppercase;\">" + lblPrice + "</th>"
             + "</tr></thead><tbody>"
             + items
             + "</tbody></table>"
             + "<div style=\"text-align:right;padding:16px;background:#f8f9fa;border-radius:8px;\">"
             + "<span style=\"font-size:14px;color:#666;\">" + lblTotal + "</span>"
             + "<span style=\"font-size:20px;font-weight:800;color:#ff4d00;\">" + total + "</span>"
             + "</div>"
             + "<p style=\"text-align:center;color:#666;margin-top:24px;font-size:14px;\">" + lblThankYou + "</p>"
             + "</div>"
             + "<div style=\"background:#f8f9fa;padding:16px 32px;text-align:center;color:#999;font-size:12px;\">"
             + "BotiX &copy; 2026"
             + "</div>"
             + "</div></body></html>";
    }

    private String formatPrice(int huf, int eur, String currency) {
        if ("EUR".equals(currency)) {
            return "&euro;" + eur;
        }
        return String.format("%,d", huf).replace(',', ' ') + " Ft";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
