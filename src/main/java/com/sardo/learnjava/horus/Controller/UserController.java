package com.sardo.learnjava.horus.Controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sardo.learnjava.horus.Entity.User;
import com.sardo.learnjava.horus.Service.UserService;
import com.sardo.learnjava.horus.Form.UserForm;
import com.sardo.learnjava.horus.Form.LoginForm;

@Controller
public class UserController {
    @Autowired
    UserService service;

    @RequestMapping("/") // ルートへこのメソッドをマップする
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET) // ルートへこのメソッドをマップする
    public String reigsterForm() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String register(@Validated UserForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setUsername(form.getUsername());

        try {
            String password = form.getPassword();
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha256Byte = sha256.digest(password.getBytes());
            HexFormat hex = HexFormat.of().withLowerCase();
            String hexString = hex.formatHex(sha256Byte);
            user.setPassword(hexString);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }

        user.setEmailAddress(form.getEmailAddress());
        user.setPhone(form.getPhone());
        user.setFullName(form.getFullName());

        if (!bindingResult.hasErrors()) {
            service.SaveUser(user);
            return "redirect:/";
        } else {
            /* エラーがある場合は、一覧表示処理を呼ぶ */
            return "redirect:/register";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String login(@Validated LoginForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        String username = form.getUsername();
        String password = form.getPassword();

        User targetUser = service.SelectByUsername(username);
        String targetPassword = targetUser.getPassword();

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha256Byte = sha256.digest(password.getBytes());
            HexFormat hex = HexFormat.of().withLowerCase();
            String hexString = hex.formatHex(sha256Byte);

            if(hexString.equals(targetPassword)){
                return "redirect:/dashboard";
            }else{
                return "redirect:/";
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return "redirect:/";
        }
    }
}