   * 由于原来的Volley框架是一个异步网络请求的框架,网络请求的线程由Volley管理，现在所有的异步操作的线程均需要由Rx管理，
所以需要对原来的Volley进行改造，而如果用retrofit一方面会有学习成本，两一方面，retrofit基于OkHttp,相对照片圈项目来讲
太重了，所以选择了再volley基础上进行改进,相比原来的Volley,做了如下改变
1.将异步的网络请求，修改为同步，即网络请求部分不会另开辟子线程执行
2.将文件\bitmap的上传与普通字段上传做成一样，只需要在HttpParams当中put(key,file/bitmap)即可
3.增加了https双向验证机制，增加了网络通信的安全性
4.增加了网络文件下载模块(支持断点续传，暂不支持多线程下载)
5.对网络请求的首层数据进行了解析封装HttpResponse,添加了错误标识ErrorCode
6.接入了测试框架stetho
6.使用：
    第一步、在Application/SplashActivity当中初始化
    Volley.init(CircleApplication.gContext, DebugUtil.isDebug(), new BaseParameterGenerator(), ApiConstant.VALIDATE_HOST);
    第二步、在ViewModel层使用
    public Observable<UserUpdate> updateUserInfo(UserUpdate userUpdate) {
        //1.构建一个request
        File file = null;
        if (!TextUtils.isEmpty(userUpdate.getAvatar())) {
            file = new File(userUpdate.getAvatar());
        }
        mUserUpdateRequest = RequestBuilder.<UserUpdate>create(UserUpdate.class)
                .url(ApiConstant.USER_INFO_UPDATE)
                .put("nickname", userUpdate.getNickname())
                .put("file", file)
                .put("description", userUpdate.getDescription())
                .put("sex", userUpdate.getSex())
                .put("addr", userUpdate.getAddr())
                .build();
        //2.请求网络
        return RequestPool.gRequestPool.request(mUserUpdateRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }