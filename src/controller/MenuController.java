package controller;

import com.alibaba.fastjson.JSON;
import db.DbConnection;
import db.MenuList;
import db.OrderList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/13 0013.
 */
@RestController
@RequestMapping(value = "/menu")
public class MenuController {
//    点菜
    @RequestMapping(value = "/addoder", produces = "text/html;charset=UTF-8")
    public String order(@RequestParam int user_id, @RequestParam int menu_id) {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        OrderList orderList = new OrderList();
        orderList.setUserid(user_id);
        String s = " from MenuList where id=?";
        java.util.List<MenuList> list = session.createQuery(s).setString(0, menu_id + "").list();
        orderList.setDetail(list.get(0).getDetail());
        orderList.setImageUrl(list.get(0).getImageUrl());
        orderList.setMenuName(list.get(0).getName());
        orderList.setPrice(list.get(0).getPrice());
        orderList.setStoreId(list.get(0).getStoreId());
        session.save(orderList);
        transaction.commit();
        session.close();
        Map<String, String> map = new HashMap<>();
        map.put("resultcode", "0000");
        map.put("info", "操作成功");
        return JSON.toJSONString(map);
    }
//菜品详情
    @RequestMapping(value = "/showfood", produces = "text/html;charset=UTF-8")
    public static String foodshow(@RequestParam int food_id) {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        String hql = "from MenuList where id=?";
//       java.util.List<MenuList> list= session.createQuery(hql).setString(0,food_id+"").list();

        Map<String, Object> map = new HashMap<>();
        map.put("resultcode", "0000");
        map.put("info", "");
        map.put("foodinfo", session.createQuery(hql).setString(0, food_id + "").list());
        transaction.commit();
        session.close();
        return JSON.toJSONString(map);
    }
//已点菜品
    @RequestMapping(value = "/listorder", produces = "text/html;charset=UTF-8")
    public String listOrder(@RequestParam int store_id, @RequestParam int user_id) {
        Session session = DbConnection.getSession();
        Transaction transaction = session.beginTransaction();
        String hql = "from OrderList where storeId=? and userid=?";
        Map<String, Object> map = new HashMap<>();
        map.put("resultcode", "0000");
        map.put("info", session.createQuery(hql).setString(0, store_id + "").setString(1, user_id + "").list());
        transaction.commit();
        session.close();
        return JSON.toJSONString(map);
    }
}
