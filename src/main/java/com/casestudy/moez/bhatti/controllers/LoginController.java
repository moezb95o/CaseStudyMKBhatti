package com.casestudy.moez.bhatti.controllers;

import com.casestudy.moez.bhatti.models.Authorities;
import com.casestudy.moez.bhatti.models.Credential;
import com.casestudy.moez.bhatti.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    CredentialService credentialService;

    @RequestMapping("/register")

    public ModelAndView getSignUpPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("registrationFormObject", new Credential());
        return mav;
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView registerAction(@Valid @ModelAttribute("registrationFormObject") Credential credential,
                                       BindingResult br,
                                       @RequestParam("confirmPassword") String confPassword,
                                       RedirectAttributes redirect) {
        ModelAndView mav = null;
        Credential cred = credentialService.getCredentialByUsername(credential.getUsername());

        if (br.hasErrors() || cred == null) {
            br.getAllErrors().forEach(System.out::println);
            mav = new ModelAndView("register");
            mav.addObject("message", "There was an error registering your account - please try again.");
        } else {
            if (credential.getPassword().equals(confPassword)) {
                cred = new Credential();
                cred.setUsername(credential.getUsername());
                cred.setPassword(new BCryptPasswordEncoder().encode(credential.getPassword()));
                cred.setEnabled(credential.isEnabled());

                Authorities authority = new Authorities();
                authority.setAuthority("user");
                cred.getAuthorities().add(authority);

                credentialService.addCredential(cred);
                mav = new ModelAndView("registrationConfirmation");
                mav.addObject("message", String.format("Credential successfully created for %s", cred.getUsername()));
            }
        }
        return mav;
    }

    @RequestMapping("/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }


}
