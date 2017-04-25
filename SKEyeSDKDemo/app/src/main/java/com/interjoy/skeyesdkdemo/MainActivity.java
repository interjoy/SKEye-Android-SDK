package com.interjoy.skeyesdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.interjoy.skutils.ConstConfig;
import com.interjoy.skutils.LogByInterjoyUtils;

/**
 * SKEyeSDK Demo
 * @author wangcan Interjoy
 *
 */
public class MainActivity extends Activity {
	private ImageView image_ObjectEye, image_FruitsEye;// 水果识别、常见物品识别控件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		initView();
		listen();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		image_ObjectEye = (ImageView) findViewById(R.id.image_object);
		image_FruitsEye = (ImageView) findViewById(R.id.image_fruits);
	}

	/**
	 * 事件监听
	 */
	private void listen() {
		image_ObjectEye.setOnClickListener(listener);
		image_FruitsEye.setOnClickListener(listener);
	}

	/**
	 * 单击事件监听
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 常见物品识别
			case R.id.image_object:
				goResultActivity(ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT);
				LogByInterjoyUtils.logd("MainActivity", "选择常见物品子集");
				break;

			// 水果识别
			case R.id.image_fruits:
				goResultActivity(ConstConfig.SKEyeSDK_SERVICE_NAME_FRUITS);
				LogByInterjoyUtils.logd("MainActivity", "选择水果子集");
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 跳转到识别结果界面
	 * 
	 * @param type
	 *            识别子集类型，0：水果识别子集，1：常见物品识别子集
	 */
	private void goResultActivity(String type) {
		Intent intent = new Intent(this, EyeResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstConfig.SKEyeSDK_SERVICE_NAME, type);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
