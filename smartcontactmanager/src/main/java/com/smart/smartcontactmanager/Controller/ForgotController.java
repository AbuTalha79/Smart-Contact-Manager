package com.smart.smartcontactmanager.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.service.EmailService;

@Controller
public class ForgotController {
    Random random = new Random(0001);

    @Autowired
    private EmailService emailService;
    //email id form open handler
    @RequestMapping("/forgot")
    public String openEmailForm()
    {

        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session)
    {

        System.out.println("EMAIL: " +email);

        // generating OTP of 4 digit

        

        int otp = random.nextInt(9999);

        System.out.println("OTP: " +otp);

        //write code to send otp on email

        String subject = "OTP From SCM" ;
        String message = "<h1> OTP = " +otp+ "</h1>";
        String to = email;

        boolean flag = this.emailService.sendEmail(subject, message, to);

        if (flag) {
            
            session.setAttribute("otp", otp);
            return "verify_otp";
        }else
        {
            session.setAttribute("message", "check your email id !!");

            return "forgot_email_form";
        }
         
        
    }
}
