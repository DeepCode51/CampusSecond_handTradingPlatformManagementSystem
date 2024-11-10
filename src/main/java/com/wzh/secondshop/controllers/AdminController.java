package com.wzh.secondshop.controllers;

import com.wzh.secondshop.models.FirstType;
import com.wzh.secondshop.models.Good;
import com.wzh.secondshop.models.Order;
import com.wzh.secondshop.models.User;
import com.wzh.secondshop.services.GoodService;
import com.wzh.secondshop.services.OrderService;
import com.wzh.secondshop.services.TypeService;
import com.wzh.secondshop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping(value = "admin")
public class AdminController {

    private final UserService userService;
    private final GoodService goodService;
    private final TypeService typeService;
    private final OrderService orderService;

    @Autowired
    public AdminController(UserService userService, GoodService goodService, TypeService typeService, OrderService orderService) {
        this.userService = userService;
        this.goodService = goodService;
        this.typeService = typeService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/adminLogin", method = RequestMethod.GET)
    public String getAdminLogin(){
        return "admin/adminLogin";
    }

    @RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
    public String postAdminLogin(ModelMap model,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "password", required = false) String password,
                                 HttpSession session) {
        User admin = userService.getUserByEmail(email);
        String message;
        if (admin != null){
            String mdsPass = DigestUtils.md5DigestAsHex((password + admin.getCode()).getBytes());
//            if (!mdsPass .equals(admin.getPassword())){
//                message = "用户密码错误！";
//            }
            if (!password .equals(admin.getPassword())){
                message = "用户密码错误！";
            } else if (admin.getRoleId() != 101){
                message = "用户没有权限访问！";
            } else {
                session.setAttribute("admin",admin);
                return "redirect:/admin/adminPage";
            }
        } else {
            message = "用户不存在！";
        }
        model.addAttribute("message", message);
        return "admin/adminLogin";
    }

    @RequestMapping(value = "/adminLogout", method = RequestMethod.GET)
    public String adminLogout(@RequestParam(required = false, defaultValue = "false" )String adminLogout, HttpSession session){
        if (adminLogout.equals("true")){
            session.removeAttribute("admin");
        }
//        adminLogout = "false";
        return "redirect:/";
    }

    @RequestMapping(value = "/adminPage", method = RequestMethod.GET)
    public String getAdminPage(ModelMap model,
                               HttpSession session){
        User admin = (User) session.getAttribute("admin");
        if (admin == null){
            return "redirect:/admin/adminLogin";
        }
        List<Good> goodList = goodService.getAllGoodList();
        for (Good good : goodList) {
            good.setGoodUser(userService.getUserById(good.getUserId()));
            good.setGoodSecondType(typeService.getSecondTypeById(good.getSecondTypeId()));
        }
        List<User> userList = userService.getAllUser();
        List<FirstType> firstTypeList = typeService.getAllFirstType();
        List<Order> orderList = orderService.getOrderList();
        model.addAttribute("goodList", goodList);
        model.addAttribute("userList", userList);
        model.addAttribute("firstTypeList", firstTypeList);
        model.addAttribute("orderList", orderList);
        return "admin/adminPage";
    }
    @RequestMapping(value = "/user/update/status/{statusId}&{userId}", method = RequestMethod.GET)
    public ResponseEntity updateUserStatus(@PathVariable Integer statusId,
                                            @PathVariable Integer userId){
        Boolean success = userService.updateUserStatus(statusId, userId);
        if (success){
            List<User> userList = userService.getAllUser();
            return ResponseEntity.ok(userList);
        }
        return ResponseEntity.ok(success);
    }

    @RequestMapping(value = "/user/delete/{userId}", method = RequestMethod.GET)
    public ResponseEntity deleteUser(@PathVariable Integer userId){
        Boolean success = userService.deleteUser(userId);
        if (success){
            List<User> userList = userService.getAllUser();
            return ResponseEntity.ok(userList);
        }
        return ResponseEntity.ok(success);
    }

}
