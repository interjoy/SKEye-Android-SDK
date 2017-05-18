package com.interjoy.skeyesdkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.interjoy.model.Tag;
import com.interjoy.model.Tags;
import com.interjoy.skeyesdk.SKEyeSDK;
import com.interjoy.skutils.BitmapUtils;
import com.interjoy.skutils.ConstConfig;
import com.interjoy.skutils.CustomToast;
import com.interjoy.skutils.JsonFormat;
import com.interjoy.skutils.LogByInterjoyUtils;
import com.interjoy.skutils.SdcardUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import android.util.Log;

/**
 * 识别结果界面
 *
 * @author wangcan Interjoy
 */
public class EyeResultActivity extends Activity {
    //请将您申请的APP KEY和APP SECRET正确填写
	private String api_key = "";//APP KEY
	private String api_secret = "";//APP SECRET
    private ImageView image_pic, image_cancel, image_openLocalGallery,
            image_openCamera;// 展示图片，返回上个界面、打开本地相册、打开系统相机按钮控件
    private TextView tv_nextPic, tv_res, tv_name, tv_confi, tv_json_title,
            tv_json_content;
    private ProgressBar progress_loading;// 加载圈
    private String service_name = "";// 服务组别
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";// 临时图片文件名称
    private File tempFile;// 临时文件路径
    private SKEyeSDK skEyeSDK;// SDK
    private ListView lv_res;// 显示识别结果
    public List<Tag> tagsData = null;
    private Bitmap localBitmap = null; // 剪裁后的图片
    private Bitmap demoTestBmp;// 测试Bitmap
    private int[] objectsRes = null, fruitsRes = null;// 本地内置图片
    private Random random = new Random();// 随机数
    private MyAdapter myAdapter;// ListView适配器
    private Gson gson;
    private final int UPDATE_LIST = 0X11;// 更新UI
    private final int ERROR_RES = 0X12;// 提示错误信息
    // 裁剪后图片的宽(X)和高(Y),600 * 600。
    private static int output_X = 600;
    private static int output_Y = 600;
    // TODO 本地上传图片
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/localPic.jpg";// 存放临时图片路径字符串
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// 临时图片路径
    private String imagePath = "/storage/emulated/0/DCIM/Camera/IMG_20170220_172708.jpg";//本地路径
    private String imageUrl = "http://pic.58pic.com/58pic/12/92/83/39j58PIChF6.jpg";//网络图片url
    private int a = 0, b = 0;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = "";
            switch (msg.what) {
                case ERROR_RES:
                    // 完成主界面更新,拿到数据
                    if (!data.equals(""))
                        data = "";
                    data = (String) msg.obj;
                    myAdapter.notifyDataSetChanged();
                    CustomToast.showToast(getApplicationContext(), data, 1000);
                    tv_json_content.setText(JsonFormat.format(data));
                    progress_loading.setVisibility(View.INVISIBLE);// 隐藏
                    break;

                case UPDATE_LIST:
                    // 完成主界面更新,拿到数据
                    if (!data.equals(""))
                        data = "";
                    data = (String) msg.obj;
                    getListViewParams();
                    myAdapter.notifyDataSetChanged();
                    tv_json_content.setText(JsonFormat.format(data));
                    progress_loading.setVisibility(View.INVISIBLE);// 隐藏
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        initView();
        init();
        listen();
    }

    /**
     * 初始化数据
     */
    private void init() {
        Intent intent = getIntent();
        service_name = intent.getExtras().getString(
                ConstConfig.SKEyeSDK_SERVICE_NAME);
        // SDK初始化
        skEyeSDK = new SKEyeSDK(getApplicationContext());
        skEyeSDK.SKEyeSDKInit(api_key, api_secret);
        //如果图片不需要进行偏色处理，则可不调用该接口
//        skEyeSDK.SKEyeSDKColorCast(1, (float)0.9, 1);
        objectsRes = new int[]{R.drawable.object1, R.drawable.object2,
                R.drawable.object3, R.drawable.object4, R.drawable.object5};
        fruitsRes = new int[]{R.drawable.fruit1, R.drawable.fruit2,
                R.drawable.fruit3, R.drawable.fruit4, R.drawable.fruit5};
        gson = new Gson();
        tagsData = new ArrayList<Tag>();
        myAdapter = new MyAdapter(getApplicationContext(), tagsData);
        lv_res.setAdapter(myAdapter);
        switchPic();// 随机一张图进行识别
    }

