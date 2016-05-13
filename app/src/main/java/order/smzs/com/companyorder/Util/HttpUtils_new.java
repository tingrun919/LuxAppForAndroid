package order.smzs.com.companyorder.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpUtils_new implements Runnable{

	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;

	private String url;
	private CallbackListener listener;
	private JSONObject jsonObject;
	private String res;
	private Context context;

	public HttpUtils_new() {

	}

	@Override
	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpUtils_new.DID_START));
		byte[] data = JsonToString(jsonObject).toString().getBytes();// 获得请求体
		// String urlPath = "http://192.168.19.47/FoodList.php";
		try {
				URL strUrlPath = new URL(url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) strUrlPath.openConnection();
				httpURLConnection.setConnectTimeout(3000); // 设置连接超时时间
				httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
				httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
				httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
				httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
				// 设置请求体的类型是文本类型
				httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				// 设置请求体的长度
				httpURLConnection.setRequestProperty("Content-Length",String.valueOf(data.length));
				// 获得输出流，向服务器写入数据
				OutputStream outputStream = httpURLConnection.getOutputStream();
				outputStream.write(data);

				int response = httpURLConnection.getResponseCode(); // 获得服务器的响应码
				if (response == HttpURLConnection.HTTP_OK) {
					InputStream inptStream = httpURLConnection.getInputStream();
					res = dealResponseResult(inptStream); // 处理服务器的响应结果
					this.sendMessage(res);
				}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			return "err: " + e.getMessage().toString();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			return "err: " + e1.getMessage().toString();
		}
	}

	private static final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case HttpUtils_new.DID_START:{

					break;
				}

				case HttpUtils_new.DID_SUCCEED:{
					CallbackListener listener = (CallbackListener) msg.obj;
					Object data = msg.getData();
					if (listener != null) {
						if (data != null) {
							Bundle bundle = (Bundle) data;
							String result = bundle.getString("res");
							listener.callBack(result);
						}
					}
					break;
				}

				case HttpUtils_new.DID_ERROR:{

					break;
				}
				default:super.handleMessage(msg);
			}

		}
	};

	public HttpUtils_new(String url, JSONObject jsonObject, CallbackListener listener){
		this.url = url;
		this.jsonObject = jsonObject;
		this.listener = listener;
	}

	public HttpUtils_new initWith(String url, JSONObject jsonObject, CallbackListener listener, Context context){

		if (order.smzs.com.companyorder.model.NetUtils.isConnected(context)){
			this.url = url;
			this.jsonObject = jsonObject;
			this.listener = listener;
			this.context = context;
			return this;
		}else {
			Toast.makeText(context,"无网络！",Toast.LENGTH_SHORT).show();
			return null;
		}
	}




	public interface CallbackListener {
		void callBack(String result);
	}

	public static String JsonToString(JSONObject param){
		String res = "";
		try {
			res = URLEncoder.encode(param.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/*
 	 * Function : 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null; // 存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		return resultData;
	}

	private void sendMessage(String result) {
		Message message = Message.obtain(handler, DID_SUCCEED, listener);
		Bundle data = new Bundle();
		data.putString("res", result);
		message.setData(data);
		handler.sendMessage(message);
	}


}
