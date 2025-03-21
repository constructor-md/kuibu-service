package com.awesome.kuibuservice.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpCacheFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 包装请求
        RequestWrapper wrappedRequest = new RequestWrapper((HttpServletRequest) request);

        // 将包装后的对象传递给后续流程
        chain.doFilter(wrappedRequest, response);
    }
}
