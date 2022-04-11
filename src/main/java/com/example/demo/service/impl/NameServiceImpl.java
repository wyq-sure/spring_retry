package com.example.demo.service.impl;

import com.example.demo.service.NameService;
import org.springframework.stereotype.Service;

/**
 * @author wangyongqiang
 */
@Service
public class NameServiceImpl implements NameService {

    @Override
    public String getName(){
        return "富兰克林";
    }
}
