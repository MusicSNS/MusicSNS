package com.co.kr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.co.kr.service.UserService;
import com.co.kr.service.CommentService;
import com.co.kr.service.UploadService;
import com.co.kr.service.ProfileService;
import com.co.kr.util.AlertUtils;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.CommentListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.domain.UserFileDomain;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ProfileService profileService;
	// 진입점
	@GetMapping("/")
	public String index() {
		return "pages/sign-in.html";
	}

	// signin
	@GetMapping("/signin")
	public String signin() {
		return "pages/sign-in.html";
	}

	// about
	@GetMapping("/about")
	public String about() {
		return "pages/about-us.html";
	}

	// Author
	@GetMapping("/author")
	public ModelAndView author(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String, Object> mb = new HashMap<String, Object>();
		mb.put("mbName", session.getAttribute("name").toString());
		int followsize = profileService.selectcountfollow(mb);
		int followingsize = profileService.selectcountfollowing(mb);
		List<BoardListDomain> items = uploadService.authorBoardList(mb);
		int size = uploadService.authorBoard(mb);
		System.out.println("items ==> " + items);
		for (BoardListDomain item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bdSeq", Integer.parseInt(item.getBdSeq()));
			List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
			for (BoardFileDomain list : fileList) {
				String path = list.getUpNewFileName().replaceAll("\\\\", "/");
				list.setUpFilePath(path);
			}
			item.setFiles(fileList);
		}
		UserFileDomain profile = profileService.mbSelectOneFile(mb);
		if (profile != null) {
		    String profilePath = profile.getUpNewFileName().replace("\\\\", "/");
		    profile.setUpFilePath(profilePath);
		    mav.addObject("profile", profile.getUpFilePath());
		} else {
		    System.out.println("NULL");
		}
		mav.addObject("followsize", followsize);
		mav.addObject("followingsize", followingsize);
		mav.addObject("items", items);
		mav.addObject("itemSize",size);
		mav.addObject("mbSeq", session.getAttribute("mbSeq"));
		mav.addObject("mbName", session.getAttribute("name").toString());
		mav.setViewName("profile/author.html");
		return mav;
	}

	//UserController.java

	@GetMapping(value="/profile")
		public ModelAndView profile(HttpServletRequest request) {
			ModelAndView mav = new ModelAndView();
			HttpSession session = request.getSession();
			HashMap<String, Object> mb = new HashMap<String, Object>();
			mb.put("mbName", request.getParameter("mbName"));
			int followingsize = profileService.selectcountfollowing(mb);
			int followsize = profileService.selectcountfollow(mb);
			
			HashMap<String, Object> mab = new HashMap<>();
			mab.put("mbName", session.getAttribute("name"));
			mab.put("flmbName", request.getParameter("mbName"));
			int duplecheck = profileService.duplicatefollow(mab);
			
			List<BoardListDomain> items = uploadService.authorBoardList(mb);
			int size = uploadService.authorBoard(mb);
			System.out.println("items ==> " + items);
			for (BoardListDomain item : items) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("bdSeq", Integer.parseInt(item.getBdSeq()));
				List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
				for (BoardFileDomain list : fileList) {
					String path = list.getUpNewFileName().replaceAll("\\\\", "/");
					list.setUpFilePath(path);
				}
				item.setFiles(fileList);
			}
			UserFileDomain profile = profileService.mbSelectOneFile(mb);
			if (profile != null) {
			    String profilePath = profile.getUpNewFileName().replace("\\\\", "/");
			    profile.setUpFilePath(profilePath);
			    mav.addObject("profile", profile.getUpFilePath());
			} else {
			    System.out.println("NULL");
			}
			String comment = profileService.userSelectName(mb);
			
			mav.addObject("duplecheck", duplecheck);
			mav.addObject("followingsize", followingsize);
			mav.addObject("followsize", followsize);
			mav.addObject("comment",comment);
			mav.addObject("othername",request.getParameter("mbName").toString());
			mav.addObject("items", items);
			mav.addObject("itemSize",size);
			mav.addObject("mbSeq", session.getAttribute("mbSeq"));
			mav.setViewName("otherprofile/other.html");
			return mav;
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
		session.setAttribute("mbSeq",loginDomain.getMbSeq());
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("pw", loginDomain.getMbPw());
		session.setAttribute("name", loginDomain.getMbName());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());
		session.setAttribute("comment", loginDomain.getMbComment());
