package controller;

import com.alibaba.fastjson.JSON;
import db.DbConnection;
import db.MenuList;
import db.SToreInfo;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/3/18 0018.
 */
@RestController
@RequestMapping(value = "/store")
public class StoreController {
//    餐厅登录
    @RequestMapping(value = "/login", produces = "text/html;charset=UTF-8")
    public String login(@RequestParam String name, @RequestParam String pass) {
        Session session = DbConnection.getSession();
        session.beginTransaction();
        String s = "from SToreInfo where store_name=? and pass=?";
        List<SToreInfo> list = session.createQuery(s).setString(0, name).setString(1, pass).list();
        session.getTransaction().commit();
        session.close();
        Map<String, Object> map = new HashMap<>();
        if (list == null || list.size() == 0) {
            map.put("info", "用户名或者密码不正确");
            map.put("resultcode", "-1");
        } else {
            map.put("list", list.get(0));
            map.put("info", "登录验证成功");
            map.put("resultcode", "0000");
        }
        String result = JSON.toJSONString(map);
        return result;
    }
//加载餐厅菜品
    @RequestMapping(value = "/getmenulist", produces = "text/html;charset=UTF-8")
    public String getmenuList(@RequestParam int store_id) {
        Session session = DbConnection.getSession();
        session.beginTransaction();
        Map<String, Object> map = new HashMap<>();
        String s = "from MenuList where store_id=?";
        map.put("menulist", session.createQuery(s).setString(0, store_id + "").list());
        session.getTransaction().commit();
        session.close();
        return JSON.toJSONString(map);
    }

    //    @RequestMapping (value = "/addmenu",produces = "text/html;charset=UTF-8")
//    public  String addMenu(@RequestParam String name,@RequestParam String desc,@RequestParam){
//
//    }
//    删除菜品
    @RequestMapping(value = "/deletemenu", produces = "text/html;charset=utf-8")
    public String deleteMenu(@RequestParam int id) {
        Session session = DbConnection.getSession();
        session.beginTransaction();
        String h = "delete from MenuList where id=?";
        session.createQuery(h).setString(0, id + "");
        session.getTransaction().commit();
        session.close();
        Map<String, String> map = new HashMap<>();
        map.put("resultcode", "0000");
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/uploadimg", produces = "text/html;charset=utf-8")
    public String getmenuimg(@RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam String desc,@RequestParam String name,@RequestParam int store_id
    ,@RequestParam String price) {
      Map<String,Object> map=new HashMap<>();
        String uploadUrl ="D:\\Apache2.2\\htdocs\\img\\";
        String fileName = imageFile.getOriginalFilename();
        File dir = new File(uploadUrl);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        System.out.print("文件上传到" + uploadUrl);
        UUID uuid=UUID.randomUUID();
        File targetFile = new File(uploadUrl +uuid+ fileName);
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            imageFile.transferTo( targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("文件上传到"+targetFile);
//
        map.put("code","0000");
        map.put("imgurl","http://192.168.0.254:80/img/"+uuid+fileName);
        MenuList menuList=new MenuList();
        menuList.setDetail(desc);
        menuList.setName(name);
        menuList.setStoreId(store_id);
        menuList.setImageUrl("http://192.168.0.254:80/img/"+uuid+fileName);
        menuList.setPrice(price);
        Session session=DbConnection.getSession();
        session.beginTransaction();
        session.save(menuList);
        session.getTransaction().commit();
        session.close();
        return JSON.toJSONString(map);
    }

}
