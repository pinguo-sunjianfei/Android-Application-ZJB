package com.idrv.coach.data.model;

import com.idrv.coach.bean.Comment;
import com.idrv.coach.bean.parser.CommentParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/8/8
 * description:
 *
 * @author sunjianfei
 */
public class CommentModel {
    String sp;


    /**
     * 刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<List<Comment>> refresh(Action0 clearAdapter) {
        sp = "";
        return loadComment().doOnNext(__ -> clearAdapter.call());
    }

    /**
     * 加载更多
     *
     * @return
     */
    public Observable<List<Comment>> loadMore() {
        return loadComment();
    }


    /**
     * 获取评论数据
     *
     * @return
     */
    private Observable<List<Comment>> loadComment() {
        //1.创建Request
        HttpGsonRequest<List<Comment>> mRefreshRequest = RequestBuilder.<List<Comment>>create()
                .parser(new CommentParser())
                .requestMethod(Request.Method.POST)
                .put("count", 20)
                .put("sp", sp)
                .url(ApiConstant.API_COMMENT_AND_CONSULATION)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .doOnNext(list -> sp = list.get(list.size() - 1).getTime())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除评论或咨询
     *
     * @return
     */
    public Observable<String> deleteComment(int messageType, String messageId) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DELETE_COMMENT)
                .put("type", messageType)
                .put("id", messageId)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
