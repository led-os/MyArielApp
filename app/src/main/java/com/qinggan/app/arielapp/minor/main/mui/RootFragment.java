package com.qinggan.app.arielapp.minor.main.mui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.main.utils.Tools;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RootFragment extends AbstractBaseFragment {
    private View rootview;
    private TextView sure_btn;
    private FragmentManager fragmentManager;
    private LocalStorageTools localStorageTools;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        //初始化本地存储
        localStorageTools = new LocalStorageTools(getActivity());
        rootview = inflater.inflate(R.layout.activity_root, container, false);
        sure_btn=(TextView)rootview.findViewById(R.id.sure_btn);//确定
        sure_btn.setOnClickListener(this);
//        if(Tools.getSdkVersionSix()) {
//            if (!Settings.System.canWrite(getActivity())) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
//                        Uri.parse("package:" + getActivity().getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } else {
//                //有了权限，你要做什么呢？具体的动作
//            }
//        }
        return rootview;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sure_btn:
                localStorageTools.setString("firstMark","yes");//设置过权限
                initPermission();
                break;
        }
    }

    //申请权限
    private void initPermission() {
        if(Tools.getSdkVersionSix()){ //判断SDK是否在6.0以上
            //权限数组   手机状态，联系人，存储权限,定位
            String [] permissions=new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION};
            List<String> mPermissionList=new ArrayList<>();
            for (int i=0;i<permissions.length;i++){
                if(ContextCompat.checkSelfPermission(getActivity(),permissions[i])!=PERMISSION_GRANTED){
                    mPermissionList.add(permissions[i]);
                }

            }
            if(mPermissionList.isEmpty()){//全部允许


                fragmentManager.popBackStack();

            }else{//存在未允许的权限

                String[] permissionsArr=mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(getActivity(),permissionsArr,101);


            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionsDismiss =false;//有权限没有通过
        switch (requestCode){
            case 101:

                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i]==-1){
                        hasPermissionsDismiss=true;
                    }
                }
                if(hasPermissionsDismiss){
                    Toast.makeText(getActivity(),"您有权限未允许，可能有些功能不能使用！",Toast.LENGTH_SHORT).show();


                }else{
                    //权限通过做下一步操作

                }

                fragmentManager.popBackStack();
                break;
        }
    }
}
