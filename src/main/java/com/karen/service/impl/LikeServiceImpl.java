package com.karen.service.impl;

import com.karen.annotation.LKService;
import com.karen.service.LikeService;

/**
 * @author LIKE
 * @date 2019/8/9 9:02
 */
@LKService("LikeServiceImpl")
public class LikeServiceImpl implements LikeService {
    @Override
    public String query(String name,String age) {
        return "{name="+ name +",age="+ age +"}";
    }
}
