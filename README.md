# SKEye-Android-SDK
SKEye-Android-SDK for Object Recognition Service 
###  更新日志
v1.0.4
- 扩充图像识别接口，加入image_id参数，方便确认请求顺序和返回顺序是否一致


v1.0.3
- 优化网络请求模块


v1.0.2
- 优化网络请求模块


v1.0.1
- 增加图像偏色处理接口（调整图像偏色：偏红、偏绿、偏蓝）
  
  备注：如需使用该接口，请在SDK初始化后调用一次即可。如果图像不需要偏色处理，则不需要调用该接口。

   public void SKEyeSDKColorCast(float rRateValue, float gRateValue, 
        float bRateValue)；


v1.0.0
- 发布常见物体识别、水果识别功能
###  目录介绍
- libs:包含识别接口的jar文件。
- SKEyeSDKDemo:物体识别的demo工程。
- 说明文档[《SKEye-Android-SDK说明文档V1.0.3》](https://github.com/interjoy/SKEye-Android-SDK/blob/master/SKEye-Android-SDK%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3V1.0.3.pdf)
###  使用步骤
- 下载SDK文件包。
- 将Demo程序导入Android Studio。
- 打开"EyeResultActivity.java"类，配置好申请的Api Key和Api Secret。
- 运行代码。
- 更多使用介绍请参考 [《SKEye-Android-SDK说明文档V1.0.3》](https://github.com/interjoy/SKEye-Android-SDK/blob/master/SKEye-Android-SDK%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3V1.0.3.pdf)。
###  调用示例
```
(1)识别一个Bitmap图像
 // 直接调用
 String result = skEyeSDK.SKEyeSDK_Image(
    service_name, bmp);
 
 // 接口回调
 skEyeSDK.SKEyeSDK_Image(
   service_name, bmp, new ImageCallback() {
	@Override
	public void recognitionInfo(String result) {
	    // 在这里可以对识别结果result进行处理
	}
 });
 
 skEyeSDK.SKEyeSDK_Image(
   image_id, service_name, bmp,
   new ImageCallback() {
	 @Override
	 public void recognitionInfo(String result) {
	    // 在这里可以对识别结果result进行处理
	 }
 });

 (2)识别一个url（网络url、本地路径）图像
 // 直接调用
 String result = skEyeSDK.SKEyeSDK_Image(
    service_name, image_url);
 
 // 接口回调
 skEyeSDK.SKEyeSDK_Image(service_name,
   image_url, new ImageCallback() {
	@Override
	public void recognitionInfo(String result) {
		// 在这里可以对识别结果result进行处理
	}
 });
 
 skEyeSDK.SKEyeSDK_Image(image_id,
   service_name, image_url, new ImageCallback() {
	@Override
	public void recognitionInfo(String result) {
		// 在这里可以对识别结果result进行处理
	}
 });
 
 (3)识别一个YUV数据格式图像
 // 直接调用
 String result = skEyeSDK.SKEyeSDK_Image(
    service_name, imageYUVData, bmpW, bmpH);

 // 接口回调
 skEyeSDK.SKEyeSDK_Image(service_name, image_YUVData, 
   image_width, image_height, new ImageCallback() {
	@Override
	public void recognitionInfo(String result) {
		// 在这里可以对识别结果result进行处理
	}
 });

 skEyeSDK.SKEyeSDK_Image(image_id, service_name, image_YUVData, image_width, image_height, 
   new ImageCallback() {
	@Override
	public void recognitionInfo(String result) {
		// 在这里可以对识别结果result进行处理
	}
 });


```
###  SDK问题反馈
- SKEye开放平台QQ群：617518775
