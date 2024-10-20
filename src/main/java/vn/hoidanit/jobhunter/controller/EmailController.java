package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }


    @GetMapping("/email")
    @ApiMessage("Send a email")
    public String sendSimpleEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
//        this.emailService.sendMailFromTemplateSync("20211841@eaut.edu.vn","Testing email with template","job");
        return "send email ";
    }
}
