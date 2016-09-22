   *此框架基于universal-image-loader源码,做了更深层次的封装和修改，相对于原来的封装，使用更简单，样式更丰富，
相比较原来的universal-image-loader,做了如下改进：
1.支持类似Glide的功能，即针对同一个url,缓存不同尺寸的图片，根据视图大小选择加载对应尺寸的缓存图片(以空间换时间)
2.修改了图片缓存到sd卡的缓存策略，利用ConcurrentLinkedQueue缓存异步任务，更加合理(详见CustomDiskCache,
  原来是加载图片的过程当中，获取到图片stream,然后生成bitmap,bitmap保存到本地之后，再将bitmap返回进行显示，
  这样就会造成时间浪费，现修改了bitmap保存到本地的key缓存到ConcurrentLinkedQueue当中，启动一个专门的子线程
  来轮询ConcurrentLinkedQueue,并通过轮询得到的key从MemoryCache中获取bitmap,保存)
3.利用builder模式，扩展性更强
4.支持15中视图显示样式和效果
5.支持listView\scrollView\RecyclerView滑动时暂停图片加载，滑动停止时继续加载(原来的universal-image-loader1.9.5有bug，不支持)
5.使用：
  第一步、在Application或者SplashActivity当中初始化
  PinguoImageLoader.init(context.getApplicationContext(), FileUtil.getPathByType(FileUtil.DIR_TYPE_CACHE));
  L.writeDebugLogs(DebugUtil.isDebug());
  第二步、在需要显示图片的地方调用（详见下面的示例)
  第三部、(可选)在程序退出的时候，调用 PinguoImageLoader.destroy();

        //示例0 、背景淡出，图片淡入
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_DEFAULT)
                .into(mIs);
        /* 示例一、普通加载图片*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_DEFAULT)
                .into(mIv1);
        PinguoImageLoader.create("assets://xiada01.jpg")
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_DEFAULT)
                .into(mIv2);
        /* 示例二、渐变显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_FADE_IN)
                .setFadeInTime(1000)
                .into(mIv2);
        /* 示例三、圆角矩形显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setRoundRadius(30)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND)
                .into(mIv3);
        /* 示例四、圆角矩形淡入显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setRoundRadius(30)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND_FADE_IN)
                .setFadeInTime(1000)
                .into(mIv4);
        /* 示例五、圆角矩形LOMO显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setRoundRadius(30)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND_VIGNETTE)
                .into(mIv5);
        /* 示例六、圆角矩形LOMO淡入显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setRoundRadius(30)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND_VIGNETTE_FADE_IN)
                .setFadeInTime(1000)
                .into(mIv6);
        /* 示例七、圆形显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_CIRCLE)
                .into(mIv7);
        /* 示例八、圆形淡入显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setDisplayType(PinguoImageLoader.DISPLAY_CIRCLE_FADE_IN)
                .setFadeInTime(1000)
                .into(mIv8);

        /* 示例九、带环的圆形图片*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setStrokeWidth(5.f)
                .setRingColor(0xff00ff00)
                .setRingPadding(3.f)
                .setDisplayType(PinguoImageLoader.DISPLAY_CIRCLE_RING)
                .into(mIv9);
        /* 示例十、模糊图片显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setBlurDepth(20)
                .setDisplayType(PinguoImageLoader.DISPLAY_BLUR)
                .into(mIv10);
        /* 示例十一、模糊图片显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setBlurDepth(20)
                .setFadeInTime(1000)
                .setDisplayType(PinguoImageLoader.DISPLAY_BLUR_FADE_IN)
                .into(mIv11);
        /* 示例十二、模糊图片显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setBlurDepth(20)
                .setRoundRadius(20)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND_BLUR)
                .into(mIv12);
        /* 示例十三、模糊图片显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setBlurDepth(20)
                .setRoundRadius(20)
                .setDisplayType(PinguoImageLoader.DISPLAY_ROUND_BLUR_VIGNETTE)
                .into(mIv13);
        /* 示例十四、模糊图片显示*/
        PinguoImageLoader.create(url)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDefaultRes(R.drawable.ic_launcher)
                .setFailRes(R.drawable.ic_launcher)
                .setEmptyRes(R.drawable.ic_launcher)
                .setBlurDepth(20)
                .setDisplayType(PinguoImageLoader.DISPLAY_CIRCLE_BLUR)
                .into(mIv14);