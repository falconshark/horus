package com.sardo.learnjava.horus.Controller;

import java.util.Calendar;
import java.util.Date;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sardo.learnjava.horus.Entity.User;
import com.sardo.learnjava.horus.Service.UserService;

import com.sardo.learnjava.horus.Entity.ResetCode;
import com.sardo.learnjava.horus.Service.ResetCodeService;

import com.sardo.learnjava.horus.Form.UserForm;
import com.sardo.learnjava.horus.Form.LoginForm;
import com.sardo.learnjava.horus.Form.ForgetForm;

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
    public String index(Model model) {
        return "index";
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
            userService.SaveUser(user);
            return "redirect:/";
        } else {
            /* エラーがある場合は、一覧表示処理を呼ぶ */
            return "redirect:/register";
        }
    }
    
    @RequestMapping(value = "/forget", method = RequestMethod.GET) // ルートへこのメソッドをマップする
    public String forgetForm() {
        return "account/forget";
    }
    
    @RequestMapping(value = "/forget", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String getForgetCode(@Validated ForgetForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        String emailAddrss = form.getEmailAddress();
        User targetUser = userService.SelectByEmail(emailAddrss);

        //If had user, create reset code and send it to email
        if(targetUser != null){
            ResetCode restCode = new ResetCode();
            String code = resetCodeService.CreateRandomCode();
            restCode.setCode(code);

            Date date = new Date(); // 今日の日付
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 15);
            
            //生成されたコードをDBに入れます
            Date expiredDate = calendar.getTime();
            restCode.setExpiredDate(dateFormat.format(expiredDate));
            resetCodeService.SaveCode(restCode);

            String resetUrl = String.format("%s/reset?code=%s", baseUrl, code);
            String mailBody = String.format("以下のリンクをクリックして、パスワードを再設定してください。\n \n %s", resetUrl);
            
            mailService.sendMail(emailAddrss, "Horus - バスワードリセット", mailBody);
            return "redirect:/";
        }else{
            //Otherwise, return to forget form
            redirectAttributes.addFlashAttribute("error", "このアカウントは存在しません。");
            return "redirect:/forget";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST) // ルートへこのメソッドをマップする
    public String login(@Validated LoginForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        String username = form.getUsername();
        String password = form.getPassword();

        User targetUser = userService.SelectByUsername(username);
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