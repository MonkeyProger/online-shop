package org.scenter.onlineshop.service.services;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final StockService stockService;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender, StockService stockService){
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.stockService = stockService;
    }

    @Value("${spring.mail.username}") private String sender;
    public String sendOrderToEmail(Set<SaleProduct> cart, Float total, String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        List<Product> productList = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        cart.forEach(saleProduct -> {
            Product product = stockService.getProductById(saleProduct.getProductId());
            productList.add(product);
            quantities.add(saleProduct.getAmount());
        });

        String description = createItemDescription(productList,quantities);

        helper.setFrom(sender);
        helper.setTo(email);
        helper.setSubject("Order confirmation");
        helper.setText(createEmailContent(description, total.toString()), true);

        try {
            javaMailSender.send(mimeMessage);
            return "Mail sent successfully!";
        }
        catch (Exception e) {
            return "Error while sending mail!";
        }
    }

    private String createItemDescription(List<Product> products, List<Integer> quantities){
        final String template = ": ${product}\nQuantity: ${quantity}\nPrice: ${price}\n\n";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < products.size(); i++){
            Map<String, Object> params = new HashMap<>();
            Product product = products.get(i);
            params.put("product", product.getDescription());
            params.put("quantity", quantities.get(i));
            params.put("price",product.getPrice());
            res.append("Good №").append(i+1).append(StrSubstitutor.replace(template, params, "${", "}"));
        }
        return res.toString();
    }
    private String createEmailContent(String description, String total) {
        Context context = new Context();
        context.setVariable("description", description);
        context.setVariable("total", total);

        return templateEngine.process("message-template", context);
    }
}
