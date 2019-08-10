package com.karen.servlet;

import com.karen.annotation.*;
import com.karen.controller.LikeController;
import com.sun.deploy.net.HttpResponse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LIKE
 * @date 2019/8/9 9:44
 */
public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();
    Map<String,Object> beans =  new HashMap<String,Object>();
    Map<String,Method> handlerMap =  new HashMap<String,Method>();

    @Override
    public void init (ServletConfig config) throws ServletException{
        doScanPackage("com.karen");
        doInstance();
        doAutowired();
        doUrlMapping();

    }

    private static Object[] hand(HttpServletRequest request, HttpServletResponse response,Method method){
        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        int args_i = 0;
        int index = 0;
        for (Class<?> paramClazz:paramClazzs){
            if (ServletRequest.class.isAssignableFrom(paramClazz)){
                args[args_i++] = request;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)){
                args[args_i++] = response;
            }

            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0){
                for (Annotation paramAn : paramAns){
                    if (LKRequestParam.class.isAssignableFrom(paramAn.getClass())){
                        LKRequestParam rp = (LKRequestParam) paramAn;
                        args[args_i++] = request.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }

    public void doInstance(){
        for(String className:classNames){
            String cn = className.replace(".class","");
            try{
                Class<?> clazz = Class.forName(cn);
                if(clazz.isAnnotationPresent(LKController.class)){
                    Object obj1 = clazz.newInstance();
                    LKRequestMapping lmp1 = clazz.getAnnotation(LKRequestMapping.class);
                    String key1 = lmp1.value();
                    beans.put(key1,obj1);
                }else if(clazz.isAnnotationPresent(LKService.class)){
                    Object obj2 = clazz.newInstance();
                    LKService ls = clazz.getAnnotation(LKService.class);
                    String key2 = ls.value();
                    beans.put(key2,obj2);
                }else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void doScanPackage(String basePackage){
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for(File file : dir.listFiles()){
            if(file.isDirectory()){
                doScanPackage(basePackage + "." + file.getName());
            }else{
                classNames.add(basePackage + "." + file.getName().replace(".class","").trim());
            }
        }
    }

    public void doUrlMapping(){
        for (Map.Entry<String,Object> entry:beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(LKController.class)){
                LKRequestMapping lm1 = clazz.getAnnotation(LKRequestMapping.class);
                String classPath = lm1.value();

                Method[] methods= clazz.getMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(LKRequestMapping.class)){
                        LKRequestMapping lm2 = method.getAnnotation(LKRequestMapping.class);
                        String methodPath = lm2.value();
                        handlerMap.put(classPath+methodPath,method);
                    }
                }
            }
        }

    }

    public void doAutowired(){
        for (Map.Entry<String,Object> entry: beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(LKController.class)){
                Field[] fields = clazz.getDeclaredFields();
                for(Field field:fields){
                    if (field.isAnnotationPresent(LKAutowired.class)){
                        LKAutowired lka = field.getAnnotation(LKAutowired.class);
                        String key = lka.value();
                        Object ins = beans.get(key);

                        field.setAccessible(true);

                        try{
                            field.set(instance,ins);
                        }catch (IllegalAccessException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
        this.doPost(request,response);
    }
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        String uri = request.getRequestURI();
        String context = request.getContextPath();

        String path = uri.replace(context,"");
        Method method = (Method)handlerMap.get(path);

       int index1=path.indexOf("/");
       path= path.substring(index1+1);
      int index2=path.indexOf("/");
        LikeController instance = (LikeController) beans.get("/"+ path.substring(index1,index2));


        if(method == null || instance == null){
            return;
        }
        Object args[] = hand(request,response,method);

        try{
            method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
