package com.intelliware.rewrite;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NGLocaleResourceFilter implements Filter {

	static final String LOCALE_COOKIE_PARAM = "localeCookieName";
	static final String REWRITE_PATH_PARAM = "rewritePath";
	static final String RESOURCE_PATH_PARAM = "resourcePath";
	
	private String rewritePath;
	private String resourcePath;
	private String localeCookieName;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.rewritePath = filterConfig.getInitParameter(REWRITE_PATH_PARAM);
		this.resourcePath = filterConfig.getInitParameter(RESOURCE_PATH_PARAM);
		this.localeCookieName = filterConfig.getInitParameter(LOCALE_COOKIE_PARAM);
	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if(httpRequest.getRequestURI().contains(this.rewritePath)) {
			
			httpRequest.getRequestDispatcher(dispatchTo(httpRequest)).forward(
					httpRequest, 
					(HttpServletResponse) response);
			
		} else {
			chain.doFilter(httpRequest, response);
		}

	}

	private Locale getLocale(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals(this.localeCookieName)) {
				String[] parts = cookie.getValue().split("-");
				if(parts.length == 1) {
					return new Locale(parts[0]);
				}
				return new Locale(parts[0], parts[1]);
			}
		}
		
		return request.getLocale();
	}

	private String dispatchTo(HttpServletRequest httpRequest) {
		String normalizedLocale = getLocale(httpRequest).toString().toLowerCase().replace("_", "-");
		return this.resourcePath + "/angular-locale_" + normalizedLocale + ".js";
	}

	@Override
	public void destroy() {}

}
