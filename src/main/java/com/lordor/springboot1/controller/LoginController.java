package com.lordor.springboot1.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lordor.springboot1.Util.IpUtil;
import com.lordor.springboot1.entity.Login;
import com.lordor.springboot1.entity.LogoEntity;
import com.lordor.springboot1.service.LoginService;
import com.lordor.springboot1.service.LogoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private LogoService logoService;

    /**
     * 首页跳转
     *
     * @return
     */
    @RequestMapping("/loginIndex")
    public String LoginAll() {
        return "index";
    }

    /**
     * 跳转到用户登录页面
     *
     * @return 登录页面
     */
    @RequestMapping("/userLogin")
    public String loginOnIndex() {
        return "login/login";
    }

    /**
     * 用户全查分页
     *
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping("/getAllPerson")
    public String getAllPerson(Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum) {
        PageHelper.startPage(pageNum, 7);
        List<Login> list = loginService.loginAll();
        PageInfo<Login> pageInfo = new PageInfo<Login>(list);
        model.addAttribute("pageInfo", pageInfo);
        System.out.println("分页全查....." + pageInfo);
        return "list";
    }

    /* 用户登录检测 */
    @RequestMapping("/longOn")
    public String loginOn(@Param("username") String username,
                          @Param("password") String password,
                          HttpServletRequest request) {
        if (StringUtils.isEmpty(username)) {
            return "redirect:/userLogin";
        }
        if (StringUtils.isEmpty(password)) {
            return "redirect:/userLogin";
        }
        Login user = loginService.loginOn(username, password);
        if (user != null) {
            //获取登录人的ip地址 添加进日志表
            //获取ip地址
            String ipAddr = IpUtil.getIpAddr(request);
            //把 username 和 ip 一起添加进logo表里面
            logoService.addLogo(username,ipAddr);
            //登录成功重定向到 全查页面
            System.out.println("添加成功.....");
            return "redirect:/getAllPerson";
        }
        return "redirect:/userLogin";
    }

    /**
     * //删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping("/delLoginById/{id}")
    public String delLoginById(@PathVariable("id") Integer id) {
        loginService.delLoginById(id);
        System.out.println("删除..........");
        return "redirect:/getAllPerson";
    }

    /**
     * //添加用户
     *
     * @param map
     * @return
     */
    @RequestMapping("/addLogin")
    public String addLogin(ModelMap map) {
        map.addAttribute("login", new Login());
        System.out.println("跳转添加.......");
        return "add/add";
    }

    /**
     * 用户添加
     *
     * @param login
     * @return
     */
    @RequestMapping("/saveLogin")
    public String saveLogin(@ModelAttribute Login login) {
        //判断添加用户,账号密码不能为空
        if (StringUtils.isEmpty(login.getUsername())) {
            return "账号不能为空";
        }
        if (StringUtils.isEmpty(login.getPassword())) {
            return "密码不能为空";
        }
        loginService.addLogin(login);
        System.out.println("添加成功......" + login);
        return "redirect:/userLogin";
    }

    /**
     * 修改用户
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/updateLogin/{id}")
    public String updateLogin(@PathVariable("id") Integer id, Model model) {
        Login login = loginService.LoginById(id);
        model.addAttribute("upLogin", login);
        System.out.println("修改跳转....." + login);
        return "update/update";
    }

    //修改用户保存
    @RequestMapping("/saveUp")
    public String saveUp(Login login) {
        if (StringUtils.isEmpty(login.getUsername())) {
            return "账号不能为空";
        }
        if (StringUtils.isEmpty(login.getPassword())) {
            return "密码不能为空";
        }
        loginService.updateLogin(login);
        System.out.println("修改成功....." + login);
        return "redirect:/getAllPerson";
    }

    /**
     * 用户详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/fanById/{id}")
    public String fanById(@PathVariable("id") Integer id, Model model) {
        Login login = loginService.LoginById(id);
        model.addAttribute("fanById", login);
        System.out.println("详情跳转....." + login);
        return "fanById/fanById";
    }
}