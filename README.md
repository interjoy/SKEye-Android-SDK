# SKEye-Android-SDK
SKEye-Android-SDK for Object Recognition Service 
###  更新日志
v1.0.0
- 发布常见物体识别、水果识别功能
###  目录介绍
- libs:包含识别接口的jar文件。
- SKEyeSDKDemo:物体识别的demo工程。
- 说明文档(SKEye-Android-SDK说明文档V1.0.0.pdf)
###  使用步骤
- 下载SDK文件包。
- 将Demo程序导入Android Studio。
- 打开"EyeResultActivity.java"类，配置好申请的Api Key和Api Secret。
- 运行代码。
- 更多使用介绍请参考 [《SKEye-Android-SDK说明文档V1.0.0》](https://github.com/interjoy/SKEye-Android-SDK.git)。
###  调用示例
```
(1)识别一个Bitmap图像
 // 直接调用 
 String respose = skEyeSDK.SKEyeSDK_Image(service_name, bmp);
 // 接口回调 
 skEyeSDK.SKEyeSDK_Image(service_name, bmp, new ImageCallback() {
        @Override
        public void recognitionInfo(String respose) {
        
        } 
 });
 (2)识别一个url（网络url、本地路径）图像
 // 直接调用 
 String respose = skEyeSDK.SKEyeSDK_Image(
    ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT, image_url); 
 // 接口回调 
 skEyeSDK.SKEyeSDK_Image(
    ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT, image_url,
        new ImageCallback() { 
            @Override 
            public void recognitionInfo(String respose) {
        
            }
 });
 (3)识别一个YUV数据格式图像
 // 直接调用 
 String respose = skEyeSDK.SKEyeSDK_Image(
 ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,yuvData,width,height);
 // 接口回调
 skEyeSDK.SKEyeSDK_Image(ConstConfig.SKEyeSDK_SERVICE_NAME_OBJECT,yuvData,
    width, height,
    new ImageCallback() { 
        @Override 
        public void recognitionInfo(String respose) {
            
        }
 });

```
###  SDK问题反馈
- SKEye开放平台QQ群：617518775
