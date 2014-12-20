package com.mtf.demo.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.mtf.demo.model.page.User;
import com.mtf.demo.util.Constant;

public class SecurityInterceptor implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(SecurityInterceptor.class);
	private List<String>		excludeUrls;

	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());
		logger.debug("preHandle: " + url);
		
		if (this.excludeUrls != null && this.excludeUrls.contains(url)) {
			return true;
		} else {
			User user = (User) request.getSession().getAttribute(Constant.SESSION_INFO);
			if (user != null && user.getId() != null && user.getId().trim().length() > 0) {
				
				
				return true;
			} else {
				request.setAttribute("errorCode", "nopermission");
				request.getRequestDispatcher("/error/error.jsp").forward(request, response);
				return false;
			}
		}
	}
}
