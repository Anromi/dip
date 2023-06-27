package com.example.sweater.controller;

import com.example.sweater.domain.User;
import com.example.sweater.domain.CaptchaResponseDto;
import com.example.sweater.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;

@Controller
public class RegisController {
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private UserSevice userSevice;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponce,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ) {
        String url = String.format(CAPTCHA_URL, secret, captchaResponce);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if (!response.isSuccess()) {
            model.addAttribute("captchaError", "Заполнить капчу");
        }

        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);

        if (StringUtils.isEmpty(passwordConfirm)) {
            model.addAttribute("password2Error", "Подтверждение пароля не может быть пустым");
        }

        if (!Objects.equals(user.getPassword(), passwordConfirm)) {
            model.addAttribute("passwordError", "Пароли разные");
        }

        if (StringUtils.isEmpty(passwordConfirm) || bindingResult.hasErrors() || !response.isSuccess()) {
            model.mergeAttributes(UtilsController.getErrors(bindingResult));
            return "registration";
        }

        if (!userSevice.addUser(user)) {
            model.addAttribute("usernameError", "Пользователь существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userSevice.activateUser(code);
        String messageType = isActivated ? "отлично" : "угроза";
        String message = isActivated ? "Пользователь успешно активирован" : "Код активации не найден";
        model.addAttribute("messageType", messageType);
        model.addAttribute("message", message);
        return "login";
    }
}
