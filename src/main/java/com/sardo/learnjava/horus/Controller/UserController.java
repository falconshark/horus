package com.sardo.learnjava.horus.Controller;

import java.util.Calendar;
import java.util.Date;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.sardo.learnjava.horus.Entity.User;
import com.sardo.learnjava.horus.Service.UserService;

import com.sardo.learnjava.horus.Entity.ResetCode;
import com.sardo.learnjava.horus.Service.ResetCodeService;

import com.sardo.learnjava.horus.Form.UserForm;
import com.sardo.learnjava.horus.Form.LoginForm;
import com.sardo.learnjava.horus.Form.ForgetForm;
import com.sardo.learnjava.horus.Form.ResetForm;

import com.sardo.learnjava.horus.Service.MailService;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    ResetCodeService resetCodeService;

    @Autowired
    MailService mailService;

    @Value("${spring.application.baseurl}")
    private String baseUrl;

    @RequestMapping("/") // ルートへこのメソッドをマップする
    public ModelAndView index(Model model, ModelAndView modelAndView) {
        String error = (String) model.getAttribute("error");
        String message = (String) model.getAttribute("message");
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET) // ルートへこのメソッドをマップする
    public String reigsterForm() {
        return "account/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String register(@Validated UserForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setUsername(form.getUsername());

        String password = form.getPassword();
        String hexString = hashString(password);
        user.setPassword(hexString);

        user.setEmailAddress(form.getEmailAddress());
        user.setPhone(form.getPhone());
        user.setFullName(form.getFullName());

        if (!bindingResult.hasErrors()) {
            userService.SaveUser(user);
            return "redirect:/";
        } else {
            /* エラーがある場合は、一覧表示処理を呼ぶ */
            return "redirect:/register";
        }
    }

    @RequestMapping(value = "/forget", method = RequestMethod.GET) // ルートへこのメソッドをマップする
    public ModelAndView forgetForm(Model model, ModelAndView modelAndView) {
        String error = (String) model.getAttribute("error");
        modelAndView.addObject("error", error);
        modelAndView.setViewName("account/forget");
        return modelAndView;
    }

    @RequestMapping(value = "/forget", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String getForgetCode(@Validated ForgetForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        String emailAddrss = form.getEmailAddress();
        User targetUser = userService.SelectByEmail(emailAddrss);

        // If had user, create reset code and send it to email
        if (targetUser != null) {
            ResetCode resetCode = new ResetCode();
            String code = resetCodeService.CreateRandomCode();
            resetCode.setCode(code);

            Date date = new Date(); // 今日の日付
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 15);
            Instant instant = calendar.toInstant();

            resetCode.setRelatedAccount(targetUser.getId());
            resetCode.setUsed(false);

            // 生成されたコードをDBに入れます
            ZoneId zoneId = ZoneId.systemDefault(); // Or a specific ZoneId like ZoneId.of("America/New_York")
            LocalDateTime expiredDate = LocalDateTime.ofInstant(instant, zoneId);

            resetCode.setExpiredDate(expiredDate);
            resetCodeService.SaveCode(resetCode);

            String resetUrl = String.format("%s/reset?code=%s", baseUrl, code);
            String mailBody = String.format("以下のリンクをクリックして、パスワードを再設定してください。\n \n %s", resetUrl);

            mailService.sendMail(emailAddrss, "Horus - バスワードリセット", mailBody);
            redirectAttributes.addFlashAttribute("message", "パスワード再設定のリンクを、ご登録のメールアドレス宛に送信いたしました。");
            return "redirect:/";
        } else {
            // Otherwise, return to forget form
            redirectAttributes.addFlashAttribute("error", "このアカウントは存在しません。");
            return "redirect:/forget";
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET) // ルートへこのメソッドをマップする
    public ModelAndView resetForm(@RequestParam(name = "code", required = true) String code, Model model,
            ModelAndView modelAndView) {
        String error = (String) model.getAttribute("error");
        ResetCode resetCode = resetCodeService.SelectByCode(code);
        if (resetCode == null) {
            error = "このリセットコードは存在しません。";
            modelAndView.addObject("error", error);
            modelAndView.setViewName("account/resetPassword");
            return modelAndView;
        }
        modelAndView.addObject("resetCode", resetCode);
        modelAndView.setViewName("account/resetPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String resetPassword(@Validated ResetForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        String inputedCode = form.getResetCode();
        String password = form.getPassword();

        ResetCode resetCode = resetCodeService.SelectByCode(inputedCode);
        // If reset code no exited, return to reset password form
        if (resetCode == null) {
            redirectAttributes.addFlashAttribute("error", "このリセットコードは存在しません。");
            return "redirect:/";
        }
        // Otherwise, reset the password
        Integer relatedUserId = resetCode.getRelatedAccount();
        Optional<User> relatedUsers = userService.SelectById(relatedUserId);
        User relatedUser = relatedUsers.get();

        String hexString = hashString(password);
        relatedUser.setPassword(hexString);
        userService.SaveUser(relatedUser);

        redirectAttributes.addFlashAttribute("message", "パスワードが更新しました。");

        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String login(@Validated LoginForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        String username = form.getUsername();
        String password = form.getPassword();

        User targetUser = userService.SelectByUsername(username);
        String targetPassword = targetUser.getPassword();
        String hexString = hashString(password);

        if (targetUser == null || hexString.equals(targetPassword)) {
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "ユーザーが存在しないか、パスワードが間違っています。");
            return "redirect:/";
        }
    }

    private static String hashString(String string) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha256Byte = sha256.digest(string.getBytes());
            HexFormat hex = HexFormat.of().withLowerCase();
            String hexString = hex.formatHex(sha256Byte);

            return hexString;

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return "";
        }
    }
}