    /**
     * 初始化视图
     */
    private void initView() {
        image_pic = (ImageView) findViewById(R.id.image_pic);
        image_cancel = (ImageView) findViewById(R.id.image_cancel);
        tv_nextPic = (TextView) findViewById(R.id.tv_nextPic);
        tv_res = (TextView) findViewById(R.id.tv_res);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_confi = (TextView) findViewById(R.id.tv_confi);
        tv_json_title = (TextView) findViewById(R.id.tv_json_title);
        tv_json_content = (TextView) findViewById(R.id.tv_json_content);
        image_openLocalGallery = (ImageView) findViewById(R.id.image_openLocalGallery);
        image_openCamera = (ImageView) findViewById(R.id.image_openCamera);
        lv_res = (ListView) findViewById(R.id.lv_res);
        progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
        progress_loading.setVisibility(View.INVISIBLE);// 隐藏
    }

    /**
     * 事件监听
     */
    private void listen() {
        image_cancel.setOnClickListener(listener);
        tv_nextPic.setOnClickListener(listener);
        image_openLocalGallery.setOnClickListener(listener);
        image_openCamera.setOnClickListener(listener);
    }

    /**
     * 单击事件监听
     */
    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 返回上个界面
                case R.id.image_cancel:
                    EyeResultActivity.this.finish();
                    break;

                // 随机切换一张图片（本地内置图片）
                case R.id.tv_nextPic:
                    switchPic();// 随机一张图进行识别
                    break;

                // 打开本地相册选择图片
                case R.id.image_openLocalGallery:
                    localGallery(image_openLocalGallery);
                    break;

                // 打开系统相机拍照
                case R.id.image_openCamera:
                    systemCamera(image_openCamera);
                    break;

