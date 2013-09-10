package com.intelliware.rewrite;

import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class NGLocaleResourceFilterTest {
	
	private static final String LOCALE_COOKIE_PARAM_VALUE = "locale";
	private static final String REWRITE_PATH_PARAM_VALUE = "scripts/angular-locale.js";
	private static final String RESOURCE_PATH_PARAM_VALUE = "scripts/locales";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain chain;
	private FilterConfig config;
	private NGLocaleResourceFilter fixture;
	private RequestDispatcher requestDispatcher;
	private Locale requestLocale;

	@Before
	public void before() throws Exception {
		
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
		chain = Mockito.mock(FilterChain.class);
		config = Mockito.mock(FilterConfig.class);

		requestLocale = new Locale("fr", "ca");
		Mockito.when(request.getLocale()).thenReturn(requestLocale);

		fixture = new NGLocaleResourceFilter();

		requestDispatcher = Mockito.mock(RequestDispatcher.class);
		Mockito.when(request.getRequestDispatcher(Mockito.anyString())).thenReturn(requestDispatcher);
		
		Mockito.when(config.getInitParameter(NGLocaleResourceFilter.RESOURCE_PATH_PARAM)).thenReturn(RESOURCE_PATH_PARAM_VALUE);
		Mockito.when(config.getInitParameter(NGLocaleResourceFilter.REWRITE_PATH_PARAM)).thenReturn(REWRITE_PATH_PARAM_VALUE);
		Mockito.when(config.getInitParameter(NGLocaleResourceFilter.LOCALE_COOKIE_PARAM)).thenReturn(LOCALE_COOKIE_PARAM_VALUE);
		fixture.init(config);		

	}

	@Test
	public void testFilterChainProceedsWhenNotNGResourceRequest() throws Exception {
		//set up
		Mockito.when(request.getRequestURI()).thenReturn("/the-path-we-are-not-looking-for");

		//execute
		fixture.doFilter(request, response, chain);
		
		//assert/verify
		Mockito.verify(request, Mockito.never()).getRequestDispatcher(Mockito.anyString());
		Mockito.verify(chain).doFilter(request , response);
	}
	
	@Test
	public void testRequestIsReWrittenUsingRequestLocale() throws Exception {
		//setup
		Mockito.when(request.getCookies()).thenReturn(new Cookie[] {});
		Mockito.when(request.getRequestURI()).thenReturn(REWRITE_PATH_PARAM_VALUE);

		//execute
		fixture.doFilter(request, response, chain);
		
		//assert/verify
		Mockito.verify(chain, Mockito.never()).doFilter(request , response);
		Mockito.verify(request).getRequestDispatcher(RESOURCE_PATH_PARAM_VALUE + "/angular-locale_fr-ca.js");
		Mockito.verify(requestDispatcher).forward(request, response);
	}
	
	@Test
	public void testRequestIsReWrittenUsingLocaleInCookieInsteadOfRequestLocale() throws Exception {
		//set up
		Mockito.when(request.getCookies()).thenReturn(new Cookie[] {new Cookie(LOCALE_COOKIE_PARAM_VALUE, "en-UK")});
		Mockito.when(request.getRequestURI()).thenReturn(REWRITE_PATH_PARAM_VALUE);

		//execute
		fixture.doFilter(request, response, chain);
		
		//assert/verify
		Mockito.verify(chain, Mockito.never()).doFilter(request , response);
		Mockito.verify(request).getRequestDispatcher(RESOURCE_PATH_PARAM_VALUE + "/angular-locale_en-uk.js");
		Mockito.verify(requestDispatcher).forward(request, response);		
	}
	
	@Test
	public void testRequestIsReWrittenUsingLocaleInCookieInsteadOfRequestLocaleAndOnlyHasLanguageCode() throws Exception {
		//set up		
		Mockito.when(request.getCookies()).thenReturn(new Cookie[] {new Cookie(LOCALE_COOKIE_PARAM_VALUE, "en")});
		Mockito.when(request.getRequestURI()).thenReturn(REWRITE_PATH_PARAM_VALUE);

		//execute
		fixture.doFilter(request, response, chain);
		
		//assert/verify
		Mockito.verify(chain, Mockito.never()).doFilter(request , response);
		Mockito.verify(request).getRequestDispatcher(RESOURCE_PATH_PARAM_VALUE + "/angular-locale_en.js");
		Mockito.verify(requestDispatcher).forward(request, response);	
	}

}
