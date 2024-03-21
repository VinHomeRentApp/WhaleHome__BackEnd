package com.example.vinhomeproject.controller;

import com.example.vinhomeproject.response.ResponseObject;
import com.example.vinhomeproject.service.PaymentService;
import com.example.vinhomeproject.service.PaypalService;
import com.example.vinhomeproject.service.UsersService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/paypal")
public class PaypalController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private PaypalService paypalService;

    @GetMapping("/pay")
    public ResponseEntity<ResponseObject> createPayment(
            @RequestParam("amount") String amount,
            @RequestParam("paymentId") String paymentId
    ) {
        try {
            String cancelUrl = "https://whalehome.up.railway.app/api/v1/paypal/cancel";
            String successUrl = "https://whalehome.up.railway.app/api/v1/paypal/success/" + paymentId;
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    "USD",
                    "paypal",
                    "sale",
                    cancelUrl,
                    successUrl
            );

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                            "Create payment successfully",
                            links.getHref()
                    ));
                }
            }
        } catch (PayPalRESTException e) {

        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                "",
                ""
        ));
    }

    @GetMapping("/success/{id}")
    public ModelAndView paymentSuccess(
            @PathVariable("id") String id,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        messagingTemplate.convertAndSend("/topic/payment", "Payment successfully");

        ModelAndView modelAndView = new ModelAndView(paypalService.paymentSuccessfully(id, paymentId, payerId));
        modelAndView.addObject("userId",paypalService.getUserByPaymentId(id));
        return modelAndView;
    }

    @GetMapping("/cancel")
    public ModelAndView paymentCancel() {
        return new ModelAndView("payment-cancel");
    }

    @MessageMapping("/payment-success")
    @SendTo("/topic/payment")
    public String send(final String message) throws Exception {
        return message;
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

}
