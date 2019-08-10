package com.karen.controller;

import com.karen.annotation.LKAutowired;
import com.karen.annotation.LKController;
import com.karen.annotation.LKRequestMapping;
import com.karen.annotation.LKRequestParam;
import com.karen.service.LikeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author LIKE
 * @date 2019/8/9 9:00
 */
@LKController
@LKRequestMapping("/like")
public class LikeController {
    @LKAutowired("LikeServiceImpl")
    private LikeService likeService;
    @LKRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response
            ,@LKRequestParam("name") String name,
                      @LKRequestParam("age") String age){
        try{
//            String result = likeService.query(request.getParameter("name"),request.getParameter("age"));
            PrintWriter pw = response.getWriter();
            String result = likeService.query(name,age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
