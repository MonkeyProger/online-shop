package org.scenter.onlineshop.services;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {
    private JavaMailSender javaMailSender;
    private ProductRepo productRepo;

    public EmailService(JavaMailSender javaMailSender, ProductRepo productRepo){
        this.javaMailSender = javaMailSender;
        this.productRepo = productRepo;
    }
    @Value("${spring.mail.username}") private String sender;
    String header = """
                Dear customer,

                We’re happy to let you know that we’ve received your order.
                Your order details can be found below.
                
                """;
    String footer = """
                Workshop Team
                This is an automated message, please do not reply.
                """;

    public String sendOrderToEmail(Set<SaleProduct> cart, float total, String email){
        SimpleMailMessage mailMessage
                = new SimpleMailMessage();
        List<Product> productList = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        cart.forEach(saleProduct -> {
            Product product = productRepo.findById(saleProduct.getProductId()).get();
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

    private Integer getAmount(Product product, Integer reQuantity){
        return (product.getAmount() < reQuantity)? product.getAmount() : reQuantity;
    }
    private String createItemDescription(List<Product> products, List<Integer> quantities){
        final String template = ": ${product}\nQuantity: ${quantity}\nPrice: ${price}\n\n";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < products.size(); i++){
            Map<String, Object> params = new HashMap<>();
            Product product = products.get(i);
            params.put("product", product.getName());
            params.put("quantity", quantities.get(i));
            params.put("price",product.getPrice());
            res.append("Good №").append(i+1).append(StrSubstitutor.replace(template, params, "${", "}"));
        }
        return res.toString();
    }
}
