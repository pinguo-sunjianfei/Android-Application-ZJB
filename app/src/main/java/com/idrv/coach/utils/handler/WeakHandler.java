package com.idrv.coach.utils.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * time: 15/7/1
 * description: 对原生handler进行封装
 *
 * @author sunjianfei
 */
public class WeakHandler {
    private final Handler.Callback mCallback;
    private final ExecHandler mExec;
    private final ChainedRef mRunnables = new ChainedRef((Runnable) null);

    public WeakHandler() {
        this.mCallback = null;
        this.mExec = new ExecHandler();
    }

    public WeakHandler(@Nullable Handler.Callback callback) {
        this.mCallback = callback;
        this.mExec = new ExecHandler(new WeakReference(callback));
    }

    public WeakHandler(@NonNull Looper looper) {
        this.mCallback = null;
        this.mExec = new ExecHandler(looper);
    }

    public WeakHandler(@NonNull Looper looper, @NonNull Handler.Callback callback) {
        this.mCallback = callback;
        this.mExec = new ExecHandler(looper, new WeakReference(callback));
    }

    public final boolean post(@NonNull Runnable r) {
        return this.mExec.post(this.wrapRunnable(r));
    }

    public final boolean postAtTime(@NonNull Runnable r, long uptimeMillis) {
        return this.mExec.postAtTime(this.wrapRunnable(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return this.mExec.postAtTime(this.wrapRunnable(r), token, uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return this.mExec.postDelayed(this.wrapRunnable(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return this.mExec.postAtFrontOfQueue(this.wrapRunnable(r));
    }

    public final void removeCallbacks(Runnable r) {
        ChainedRef runnableRef = this.mRunnables.findForward(r);
        if (runnableRef != null) {
            this.mExec.removeCallbacks(runnableRef.wrapper);
        }

    }

    public final void removeCallbacks(Runnable r, Object token) {
        ChainedRef runnableRef = this.mRunnables.findForward(r);
        if (runnableRef != null) {
            this.mExec.removeCallbacks(runnableRef.wrapper, token);
        }

    }

    public final boolean sendMessage(Message msg) {
        return this.mExec.sendMessage(msg);
    }

    public final boolean sendEmptyMessage(int what) {
        return this.mExec.sendEmptyMessage(what);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return this.mExec.sendEmptyMessageDelayed(what, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        return this.mExec.sendEmptyMessageAtTime(what, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        return this.mExec.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return this.mExec.sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        return this.mExec.sendMessageAtFrontOfQueue(msg);
    }

    public final void removeMessages(int what) {
        this.mExec.removeMessages(what);
    }

    public final void removeMessages(int what, Object object) {
        this.mExec.removeMessages(what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        this.mExec.removeCallbacksAndMessages(token);
    }

    public final boolean hasMessages(int what) {
        return this.mExec.hasMessages(what);
    }

    public final boolean hasMessages(int what, Object object) {
        return this.mExec.hasMessages(what, object);
    }

    public final Looper getLooper() {
        return this.mExec.getLooper();
    }

    private WeakRunnable wrapRunnable(Runnable r) {
        ChainedRef hardRef = ChainedRef.obtain(r);
        this.mRunnables.insertAbove(hardRef);
        return hardRef.wrapper = new WeakRunnable(new WeakReference(r), new WeakReference(hardRef));
    }

    static class ChainedRef {
        @Nullable
        ChainedRef next;
        @Nullable
        ChainedRef prev;
        @Nullable
        Runnable runnable;
        @Nullable
        WeakRunnable wrapper;
        @Nullable
        static ChainedRef sPool;
        static int sPoolSize;
        static final int MAX_POOL_SIZE = 15;

        public ChainedRef(Runnable r) {
            this.runnable = r;
        }

        public void remove() {
            if (this.prev != null) {
                this.prev.next = this.next;
            }

            if (this.next != null) {
                this.next.prev = this.prev;
            }

            this.prev = null;
            this.runnable = null;
            this.wrapper = null;
            Class var1 = ChainedRef.class;
            synchronized (ChainedRef.class) {
                if (sPoolSize <= 15) {
                    this.next = sPool;
                    sPool = this;
                    ++sPoolSize;
                }
            }
        }

        public void insertAbove(@NonNull ChainedRef candidate) {
            if (this.next != null) {
                this.next.prev = candidate;
            }

            candidate.next = this.next;
            this.next = candidate;
            candidate.prev = this;
        }

        @Nullable
        public ChainedRef findForward(Runnable obj) {
            for (ChainedRef curr = this; curr != null; curr = curr.next) {
                if (curr.runnable != null) {
                    if (curr.runnable.equals(obj)) {
                        return curr;
                    }
                } else if (obj == null) {
                    return curr;
                }
            }

            return null;
        }

        public static ChainedRef obtain(Runnable r) {
            ChainedRef result = null;
            Class var2 = ChainedRef.class;
            synchronized (ChainedRef.class) {
                if (sPool != null) {
                    result = sPool;
                    sPool = sPool.next;
                    --sPoolSize;
                }
            }

            if (result != null) {
                result.runnable = r;
                return result;
            } else {
                return new ChainedRef(r);
            }
        }
    }

    static class WeakRunnable implements Runnable {
        private final WeakReference<Runnable> mDelegate;
        private final WeakReference<ChainedRef> mReference;

        WeakRunnable(WeakReference<Runnable> delegate, WeakReference<ChainedRef> reference) {
            this.mDelegate = delegate;
            this.mReference = reference;
        }

        public void run() {
            Runnable delegate = (Runnable) this.mDelegate.get();
            ChainedRef reference = (ChainedRef) this.mReference.get();
            if (reference != null) {
                reference.remove();
            }

            if (delegate != null) {
                delegate.run();
            }

        }
    }

    private static class ExecHandler extends Handler {
        private final WeakReference<Callback> mCallback;

        ExecHandler() {
            this.mCallback = null;
        }

        ExecHandler(WeakReference<Callback> callback) {
            this.mCallback = callback;
        }

        ExecHandler(Looper looper) {
            super(looper);
            this.mCallback = null;
        }

        ExecHandler(Looper looper, WeakReference<Callback> callback) {
            super(looper);
            this.mCallback = callback;
        }

        public void handleMessage(Message msg) {
            if (this.mCallback != null) {
                Callback callback = (Callback) this.mCallback.get();
                if (callback != null) {
                    callback.handleMessage(msg);
                }
            }
        }
    }
}

