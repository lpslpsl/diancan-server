package controller;

import com.alibaba.fastjson.JSON;
import com.tencent.xinge.XingeApp;
import db.Comments;
import db.DbConnection;
import db.StoreList;
import db.UserEntity;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    long id = 2100190408;
    String key = "A7Q95EF6TB5N";

    //    登录
    @RequestMapping(value = "/login", produces = "text/html;charset=UTF-8")
    public String Login(@RequestParam String name, @RequestParam String pass) {
        Session session = DbConnection.getSession();
        session.beginTransaction();
        //登录测试
        String hql = "from UserEntity where username=? and password=?";
        List<UserEntity> list = session.createQuery(hql).setString(0, name).setString(1, pass).list();
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
        return JSON.toJSONString(map);
    }

    //注册
    @RequestMapping(value = "/register", produces = "text/html;charset=UTF-8")
    public String register(@RequestParam String name, @RequestParam String pass) {
        Session session = DbConnection.getSession();
        Map<String, String> map = new HashMap<>();
        try {
            session.beginTransaction();
            UserEntity user = new UserEntity();
            user.setUsername(name);
            user.setPassword(pass);
            session.save(user);
            session.getTransaction().commit();
            map.put("resultcode", "0000");
            map.put("info", "成功");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.beginTransaction().rollback();
            map.put("resultcode", "-12");
            map.put("info", "失败");
        } finally {
            session.close();
        }
        String result = JSON.toJSONString(map);
        return result;
    }

    //获取餐厅列表
    @RequestMapping(value = "/getstoreList", produces = "text/html;charset=UTF-8")
    public String getStoreList() {
        Map<String, Object> map = new HashMap<>();
//        String hql = "from StoreList";
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        map.put("storelist", session.createCriteria(StoreList.class).list());
        map.put("resultcode", "0000");
        transaction.commit();
        String result = JSON.toJSONString(map);
        session.close();
        return result;
    }

    //    获取店铺对应得
//菜单列表
    @RequestMapping(value = "/getMenuList", produces = "text/html;charset=UTF-8")
    public String getMenuList(@RequestParam int store_id) {
        Map<String, Object> map = new HashMap<>();
        String hql = "from MenuList where storeId=?";
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        map.put("menulist", session.createQuery(hql).setString(0, store_id + "").list());
        map.put("resultcode", "0000");
        transaction.commit();
        session.close();
        String result = JSON.toJSONString(map);
        return result;
    }

    //
    @RequestMapping(value = "/addmenu", produces = "text/html;charset=UTF-8")
    public String addmenu() {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        String hql = "from StoreList ";
        Map<String, String> map = new HashMap<>();
        List<String> list = (List<String>) session.createQuery(hql).list();
        map.put("storename", list.get(0));
        transaction.commit();
        String result = JSON.toJSONString(map);
        System.out.print(map.toString() + "\n");
        System.out.print(result);
        session.close();
        return result;
    }

    //    结账
    @RequestMapping(value = "/checkout", produces = "text/html;charset=UTF-8")
    public String checkOut(@RequestParam int myid, @RequestParam String store, @RequestParam String money) {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        Map<String, Object> map = new HashMap<>();
        String s = "from UserEntity where id=?";
        List<UserEntity> userEntity = session.createQuery(s).setInteger(0, myid).list();
        String myname = userEntity.get(0).getUsername();
        JSONObject jsonObject = XingeApp.pushAccountAndroid(id, key, "我是" + myname, "请求结账,本次消费了" + money, "diancan" + store);
        if (jsonObject.getInt("ret_code") == 0) {
            map.put("code", "0000");
            map.put("info", "已通知餐厅结账");
        } else {
            map.put("code", "-1");
//            map.put("info", jsonObject.getString("err_msg"));
            map.put("info", "商家暂时不在线");
        }
        transaction.commit();
        session.close();
        return JSON.toJSONString(map);
    }

    //    评论
    @RequestMapping(value = "/addcomment", produces = "text/html;charset=utf-8")

    public String addcomment(@RequestParam String content, @RequestParam int menu_id
            , @RequestParam double rating, @RequestParam int store_id, @RequestParam int user_id) {
        Session session = DbConnection.getSession();
        Comments comments = new Comments();
        comments.setContent(content);
        comments.setMenuId(menu_id);
        comments.setRatingstar(rating);
        comments.setStoreId(store_id);
        comments.setUserId(user_id);
        Map<String, Object> map = new HashMap<>();
        try {
            session.save(comments);
            map.put("code", "0000");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", "-1");
        }
        return JSON.toJSONString(map);
    }

    //    修改密码
    @RequestMapping(value = "/changepass", produces = "text/html;charset=utf-8")
    public String chagedPass(@RequestParam String oldpass, @RequestParam String newpass, @RequestParam String user_id) {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        Map<String, Object> map = new HashMap<>();
        String pass = session.createQuery("select password from UserEntity where id=" + user_id).list().get(0).toString();
        if (oldpass.equals(pass)) {
            String hql = "update UserEntity e set e.password=? where id=" + user_id;
            Query query = session.createQuery(hql).setString(0, newpass);
            query.executeUpdate();
            transaction.commit();
            map.put("code", "0000");
            map.put("info", "操作成功");
        } else {
            map.put("code", "-1");
            map.put("info", "旧密码错误");
        }
        session.close();
        return JSON.toJSONString(map);
    }
}
