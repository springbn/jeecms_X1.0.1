package com.jeecms.front.config;

import java.util.ArrayList;
import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.jeecms.common.constants.TplConstants;
import com.jeecms.common.constants.WebConstants;
import com.jeecms.common.web.ContentSecurityMethodArgumentResolver;
import com.jeecms.common.web.PageableArgumentResolver;
import com.jeecms.common.web.PaginableArgumentResolver;
import com.jeecms.common.web.RegisterCustomEditors;
import com.jeecms.front.interceptor.FrontContextInterceptor;
import com.jeecms.system.interceptor.GlobalInterceptor;

/**
 * 前端配置类，并非 通用全局配置类， application的WebConfig 通用全局配置类，通用全局配置类只允许存在一个。
 * 所以此类不能被springboot扫描在内当做组件（Configuration配置类注解） 具体的拦截请求处理业务没实现
 * 
 * @author: tom
 * @date: 2019年3月11日 下午1:51:15
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved. Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Configuration
@ComponentScan({ "com.jeecms.front.controller" })
public class FrontConfig extends DelegatingWebMvcConfiguration {

	/**
	 * 注册{@link BindingInitializer}和{@link PageableArgumentResolver}
	 */
	@Bean
	@Override
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
		ConfigurableWebBindingInitializer bindingInitializer = getConfigurableWebBindingInitializer();
		RegisterCustomEditors.registerCustomEditors(bindingInitializer);
		adapter.setWebBindingInitializer(bindingInitializer);
		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
		// 分页Pageable参数解析器
		argumentResolvers.add(new PageableArgumentResolver());
		// Paginable参数解析器
		argumentResolvers.add(new PaginableArgumentResolver());
		argumentResolvers.add(new ContentSecurityMethodArgumentResolver());
		adapter.setCustomArgumentResolvers(argumentResolvers);
		return adapter;
	}

	@Bean
	public FrontContextInterceptor frontContextInterceptor() {
		return new FrontContextInterceptor();
	}

	@Bean
	public GlobalInterceptor globalInterceptor() {
		return new GlobalInterceptor();
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	/**
	 * 注册拦截器。
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(globalInterceptor()).excludePathPatterns(TplConstants.RES_PATH + "**",
				WebConstants.UPLOAD_PATH + "**");
		registry.addInterceptor(frontContextInterceptor()).excludePathPatterns(TplConstants.RES_PATH + "**",
				WebConstants.UPLOAD_PATH + "**");
		super.addInterceptors(registry);
	}


}