                default:
                    break;
            }
        }
    };

    /*
     * 从相册获取
     */
    private void localGallery(View view) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_LOCALGALLERY
        startActivityForResult(intent, ConstConfig.PHOTO_REQUEST_LOCALGALLERY);
    }

    /*
     * 从相机获取
     */
    private void systemCamera(View view) {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (SdcardUtils.hasSdcard()) {

            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, ConstConfig.PHOTO_REQUEST_SYSTEMCAREMA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstConfig.PHOTO_REQUEST_LOCALGALLERY:
                // 从相册返回的数据
                if (data != null)
                    cropRawPhoto(data.getData());
                break;

            case ConstConfig.PHOTO_REQUEST_SYSTEMCAREMA:
                // 从相机返回的数据
                if (hasSdcard()) {
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    CustomToast.showToast(getApplicationContext(), "未找到存储卡，无法存储照片！", 1000);
                }

                break;

            case ConstConfig.PHOTO_REQUEST_CUT:
                // 裁剪后的数据
                if (data != null)
                    try {
                        localBitmap = BitmapFactory
                                .decodeStream(getContentResolver().openInputStream(
                                        imageUri));
                        setImage(localBitmap, image_pic);
                        progress_loading.setVisibility(View.VISIBLE);// 显示
                        sendBmpToServer(localBitmap);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                if (tempFile != null)
                    if (tempFile.exists())
                        // 将临时文件删除
                        tempFile.delete();
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param uri 图片路径
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, ConstConfig.PHOTO_REQUEST_CUT);
    }

    /**
     * 随机切换一张图片进行识别
     */
    private void switchPic() {
        progress_loading.setVisibility(View.VISIBLE);// 显示
        if (!service_name.equals("")) {
            LogByInterjoyUtils.logd("EyeResultActivity", "用户选择的识别服务名称是："
                    + service_name);
            if (service_name
                    .equals(ConstConfig.SKEyeSDK_SERVICE_NAME_FRUITS)) {
                int ram = random.nextInt(fruitsRes.length);
                InputStream is = getResources().openRawResource(fruitsRes[ram]);
                demoTestBmp = BitmapFactory.decodeStream(is);
                image_pic.setImageDrawable(getResources().getDrawable(
                        fruitsRes[ram]));
            } else if (service_name
                    .equals(ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT)) {
                int ram = random.nextInt(objectsRes.length);
                InputStream is = getResources()
                        .openRawResource(objectsRes[ram]);
                demoTestBmp = BitmapFactory.decodeStream(is);
                image_pic.setImageDrawable(getResources().getDrawable(
                        objectsRes[ram]));
            }
            // 发送Bitmap图进行识别
            sendBmpToServer(demoTestBmp);
            //发送图片url进行识别
//			sendUrlImageToServer(imageUrl);
            // 发送YUV数据进行识别
//			 sendYuvImageToServer();
        }
    }

    /**
     * 适配控件显示Bitmap
     *
     * @param bm        bitmap图
     * @param imageView 控件
     */
    private void setImage(Bitmap bm, ImageView imageView) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        if (bm.getWidth() > screenWidth) {
            bm = Bitmap.createScaledBitmap(bm, screenWidth, bm.getHeight()
                    * screenWidth / bm.getWidth(), true);
        }
        bm = BitmapUtils.zoomImg(bm, screenWidth);
        imageView.setImageBitmap(bm);
    }

    /**
     * 判断sdcard是否被挂载
     *
     * @return
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新UI
     *
     * @param res
     */
    private void updateUI(String res) {
        if (res.equals("") || res == null) {
            return;
        }
        if (tagsData.size() != 0)
            tagsData.clear();
        Tags tags = gson.fromJson(res, Tags.class);
        if (0 == tags.getError_code()) {
            List<Tag> data = tags.getTags();
            if (data == null) {
                return;
            }
            if (data.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    tagsData.add(data.get(i));
                }
            } else {
                tagsData.addAll(data);
            }
            // 发送至Handler更新UI
            Message message = myHandler.obtainMessage();
            message.what = UPDATE_LIST;
            message.obj = res;
            myHandler.sendMessage(message);
        } else {
            if (tagsData.size() != 0)
                tagsData.clear();
            // 发送至Handler更新UI
            Message message = myHandler.obtainMessage();
            message.what = ERROR_RES;
            message.obj = res/*tags.getError_msg()*/;
            myHandler.sendMessage(message);
        }
    }

    /**
     * 动态的算出ListView实际的LayoutParams 最关键的是算出LayoutParams.height
     */
    private void getListViewParams() {
        // 通过ListView获取其中的适配器adapter
        ListAdapter listAdapter = lv_res.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 声明默认高度为0
        int totalHeight = 0;
        // 遍历listView中所有Item，累加所有item的高度就是ListView的实际高度（后面会考虑分割线的高度）
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View item = listAdapter.getView(i, null, lv_res);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams lp = lv_res.getLayoutParams();
        lp.height = totalHeight
                + (lv_res.getDividerHeight() * (listAdapter.getCount() - 1));
        lv_res.setLayoutParams(lp);
    }

    /**
     * 自定义ListView适配器
     *
     * @author wangcan
     */
    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Tag> mData;

        public MyAdapter(Context context, List<Tag> result) {
            this.mData = result;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.result_item, null);
                viewHolder.tv_tag = (TextView) convertView
                        .findViewById(R.id.tv_tag);
                viewHolder.tv_confidence = (TextView) convertView
                        .findViewById(R.id.tv_confidence);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (mData.size() <= 0)
                Log.e("mData","mData长度为0");
            if (mData.size() > 0){
                viewHolder.tv_tag.setText(mData.get(position).getTag());
                viewHolder.tv_confidence.setText(mData.get(position)
                        .getConfidence() + "");
            }



            return convertView;
        }

    }

    class ViewHolder {
        TextView tv_tag, tv_confidence;
    }

    /**
     * 发送Bitmap到服务器
     *
     * @param bmp bitmap图
     */
    private void sendBmpToServer(final Bitmap bmp) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 直接调用
                    String respose = skEyeSDK.SKEyeSDK_Image(
                            service_name, bmp);
                    updateUI(respose);
                    // 接口回调
                    /*skEyeSDK.SKEyeSDK_Image(service_name, bmp,
                            new ImageCallback() {

								@Override
								public void recognitionInfo(String respose) {
									updateUI(respose);
								}
							});*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 使用图片url（网络url/本地路径）接口进行识别
     *
     * @param image_url 图片url
     *                  （网络url/本地路径）
     */
    private void sendUrlImageToServer(final String image_url) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 直接调用
                    String respose = skEyeSDK.SKEyeSDK_Image(
                            ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,
                            image_url);
                    updateUI(respose);
                    // 接口回调
                    /*skEyeSDK.SKEyeSDK_Image(
                            ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,
                            image_url, new ImageCallback() {

                                @Override
                                public void recognitionInfo(String respose) {
                                    updateUI(respose);
                                }
                            });*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送YUV图像
     *
     * @param yuvData yuv图像的byte数组
     * @param width   图像宽度
     * @param height  图像高度
     */
    private void sendYuvImageToServer(final byte[] yuvData, final int width,
                                      final int height) {
        // 获取YUV数据数组
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 直接调用
                    String respose = skEyeSDK.SKEyeSDK_Image(ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,
                            yuvData, width, height);
                    updateUI(respose);
                    // 接口回调
                   /* skEyeSDK.SKEyeSDK_Image(
                            ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,
                            yuvData, width, height, new ImageCallback() {

                                @Override
                                public void recognitionInfo(String respose) {
                                    // TODO Auto-generated method stub
                                    updateUI(respose);
                                }
                            });*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}