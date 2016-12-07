package order.smzs.com.companyorder.Model;

import java.io.Serializable;

/**
 * Created by niexinrong on 16/5/10.
 */
public class Singleton implements Serializable {

        //用户 ID
        public String user_id;
        // 用户昵称
        public String user_nickname;
        // 用户头像
        public String user_img;
        // 用户名称
        public String user_Name;
        // 用户权限
        public String user_indetify;
        // 是否登录
        public boolean isLogin;
        // 服务器地址
        public String httpServer;
        // 活跃餐厅名称
        public String h_Name;
        // 活跃餐厅 ID
        public String h_indentify;
        // 连续签到天数
        public String e_con_day;


           private static class SingletonHolder {
               /**
                  * 单例对象实例
                  */
               static final Singleton INSTANCE = new Singleton();
            }

          public static Singleton getInstance() {

              // 内网测试环境
//              SingletonHolder.INSTANCE.httpServer = "http://192.168.19.47";
              // 外网环境
              SingletonHolder.INSTANCE.httpServer = "http://www.tiantianchisha.site";

              return SingletonHolder.INSTANCE;

            }

                /**
         * private的构造函数用于避免外界直接使用new来实例化对象
          */
                private Singleton() {
           }

                /**
          * readResolve方法应对单例对象被序列化时候
          */private Object readResolve() {
                return getInstance();
           }

    /**
     * 单例清空
     */

    public static Singleton cleanUserMessage(){

        SingletonHolder.INSTANCE.user_id = null;
        SingletonHolder.INSTANCE.user_nickname = null;
        SingletonHolder.INSTANCE.user_img = null;
        SingletonHolder.INSTANCE.user_Name = null;
        SingletonHolder.INSTANCE.user_indetify = null;
        SingletonHolder.INSTANCE.isLogin = false;

        return SingletonHolder.INSTANCE;
    }

}
