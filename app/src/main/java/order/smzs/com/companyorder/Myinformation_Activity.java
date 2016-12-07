package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import order.smzs.com.companyorder.image.SmartImageView;
import order.smzs.com.companyorder.Model.Singleton;
import order.smzs.com.companyorder.Util.HttpUtils_new;
import order.smzs.com.companyorder.Util.ThreadPoolUtils;

/**
 * Created by Tarn on 2016/5/5.
 */
public class Myinformation_Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView my_tx,my_qm;
    private EditText my_nc;
    private Button up_btn;
    private SmartImageView imageView;
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private String resValue, new_nname;


    public static void startAct(Activity context) {
        Intent intent = new Intent(context, Myinformation_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinformation_main);
        setTitle("个人信息");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

    }

    private void initView() {
        my_tx = (TextView) findViewById(R.id.my_tx);
        my_nc = (EditText) findViewById(R.id.my_nc);
        my_qm = (TextView) findViewById(R.id.my_qm);
        up_btn = (Button) findViewById(R.id.btn_udmyi);
        imageView = (SmartImageView) findViewById(R.id.my_pic);
        my_tx.setOnClickListener(this);
        up_btn.setOnClickListener(this);

        String img_pic = Singleton.getInstance().user_img;
        imageView.setImageUrl(img_pic);

        my_nc.setText(Singleton.getInstance().user_nickname);

        my_qm.setText(Singleton.getInstance().h_Name);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_tx://调用系统相册
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType("image/*");
                startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
                break;
            case R.id.btn_udmyi://修改个人信息
                startUpdate();
                break;
        }

    }

    private void startUpdate() {

        new_nname = my_nc.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Singleton.getInstance().user_id);
            jsonObject.put("user_img", Singleton.getInstance().user_img);
            jsonObject.put("user_nickname",new_nname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer,"/UpdataUsersImg.php"), jsonObject, new BackListener2(),Myinformation_Activity.this);
        ThreadPoolUtils.execute(httpUtils_new);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                // 做非空判断，当觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data != null) {
                    System.out.println("11================");
                    startPhotoZoom(data.getData());
                } else {
                    System.out.println("================");
                }
                break;
            case PHOTO_REQUEST_CUT:// 返回的结果
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                    imageView.setImageBitmap(photo); //把图片显示在ImageView控件上
                    saveBitmap(photo);
                    //上传图片
                    new Thread(networkTask).start();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            // TODO
            // 在这里进行 http request.网络请求相关操作
            String path = Environment.getExternalStorageDirectory().getPath() + "/TTCS/headerImage/"+ resValue + ".JPEG";
            File file1 = new File(path);
            String url = "http://192.168.19.47/upLoadPic.php";
            String resUrl = UploadUtil.uploadFile(file1,url);
            try {
                JSONObject js= new JSONObject(resUrl);
                String res = "http://192.168.19.47/Jehovah/"+js.getString("result");
                Singleton.getInstance().user_img = res;
                saveNetPic(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    private void saveNetPic(String res) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Singleton.getInstance().user_id);
            jsonObject.put("user_img", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer,"/UpdataUsersImg.php"), jsonObject, new BackListener(),Myinformation_Activity.this);
        ThreadPoolUtils.execute(httpUtils_new);
    }

    private void saveBitmap(Bitmap photo) {

        File file;
        String path = Environment.getExternalStorageDirectory().getPath() + "/TTCS/headerImage/";

        File file1 = new File(path);
        if (!file1.exists()) {
            try {
                //按照指定的路径创建文件夹
                file1.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        file = new File(path + getRandomNum()+".JPEG");
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(stream.toByteArray());
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取随机数
     * @return 随机16位数
     */
    public String getRandomNum(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        int resNum = (int) (Math.random()*900000+100000);

        resValue = str+String.valueOf(resNum);
        return resValue;
    }

    /**
     * 裁剪选择好的图片
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        System.out.println("22================");
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    public static class UploadUtil {
        private static final String TAG = "uploadFile";
        private static final int TIME_OUT = 10 * 1000; // 超时时间
        private static final String CHARSET = "utf-8"; // 设置编码
        /**
         * 上传文件到服务器
         * @param file 需要上传的文件
         * @param RequestURL 请求的rul
         * @return 返回响应的内容
         */
        public static String uploadFile(File file, String RequestURL) {
            int res=0;
            String result = null;
            String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; // 内容类型

            try {
                URL url = new URL(RequestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); // 允许输入流
                conn.setDoOutput(true); // 允许输出流
                conn.setUseCaches(false); // 不允许使用缓存
                conn.setRequestMethod("POST"); // 请求方式
                conn.setRequestProperty("Charset", CHARSET); // 设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);

                if (file != null) {
                    /**
                     * 当文件不为空时执行上传
                     */
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    /**
                     * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名
                     */

                    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+ file.getName() + "\"" + LINE_END);
                    sb.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                            .getBytes();
                    dos.write(end_data);
                    dos.flush();
                    /**
                     * 获取响应码 200=成功 当响应成功，获取响应的流
                     */
                    res = conn.getResponseCode();
                    Log.e(TAG, "response code:" + res);
                    if (res == 200) {
                        Log.e(TAG, "request success");
                        InputStream input = conn.getInputStream();
                        StringBuffer sb1 = new StringBuffer();
                        int ss;
                        while ((ss = input.read()) != -1) {
                            sb1.append((char) ss);
                        }
                        result = sb1.toString();
                        Log.e(TAG, "result : " + result);
                    } else {
                        Log.e(TAG, "request error");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    class BackListener implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//服务器返回错误
                        Toast.makeText(Myinformation_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(Myinformation_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//设置头像成功
                        Toast.makeText(Myinformation_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Myinformation_Activity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class BackListener2 implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//服务器返回错误
                        Toast.makeText(Myinformation_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(Myinformation_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//修改个人信息成功
                        Toast.makeText(Myinformation_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                        Singleton.getInstance().user_nickname = new_nname;
                        my_nc.setText(Singleton.getInstance().user_nickname);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Myinformation_Activity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
