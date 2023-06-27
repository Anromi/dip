package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.NotificationEndpoint;
import com.example.sweater.domain.User;
import com.example.sweater.domain.Mess;
import com.example.sweater.repos.MessageRepo;
import com.example.sweater.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private MessageService messageService;

    @Value("${pap.path}")
    private String papPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            @Valid Message message,
            @AuthenticationPrincipal User user
    ) throws IOException {
        Page<Mess> p = messageService.messageList(pageable, filter, user);
        model.addAttribute("p", p);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        if (message.getStatus(user).equals("no active")) {
            NotificationEndpoint.sendNotification("Проверьте таблицу заявок");
        }
        return "main";
    }

    @PostMapping("/questionnaire")
    public String questionnaire(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam(required = false, defaultValue = "") String filter,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        message.setAuthor(user);
        Map<String, String> errorsMap = bindingResult.hasErrors() ? UtilsController.getErrors(bindingResult) : Collections.emptyMap();
        model.mergeAttributes(errorsMap);
        model.addAttribute("message", bindingResult.hasErrors() ? message : null);
        if (!bindingResult.hasErrors()) {
            saveFile(message, file);
            messageRepo.save(message);
        }
        Page<Mess> page = messageService.messageList(pageable, filter, user);
        model.addAttribute("page", page);
        return "questionnaire";
    }

    @GetMapping("/application_selection")
    public Object application_selection(
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            @AuthenticationPrincipal User user
    ) {
        Page<Mess> requests = messageService.messageList(pageable, user);
        model.addAttribute("page", requests);

        return "application_selection";
    }

    @GetMapping("/saveFile")
    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File papDir = new File(papPath);

            if (!papDir.exists()) papDir.mkdir();

            String resultFilename = UUID.randomUUID().toString() + "." + file.getOriginalFilename();

            file.transferTo(new File(papPath, resultFilename));

            message.setFilename(resultFilename);
        }
    }

    @GetMapping("/orderTable")
    public String userMessges(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User aut,
            Model model,
            @RequestParam(required = false) Message mes,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Mess> page = messageService.messageListForUser(pageable, currentUser, aut);

        model.addAttribute("page", page);
        model.addAttribute("message", mes);
        model.addAttribute("userChannel", aut);
        model.addAttribute("isCurrentUser", currentUser.equals(aut));
        model.addAttribute("subscriptionsCount", aut.getSubscriptions().size());
        model.addAttribute("subscribersCount", aut.getSubscribers().size());
        model.addAttribute("isSubscriber", aut.getSubscribers().contains(currentUser));

        return "orderTable";
    }

    @PostMapping("/questionnaire2")
    public String questionnaire2(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam(required = false, defaultValue = "") String filter,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        message.setAuthor(user);
        Map<String, String> errorsMap = bindingResult.hasErrors() ? UtilsController.getErrors(bindingResult) : Map.of();
        model.mergeAttributes(errorsMap);
        model.addAttribute("message", bindingResult.hasErrors() ? message : null);
        if (!bindingResult.hasErrors()) saveFile(message, file); messageRepo.save(message);
        Page<Mess> page = messageService.messageList(pageable, filter, user);
        model.addAttribute("page", page);
        return "questionnaire2";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) message.setText(text);
            if (!StringUtils.isEmpty(tag)) message.setTag(tag);
            saveFile(message, file);
            messageRepo.save(message);
        }

        return "redirect:/user-messages/" + user;
    }
}