//		session.setAttribute("mac", getLocalMacAddress());
		mav.addObject("data", new AlertUtils("로그인 성공", "bdList"));
		mav.setViewName("alert/alert");
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
		int totalcount = userService.mbGetAll();
		loginDomain.setMbId(request.getParameter("id"));
		loginDomain.setMbName(request.getParameter("name"));
		loginDomain.setMbPw(request.getParameter("pw"));
		loginDomain.setMbIp(CommonUtils.getClientIP(request));
		loginDomain.setMbLevel((totalcount == 0 ) ? 100 : 1);
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

	@RequestMapping(value = "bdList")
	public ModelAndView bdList(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		
		HashMap<String, Object> like = new HashMap<String, Object>();
		like.put("mbId", session.getAttribute("id")); //추가
		
 		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> " + items);
		for (BoardListDomain item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bdSeq", Integer.parseInt(item.getBdSeq()));
			like.put("bdSeq", Integer.parseInt(item.getBdSeq())); //추가
			List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
			List<CommentListDomain> commentList = commentService.commentList(map);
			for (BoardFileDomain list : fileList) {
				String path = list.getUpNewFileName().replaceAll("\\\\", "/");
				list.setUpFilePath(path);
			}
			item.setComment(commentList);
			item.setFiles(fileList);
			int likecount = uploadService.selectlike(like);
			String statlike = uploadService.selectstatus(like);
			System.out.println("status : "+statlike);
			if (statlike == null) {
				item.setLike("like!");
			}
			else {
				item.setLike("unlike!");
			}
			item.setLikecount(likecount);
		}
		mav.addObject("items", items);
		mav.setViewName("pages/board.html");
		return mav;
	}
	
	//대시보드 리스트 보여주기
	@GetMapping("mbList") // required=false null 일때 받기 에러금지 // querystring == @RequestParam
	public ModelAndView mbList(HttpServletRequest request) {
				
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<>();
			HttpSession session = request.getSession();
			
			String MenuName = "Admin";
			List<LoginDomain> loginDomains = userService.mbAllList();
			
			mav.addObject("menuname", MenuName);
			mav.addObject("items", loginDomains);
			mav.setViewName("pages/admin");
			return mav; 
		};

	
	//수정페이지 이동
	@GetMapping("/modify/{mbSeq}")
	   public ModelAndView mbModify(@PathVariable("mbSeq") String mbSeq, RedirectAttributes re) throws IOException {
			ModelAndView mav = new ModelAndView();
			re.addAttribute("mbSeq", mbSeq);
			mav.setViewName("redirect:/mbEditList");
			return mav;
		};
		
	//대시보드 리스트 보여주기
	@GetMapping("mbEditList")
	   public ModelAndView mbListEdit(@RequestParam("mbSeq") String mbSeq, HttpServletRequest request) {
			
			ModelAndView mav = new ModelAndView();
			// 해당리스트 가져옴
			HashMap<String, String> map = new HashMap<>();
			map.put("mbSeq", mbSeq);
			System.out.println(map);
			LoginDomain loginDomain = userService.mbSelectList(map);
			mav.addObject("edititem",loginDomain);
			mbList(request);
			List<LoginDomain> list = userService.mbAllList();
			mav.addObject("items", list);
			mav.setViewName("pages/adminEdit");
			return mav; 
		};
		
	//수정업데이트
	@RequestMapping("/update")
	public ModelAndView mbModify(HttpServletRequest request, RedirectAttributes re, LoginVO loginVO) throws IOException {
			
			ModelAndView mav = new ModelAndView();
			
			//page 초기화
			HttpSession session = request.getSession();
			
			System.out.println(request.getParameter("id"));
			System.out.println(request.getParameter("name"));
			System.out.println(request.getParameter("pw"));
			System.out.println(request.getParameter("comment"));
			System.out.println(request.getParameter("id"));
			//db 업데이트
			LoginDomain loginDomain = null; //초기화
			String IP = CommonUtils.getClientIP(request);
			loginDomain = LoginDomain.builder()
					.mbName(request.getParameter("name"))
					.mbPw(request.getParameter("pw"))
					.mbLevel(Integer.parseInt(request.getParameter("level")))
					.mbComment(request.getParameter("comment"))
					.mbIp(IP)
					.mbUse("Y")
					.mbId(request.getParameter("id"))
					.build();
			
			userService.mbUpdate(loginDomain);
			
			mav.setViewName("redirect:/mbList");
			return mav;
		};

	//삭제
	@GetMapping("/mbremove/{mbId}")
	public ModelAndView mbRemove(RedirectAttributes re, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			
			System.out.println(request.getParameter("mbId"));
			//db 삭제
			Map map = new HashMap<String, String>();
			map.put("mbId", request.getParameter("mbId"));
			userService.mbRemove(map);
					
			//보고 있던 현재 페이지로 이동
			mav.setViewName("redirect:/mbList");
			return mav;
		};
		
	// 회원가입 POST
	@RequestMapping(value = "adminsign", method = RequestMethod.POST)
	public ModelAndView adminsign(LoginDomain loginDomain, HttpServletResponse response, HttpServletRequest request,
			AlertUtils alert) throws IOException {
			ModelAndView mav = new ModelAndView();
			System.out.println("가입하기 POST");
			int totalcount = userService.mbGetAll();
			loginDomain.setMbId(request.getParameter("id"));
			loginDomain.setMbName(request.getParameter("name"));
			loginDomain.setMbPw(request.getParameter("pw"));
			loginDomain.setMbIp(CommonUtils.getClientIP(request));
			loginDomain.setMbLevel((totalcount == 0 ) ? 100 : 1);
			loginDomain.setMbUse("Y");
			if (check(request) == 1) {
				System.out.println(check(request));
				mav.addObject("data", new AlertUtils("이메일이나 닉네임이 중복되었습니다.", "signup"));
				mav.setViewName("redirect:/mbList");
				return mav;
			}
			userService.mbCreate(loginDomain);
			mav.addObject("data", new AlertUtils("회원가입이 완료되었습니다.", "signin"));
			System.out.println("가입 완료");
			mav.setViewName("redirect:/mbList");
			return mav;
		}
		
}