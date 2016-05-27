package order.smzs.com.companyorder.download;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import order.smzs.com.companyorder.R;

public class MainActivity3 extends FragmentActivity {

    private static final String TAG = MainActivity3.class.getSimpleName();
    private MaterialDialog dialog;
    private int max;
    private MaterialDialog downloadDialog;
    private DownloadUtil mDownloadUtil;
    private String fileName = "temp.apk";
    private String localPath = "/mnt/sdcard/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Bundle bundle = this.getIntent().getExtras();
        String name = bundle.getString("result");

        String [] temp = null;
        temp = name.split("-");

        if (temp.length == 2){
            fileName = temp[1];
        }

        String urlString = "http://www.tiantianchisha.site/FileDownLoad.php?path="+name;



        mDownloadUtil = new DownloadUtil(1, localPath, fileName, urlString,
                this);
        mDownloadUtil.setOnDownloadListener(new DownloadUtil.OnDownloadListener() {

            @Override
            public void downloadStart(int fileSize) {
                // TODO Auto-generated method stub
                Log.w(TAG, "fileSize::" + fileSize);
                max = fileSize;
                dialog.setMaxProgress(fileSize/1024);
            }

            @Override
            public void downloadProgress(int downloadedSize) {
                // TODO Auto-generated method stub
                Log.w(TAG, "Compelete::" + downloadedSize);
                dialog.setProgress(downloadedSize/1024);
                if (downloadedSize+1 == max){
                    downloadDialog.dismiss();
                    showAlert("下载完成", "快安装天天吃啥吧！");
                }
            }

            @Override
            public void downloadEnd() {
                // TODO Auto-generated method stub
                Log.w(TAG, "ENd");
            }
        });

        downloadDialog = new MaterialDialog.Builder(MainActivity3.this)
                .title("版本更新")
                .content("正在下载...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 150, true)
                .canceledOnTouchOutside(false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDownloadUtil.pause();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                          dialog = (MaterialDialog) dialogInterface;

                    }
                })
                .show();

        mDownloadUtil.start();

    }

    public void showAlert(String title, String context){

        new MaterialDialog.Builder(MainActivity3.this)
                .iconRes(R.drawable.ic_menu_camera)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(title)
                .content(context)
                .positiveText("确定")
                .negativeText("取消")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which.name() == "POSITIVE"){
                            AutoInstall.setUrl(localPath+fileName);
                            AutoInstall.install(MainActivity3.this);
                        }else{
                            finish();
                        }
                    }
                })
                .canceledOnTouchOutside(false)
                .show();
    }


}
