package org.example.rgybackend.Controller;

import java.util.List;

import org.example.rgybackend.DTO.IntimateDTO;
import org.example.rgybackend.Model.*;
import org.example.rgybackend.Service.MilestoneServive;
import org.example.rgybackend.Service.UserService;
import org.example.rgybackend.Utils.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MilestoneServive milestoneServive;

    @GetMapping("/login")
    public boolean login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        boolean result = userService.verifyPasswordByName(username, password);
        if(result) {
            String userid = userService.getProfileByName(username).getUserid();
            session.setAttribute("user", userid);
        }
        return result;
    }

    @GetMapping("/logout")
    public boolean logout(HttpSession session) {
        session.invalidate();
        return true;
    }

    @GetMapping("/existed")
    public boolean userExisted(@RequestParam String username) {
        return userService.userExisted(username);
    }

    @GetMapping("/get")
    public ProfileModel getUserProfile(HttpSession session) {
        String userid = (String)session.getAttribute("user");
        return userService.getUserProfile(userid);
    }

    @GetMapping("/getall")
    public List<AdminProfileModel> getAllProfile(HttpSession session) {
        String adminid = (String)session.getAttribute("user");
        if(!userService.isAdmin(adminid)) {
            throw new ForbiddenException("只有管理员允许进行此操作");
        }
        return userService.getAllProfile(adminid);
    }

    @GetMapping("/getpsy")
    public PsyProfileModel getPsyProfile(HttpSession session) {
        String psyid = (String)session.getAttribute("user");
        return userService.getPsyProfile(psyid);
    }

    @GetMapping("/getsim")
    public SimplifiedProfileModel getSimplifiedProfile(@RequestParam String userid) {
        return userService.getSimplifiedProfile(userid);
    }

    @GetMapping("/getintm")
    public List<IntimateDTO> getIntimateUsers(HttpSession session) {
        String userid = (String)session.getAttribute("user");
        return userService.getIntimateUsers(userid);
    }

    @GetMapping("/milestone")
    public MilestoneModel getMilestone(HttpSession session) {
        String userid = (String)session.getAttribute("user");
        return milestoneServive.getMilestone(userid);
    }

    @GetMapping("/verify/pwd")
    public boolean verifyPassword(@RequestParam String password, HttpSession session) {
        String userid = (String)session.getAttribute("user");
        return userService.verifyPassword(userid, password);
    }

    @GetMapping("/verify/admin")
    public boolean verifyAdmin(@RequestParam String verifyKey) {
        return userService.verifyAdmin(verifyKey);
    }

    @GetMapping("/disabled/get")
    public boolean isDisabled(@RequestParam String username) {
        return userService.isDisabled(username);
    }

    @PostMapping("/add")
    public boolean addUser(@RequestBody UserModel user, HttpSession session) {
        String email = (String)session.getAttribute("check");
        if(!user.getProfile().getEmail().equals(email)) {
            return false;
        }
        return userService.addUser(user);
    }

    @PutMapping("/profile/update")
    public boolean updateProfile(@RequestBody ProfileModel profile, HttpSession session) {
        String userid = (String)session.getAttribute("user");
        if(!userid.equals(profile.getUserid())) {
            throw new ForbiddenException("无权修改该用户信息");
        }
        return userService.updateProfile(profile);
    }

    @PutMapping("/profile/updatepsy")
    public boolean updatePsyProfile(@RequestBody PsyProfileModel profile, HttpSession session) {
        String psyid = (String)session.getAttribute("user");
        if(!psyid.equals(profile.getUserid())) {
            throw new ForbiddenException("无权修改该用户信息");
        }
        return userService.updatePsyProfile(profile);
    }

    @PutMapping("/pwd")
    public boolean updatePassword(@RequestParam String password, HttpSession session) {
        String userid = (String)session.getAttribute("user");
        return userService.updatePassword(userid, password);
    }

    @PutMapping("/disabled/set")
    public boolean setDisabled(@RequestParam String userid, @RequestParam boolean disabled, HttpSession session) {
        String adminid = (String)session.getAttribute("user");
        if(!userService.isAdmin(adminid)) {
            throw new ForbiddenException("只有管理员允许进行此操作");
        }
        return userService.setDisabled(userid, disabled);
    }

    @GetMapping("/secure/code")
    public boolean postAuthCode(@RequestParam String email, HttpSession session) {
        boolean result = userService.postAuthCode(email);
        if(result) {
            session.setAttribute("check", email);
        }
        return result;
    }

    @GetMapping("/secure/check")
    public boolean checkAuthCode(@RequestParam Long authCode, HttpSession session) {
        String email = (String)session.getAttribute("check");
        if(email == null) {
            return false;
        }
        return userService.checkAuthCode(email, authCode);
    }
}
