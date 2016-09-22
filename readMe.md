#服务器接口文档

https://coding.net/u/sunjianfei12388/p/zjb_parent/git/tree/master

测试服务器:  http://test.idrv.com.cn/zjb

正式服务器:  http://zjb.idrv.com.cn


#七牛服务器文档
http://developer.qiniu.com/docs/v6/api/reference/fop/image/imageview2.html

#公共参数文档
https://coding.net/u/sunjianfei12388/p/zjb_parent/git/tree/master
  
1.ui->adapter下面包含了各种适配器，除了传统的ListView\RecyclerView\ViewPager的适配器之外，还包括了一些多方法接口的空实现
    比如：TextWatcher的默认实现TextWatcherAdapter
2.常用插件下载：
  2.1 Parcelable:https://plugins.jetbrains.com/files/7332/19984/android-parcelable-intellij-plugin.jar
  2.2 Gson : https://plugins.jetbrains.com/files/7654/21015/GsonFormat1.2.jar
3.Rxjava注意事项
  3.1 Observable对象的doOnNext方法优先于subscribe方法的onNext执行