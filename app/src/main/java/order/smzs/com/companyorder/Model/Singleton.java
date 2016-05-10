package order.smzs.com.companyorder.model;

import java.io.Serializable;

/**
 * Created by niexinrong on 16/5/10.
 */
public class Singleton implements Serializable {

        public String user_id;
        public String user_nickname;
        public String user_img;
        public String user_Name;
        public String user_indetify;
        public boolean isLogin;

        public String httpServer;

           private static class SingletonHolder {
               /**
                  * 单例对象实例
                  */
               static final Singleton INSTANCE = new Singleton();
            }

          public static Singleton getInstance() {

              // 内网测试环境
              SingletonHolder.INSTANCE.httpServer = "http://192.168.19.47";
              // 外网环境
            //  SingletonHolder.INSTANCE.httpServer = "http://www.tiantianchisha.site";

              return SingletonHolder.INSTANCE;

            }

                /**
         * private的构造函数用于避免外界直接使用new来实例化对象
          */
                private Singleton() {
           }

                /**
          * readResolve方法应对单例对象被序列化时候
          */
                private Object readResolve() {
                return getInstance();
           }
    }
