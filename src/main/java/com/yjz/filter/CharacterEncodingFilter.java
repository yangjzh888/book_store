package com.yjz.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

public class CharacterEncodingFilter implements Filter {

    //----------实例变量----------//

    // 为通过的请求设置默认字符编码
    protected String encoding = null;

    // 关联的过滤器配置对象.如果为空,表示未配置该过滤器实例
    protected FilterConfig filterConfig = null;

    // 是否忽略客户端指定的字符编码
    protected boolean ignore = true;


    //----------公共方法----------//

    /**
     * 使用这个过滤器
     * @param filterConfig 过滤器配置对象
     */
    public void init(FilterConfig filterConfig){
        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        if (value == null){
            this.ignore = true;
        } else if (value.equalsIgnoreCase("true")){
            this.ignore = true;
        } else if(value.equalsIgnoreCase("yes")){
            this.ignore = true;
        } else {
            this.ignore = false;
        }
    }

    // 停止使用这个过滤器
    public void destroy(){
        this.encoding = null;
        this.filterConfig = null;
    }

    // 对通过的请求参数值进行编码
    void encoding(HttpServletRequest request){
        Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()){
            String[] values = request.getParameterValues(names.nextElement().toString());
            for (int i = 0; i < values.length; i++){
                try {
                    values[i] = new String(values[i].getBytes("ISO-8859-1"), encoding);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 选择并设置(如果指定)要用于解释此请求的请求参数
     * @param request 正在处理的servlet请求
     * @param response 正在创建的servlet响应
     * @param chain 正在处理的过滤链
     * @throws IOException 输入输出错误
     * @throws ServletException servlet错误
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 选择设置需要使用的字符编码
        if (ignore || (request.getCharacterEncoding() == null)){
            String encoding = selectEncoding(request);
            if (encoding != null){
                request.setCharacterEncoding(encoding);
            }
            HttpServletRequest r = (HttpServletRequest) request;
            if (r.getMethod().equalsIgnoreCase("get")){
                encoding(r);
            }
        }
        // 将控制传递给下一个过滤器
        chain.doFilter(request, response);
    }

    //----------受保护的方法----------//

    /**
     * 根据当前请求的and/or过滤器初始化参数的特征,选择要使用的适当字符编码.
     * 如果不设置字符编码,则返回null
     * 默认实现无条件返回<strong>编码</strong>初始化参数为此过滤器配置的值
     * @param request 正在处理的servlet请求
     * @return
     */
    protected String selectEncoding(ServletRequest request){
        return (this.encoding);
    }






}
