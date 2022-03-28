package com.duan.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duan.common.lang.Result;
import com.duan.entity.Blog;
import com.duan.service.BlogService;
import com.duan.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author duanyhui
 * @since 2022-03-28
 */
@RestController
public class BlogController {

    @Autowired
    BlogService blogService;
    //默认为1
    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage){
        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.success(pageData);
    }

    //@PathVariable动态路由
    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable Long id){
        Blog blog = blogService.getById(id);
        //判断是否为空 为空则断言异常
        Assert.notNull(blog,"该博客已被删除");
        return Result.success(blog);
    }
    //@Validated校验
    //@RequestBody
    //添加删除  木有id则添加 有id则编辑

    @RequiresAuthentication  //需要认证之后才能操作
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){

        //一个空对象用于赋值
        Blog temp = null;
        //如果有id则是编辑
        if(blog.getId() != null){
            temp = blogService.getById(blog.getId());//将数据库的内容传递给temp
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().longValue()== ShiroUtil.getProfile().getId().longValue(),"没有编辑权限");

        } else {
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }
        //将blog的值赋给temp 忽略 id userid created status 引用于hutool

        //    blog是前端传递过来的值，是要编辑的博客的信息，通过这个copy方法，将
//    前端传递过来的值（blog）
//    添加到temp对象中，同时要忽略在上方已赋过值的4个属性
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);

        return Result.success(null);
    }

    //@PathVariable动态路由
    @RequiresAuthentication  //需要认证之后才能操作
    @PostMapping("/blogdel/{id}")
    public Result del(@PathVariable Long id){
        boolean b = blogService.removeById(id);
        //判断是否为空 为空则断言异常
        if(b==true){

            return Result.success("文章删除成功");
        }else{
            return Result.fail("文章删除失败");
        }
    }

}


