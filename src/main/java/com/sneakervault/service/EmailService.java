package com.sneakervault.service;

import com.sneakervault.model.OrderItem;
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

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOrderConfirmation(ShoeOrder order) {
        boolean hu = "hu".equals(order.getLanguage());
        String customerEmail = order.getCustomerEmail();
        if (customerEmail != null && !customerEmail.isBlank()) {
            String subject = hu
                    ? "SneakerVault - #" + order.getId() + " Rendelés visszaigazolás"
                    : "SneakerVault - Order #" + order.getId() + " Confirmation";
            sendEmail(customerEmail, subject, buildHtml(order, hu));
        }
        sendNotification(order);
    }

    private void sendNotification(ShoeOrder order) {
        if (notifyAddresses == null || notifyAddresses.isBlank()) return;
        String subject = "SneakerVault - New Order #" + order.getId();
        String html = buildHtml(order, false);

        if (adminUrl != null && !adminUrl.isBlank()) {
            html = html.replace("SneakerVault &copy; 2026",
                    "<a href=\"" + adminUrl + "\" style=\"color:#e94560;text-decoration:none;font-weight:600;\">Open Admin Panel</a>"
                    + "<br>SneakerVault &copy; 2026");
        }

        for (String addr : notifyAddresses.split(",")) {
            String trimmed = addr.trim();
            if (!trimmed.isBlank()) {
                sendEmail(trimmed, subject, html);
            }
        }
    }

    private void sendEmail(String to, String subject, String html) {
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
             + "<div style=\"background:#1a1a2e;padding:24px 32px;text-align:center;\">"
             + "<h1 style=\"color:#fff;margin:0;font-size:24px;\">Sneaker<span style=\"color:#e94560;\">Vault</span></h1>"
             + "</div>"
             + "<div style=\"padding:32px;\">"
             + "<h2 style=\"color:#1a1a2e;margin:0 0 8px;\">" + lblConfirmation + "</h2>"
             + "<p style=\"color:#666;margin:0 0 24px;\">" + lblOrder + order.getId() + "</p>"
             + "<div style=\"background:#f8f9fa;border-radius:8px;padding:16px;margin-bottom:24px;\">"
             + "<p style=\"margin:0 0 4px;font-weight:600;color:#1a1a2e;\">" + esc(order.getCustomerName()) + "</p>"
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
             + "<span style=\"font-size:20px;font-weight:800;color:#e94560;\">" + total + "</span>"
             + "</div>"
             + "<p style=\"text-align:center;color:#666;margin-top:24px;font-size:14px;\">" + lblThankYou + "</p>"
             + "</div>"
             + "<div style=\"background:#f8f9fa;padding:16px 32px;text-align:center;color:#999;font-size:12px;\">"
             + "SneakerVault &copy; 2026"
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
