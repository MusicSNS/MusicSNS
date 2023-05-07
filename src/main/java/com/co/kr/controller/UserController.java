package com.co.kr.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.service.UserService;
import com.co.kr.util.AlertUtils;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.LoginVO;
import com.co.kr.domain.LoginDomain;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {

	@Autowired
	private UserService userService;
	
	// 진입점
	@GetMapping("/")
	public String index() {
		return "index.html";
	}

	// about
	@GetMapping("/about")
	public String about() {
		return "pages/about-us.html";
	}

	// signin
	@GetMapping("/signin")
	public String signin() {
		return "pages/sign-in.html";
	}

	// Author
	@GetMapping("/author")
	public String author() {
		return "pages/author.html";
	}

	// contact
	@GetMapping("/contact")
	public String contact() {
		return "pages/contact-us.html";
	}

	// signup
	@GetMapping("/signup")
	public String signup() {
		return "pages/sign-up.html";
	}
	
	// 초기화면 설정
	@RequestMapping(value = "board")
	public ModelAndView login(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());
		int loginCheck = userService.mbLoginCheck(map);
		LoginDomain loginDomain = userService.mbGetId(map);
		if (loginCheck == 0) {
			String alertText = "없는 아이디이거나 패스워드가 잘못되었습니다. 가입해주세요";
			String redirectPath = "/main/signin";
			CommonUtils.redirect(alertText, redirectPath, response);
			return mav;
		}

		String IP = CommonUtils.getClientIP(request);
		session.setAttribute("ip", IP);
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("pw", loginDomain.getMbPw());
		session.setAttribute("name", loginDomain.getMbName());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());
//		session.setAttribute("mac", getLocalMacAddress());
		mav.setViewName("index.html");
		return mav;
	};
	// 회원가입 GET
		@RequestMapping(value = "sign", method = RequestMethod.GET)
		public void sign() {
			System.out.println("가입하기 GET");
		}

		// 회원가입 POST
		@RequestMapping(value = "sign", method = RequestMethod.POST)
		public ModelAndView sign(LoginDomain loginDomain, HttpServletResponse response, HttpServletRequest request,
				AlertUtils alert) throws IOException {
			ModelAndView mav = new ModelAndView();
			System.out.println("가입하기 POST");
			loginDomain.setMbId(request.getParameter("id"));
			loginDomain.setMbName(request.getParameter("name"));
			loginDomain.setMbPw(request.getParameter("pw"));
			loginDomain.setMbIp(CommonUtils.getClientIP(request));
			loginDomain.setMbLevel(0);
			loginDomain.setMbUse("Y");
			if (check(request) == 1) {
				System.out.println(check(request));
				mav.addObject("data", new AlertUtils("이메일이나 닉네임이 중복되었습니다.", "signup"));
				mav.setViewName("alert/alert");
				return mav;
			}
			userService.mbCreate(loginDomain);
			mav.addObject("data", new AlertUtils("회원가입이 완료되었습니다.", "signin"));
			System.out.println("가입 완료");
			mav.setViewName("alert/alert");
			return mav;
		}

		// 아이디 중복확인
		@ResponseBody
		@RequestMapping(value = "check")
		public int check(HttpServletRequest request) {
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			// 중복체크
			Map<String, String> map = new HashMap();
			map.put("mbId", id);
			map.put("mbName", name);
			int i = userService.mbDuplicationCheck(map);
			return i;
		}

		// 로그아웃
		@RequestMapping(value = "/logout", method = RequestMethod.GET)
		public String logout(HttpServletRequest request) throws Exception {
			System.out.println("로그아웃");
			HttpSession session = request.getSession();
			session.invalidate();
			return "redirect:/";
		}
}