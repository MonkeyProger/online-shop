package org.scenter.onlineshop.service.services;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final StockService stockService;

    public EmailService(JavaMailSender javaMailSender, StockService stockService){
        this.javaMailSender = javaMailSender;
        this.stockService = stockService;
    }

    @Value("${spring.mail.username}") private String sender;
    StringBuilder header = new StringBuilder()
            .append("Dear customer,\n\n")
            .append("We’re happy to let you know that we’ve received your order.\n")
            .append("Your order details can be found below.\n");

    StringBuilder footer = new StringBuilder()
            .append("Workshop Team\n")
            .append("This is an automated message, please do not reply.\n");

    public String sendOrderToEmail(Set<SaleProduct> cart, float total, String email){
        SimpleMailMessage mailMessage
                = new SimpleMailMessage();
        List<Product> productList = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        cart.forEach(saleProduct -> {
            Product product = stockService.getProductById(saleProduct.getProductId());
            productList.add(product);
            quantities.add(saleProduct.getAmount());
        });

        mailMessage.setFrom(sender);
        mailMessage.setTo(email);
        mailMessage.setText(header +
                createItemDescription(productList,quantities) +
                "Total order price:" + total + "\n\n" +
                footer);
        mailMessage.setSubject("Order confirmation");
        try {
            javaMailSender.send(mailMessage);
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
}
