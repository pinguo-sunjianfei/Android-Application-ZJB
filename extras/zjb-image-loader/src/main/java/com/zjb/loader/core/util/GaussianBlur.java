package com.zjb.loader.core.util;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GaussianBlur {
    private static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXECUTOR;
    private static final short[] stackblur_mul;
    private static final byte[] stackblur_shr;

    static {
        EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);
        stackblur_mul = new short[]{(short) 512, (short) 512, (short) 456, (short) 512, (short) 328, (short) 456, (short) 335, (short) 512, (short) 405, (short) 328, (short) 271, (short) 456, (short) 388, (short) 335, (short) 292, (short) 512, (short) 454, (short) 405, (short) 364, (short) 328, (short) 298, (short) 271, (short) 496, (short) 456, (short) 420, (short) 388, (short) 360, (short) 335, (short) 312, (short) 292, (short) 273, (short) 512, (short) 482, (short) 454, (short) 428, (short) 405, (short) 383, (short) 364, (short) 345, (short) 328, (short) 312, (short) 298, (short) 284, (short) 271, (short) 259, (short) 496, (short) 475, (short) 456, (short) 437, (short) 420, (short) 404, (short) 388, (short) 374, (short) 360, (short) 347, (short) 335, (short) 323, (short) 312, (short) 302, (short) 292, (short) 282, (short) 273, (short) 265, (short) 512, (short) 497, (short) 482, (short) 468, (short) 454, (short) 441, (short) 428, (short) 417, (short) 405, (short) 394, (short) 383, (short) 373, (short) 364, (short) 354, (short) 345, (short) 337, (short) 328, (short) 320, (short) 312, (short) 305, (short) 298, (short) 291, (short) 284, (short) 278, (short) 271, (short) 265, (short) 259, (short) 507, (short) 496, (short) 485, (short) 475, (short) 465, (short) 456, (short) 446, (short) 437, (short) 428, (short) 420, (short) 412, (short) 404, (short) 396, (short) 388, (short) 381, (short) 374, (short) 367, (short) 360, (short) 354, (short) 347, (short) 341, (short) 335, (short) 329, (short) 323, (short) 318, (short) 312, (short) 307, (short) 302, (short) 297, (short) 292, (short) 287, (short) 282, (short) 278, (short) 273, (short) 269, (short) 265, (short) 261, (short) 512, (short) 505, (short) 497, (short) 489, (short) 482, (short) 475, (short) 468, (short) 461, (short) 454, (short) 447, (short) 441, (short) 435, (short) 428, (short) 422, (short) 417, (short) 411, (short) 405, (short) 399, (short) 394, (short) 389, (short) 383, (short) 378, (short) 373, (short) 368, (short) 364, (short) 359, (short) 354, (short) 350, (short) 345, (short) 341, (short) 337, (short) 332, (short) 328, (short) 324, (short) 320, (short) 316, (short) 312, (short) 309, (short) 305, (short) 301, (short) 298, (short) 294, (short) 291, (short) 287, (short) 284, (short) 281, (short) 278, (short) 274, (short) 271, (short) 268, (short) 265, (short) 262, (short) 259, (short) 257, (short) 507, (short) 501, (short) 496, (short) 491, (short) 485, (short) 480, (short) 475, (short) 470, (short) 465, (short) 460, (short) 456, (short) 451, (short) 446, (short) 442, (short) 437, (short) 433, (short) 428, (short) 424, (short) 420, (short) 416, (short) 412, (short) 408, (short) 404, (short) 400, (short) 396, (short) 392, (short) 388, (short) 385, (short) 381, (short) 377, (short) 374, (short) 370, (short) 367, (short) 363, (short) 360, (short) 357, (short) 354, (short) 350, (short) 347, (short) 344, (short) 341, (short) 338, (short) 335, (short) 332, (short) 329, (short) 326, (short) 323, (short) 320, (short) 318, (short) 315, (short) 312, (short) 310, (short) 307, (short) 304, (short) 302, (short) 299, (short) 297, (short) 294, (short) 292, (short) 289, (short) 287, (short) 285, (short) 282, (short) 280, (short) 278, (short) 275, (short) 273, (short) 271, (short) 269, (short) 267, (short) 265, (short) 263, (short) 261, (short) 259};
        stackblur_shr = new byte[]{(byte) 9, (byte) 11, (byte) 12, (byte) 13, (byte) 13, (byte) 14, (byte) 14, (byte) 15, (byte) 15, (byte) 15, (byte) 15, (byte) 16, (byte) 16, (byte) 16, (byte) 16, (byte) 17, (byte) 17, (byte) 17, (byte) 17, (byte) 17, (byte) 17, (byte) 17, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 18, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 19, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 20, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 21, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 22, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 23, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24, (byte) 24};
    }

    public GaussianBlur() {
    }

    public Bitmap blur(Bitmap original, float radius) {
        try {
            int error = original.getWidth();
            int h = original.getHeight();
            int[] currentPixels = new int[error * h];
            original.getPixels(currentPixels, 0, error, 0, 0, error, h);
            int cores = EXECUTOR_THREADS;
            ArrayList horizontal = new ArrayList(cores);
            ArrayList vertical = new ArrayList(cores);

            for (int bitmap = 0; bitmap < cores; ++bitmap) {
                horizontal.add(new BlurTask(currentPixels, error, h, (int) radius, cores, bitmap, 1));
                vertical.add(new BlurTask(currentPixels, error, h, (int) radius, cores, bitmap, 2));
            }

            try {
                EXECUTOR.invokeAll(horizontal);
            } catch (InterruptedException var11) {
                return null;
            }

            try {
                EXECUTOR.invokeAll(vertical);
            } catch (InterruptedException var10) {
                return null;
            }

            Bitmap var14 = Bitmap.createBitmap(currentPixels, error, h, original.getConfig());
            return var14;
        } catch (Exception var12) {
            return null;
        } catch (OutOfMemoryError var13) {
            return null;
        }
    }

    private static void blurIteration(int[] src, int w, int h, int radius, int cores, int core, int step) {
        int wm = w - 1;
        int hm = h - 1;
        int div = radius * 2 + 1;
        short mul_sum = stackblur_mul[radius];
        byte shr_sum = stackblur_shr[radius];
        int[] stack = new int[div];
        int x;
        int y;
        int i;
        int sp;
        int stack_start;
        int stack_i;
        int src_i;
        int dst_i;
        long sum_r;
        long sum_g;
        long sum_b;
        long sum_in_r;
        long sum_in_g;
        long sum_in_b;
        long sum_out_r;
        long sum_out_g;
        long sum_out_b;
        int minX;
        int maxX;
        if (step == 1) {
            minX = core * h / cores;
            maxX = (core + 1) * h / cores;

            for (y = minX; y < maxX; ++y) {
                sum_out_b = 0L;
                sum_out_g = 0L;
                sum_out_r = 0L;
                sum_in_b = 0L;
                sum_in_g = 0L;
                sum_in_r = 0L;
                sum_b = 0L;
                sum_g = 0L;
                sum_r = 0L;
                src_i = w * y;

                for (i = 0; i <= radius; ++i) {
                    stack[i] = src[src_i];
                    sum_r += (long) ((src[src_i] >>> 16 & 255) * (i + 1));
                    sum_g += (long) ((src[src_i] >>> 8 & 255) * (i + 1));
                    sum_b += (long) ((src[src_i] & 255) * (i + 1));
                    sum_out_r += (long) (src[src_i] >>> 16 & 255);
                    sum_out_g += (long) (src[src_i] >>> 8 & 255);
                    sum_out_b += (long) (src[src_i] & 255);
                }

                for (i = 1; i <= radius; ++i) {
                    if (i <= wm) {
                        ++src_i;
                    }

                    stack_i = i + radius;
                    stack[stack_i] = src[src_i];
                    sum_r += (long) ((src[src_i] >>> 16 & 255) * (radius + 1 - i));
                    sum_g += (long) ((src[src_i] >>> 8 & 255) * (radius + 1 - i));
                    sum_b += (long) ((src[src_i] & 255) * (radius + 1 - i));
                    sum_in_r += (long) (src[src_i] >>> 16 & 255);
                    sum_in_g += (long) (src[src_i] >>> 8 & 255);
                    sum_in_b += (long) (src[src_i] & 255);
                }

                sp = radius;
                int xp = radius;
                if (radius > wm) {
                    xp = wm;
                }

                src_i = xp + y * w;
                dst_i = y * w;

                for (x = 0; x < w; ++x) {
                    src[dst_i] = (int) ((long) (src[dst_i] & -16777216) | (sum_r * (long) mul_sum >>> shr_sum & 255L) << 16 | (sum_g * (long) mul_sum >>> shr_sum & 255L) << 8 | sum_b * (long) mul_sum >>> shr_sum & 255L);
                    ++dst_i;
                    sum_r -= sum_out_r;
                    sum_g -= sum_out_g;
                    sum_b -= sum_out_b;
                    stack_start = sp + div - radius;
                    if (stack_start >= div) {
                        stack_start -= div;
                    }

                    sum_out_r -= (long) (stack[stack_start] >>> 16 & 255);
                    sum_out_g -= (long) (stack[stack_start] >>> 8 & 255);
                    sum_out_b -= (long) (stack[stack_start] & 255);
                    if (xp < wm) {
                        ++src_i;
                        ++xp;
                    }

                    stack[stack_start] = src[src_i];
                    sum_in_r += (long) (src[src_i] >>> 16 & 255);
                    sum_in_g += (long) (src[src_i] >>> 8 & 255);
                    sum_in_b += (long) (src[src_i] & 255);
                    sum_r += sum_in_r;
                    sum_g += sum_in_g;
                    sum_b += sum_in_b;
                    ++sp;
                    if (sp >= div) {
                        sp = 0;
                    }

                    sum_out_r += (long) (stack[sp] >>> 16 & 255);
                    sum_out_g += (long) (stack[sp] >>> 8 & 255);
                    sum_out_b += (long) (stack[sp] & 255);
                    sum_in_r -= (long) (stack[sp] >>> 16 & 255);
                    sum_in_g -= (long) (stack[sp] >>> 8 & 255);
                    sum_in_b -= (long) (stack[sp] & 255);
                }
            }
        } else if (step == 2) {
            minX = core * w / cores;
            maxX = (core + 1) * w / cores;

            for (x = minX; x < maxX; ++x) {
                sum_out_b = 0L;
                sum_out_g = 0L;
                sum_out_r = 0L;
                sum_in_b = 0L;
                sum_in_g = 0L;
                sum_in_r = 0L;
                sum_b = 0L;
                sum_g = 0L;
                sum_r = 0L;
                src_i = x;

                for (i = 0; i <= radius; ++i) {
                    stack[i] = src[src_i];
                    sum_r += (long) ((src[src_i] >>> 16 & 255) * (i + 1));
                    sum_g += (long) ((src[src_i] >>> 8 & 255) * (i + 1));
                    sum_b += (long) ((src[src_i] & 255) * (i + 1));
                    sum_out_r += (long) (src[src_i] >>> 16 & 255);
                    sum_out_g += (long) (src[src_i] >>> 8 & 255);
                    sum_out_b += (long) (src[src_i] & 255);
                }

                for (i = 1; i <= radius; ++i) {
                    if (i <= hm) {
                        src_i += w;
                    }

                    stack_i = i + radius;
                    stack[stack_i] = src[src_i];
                    sum_r += (long) ((src[src_i] >>> 16 & 255) * (radius + 1 - i));
                    sum_g += (long) ((src[src_i] >>> 8 & 255) * (radius + 1 - i));
                    sum_b += (long) ((src[src_i] & 255) * (radius + 1 - i));
                    sum_in_r += (long) (src[src_i] >>> 16 & 255);
                    sum_in_g += (long) (src[src_i] >>> 8 & 255);
                    sum_in_b += (long) (src[src_i] & 255);
                }

                sp = radius;
                int yp = radius;
                if (radius > hm) {
                    yp = hm;
                }

                src_i = x + yp * w;
                dst_i = x;

                for (y = 0; y < h; ++y) {
                    src[dst_i] = (int) ((long) (src[dst_i] & -16777216) | (sum_r * (long) mul_sum >>> shr_sum & 255L) << 16 | (sum_g * (long) mul_sum >>> shr_sum & 255L) << 8 | sum_b * (long) mul_sum >>> shr_sum & 255L);
                    dst_i += w;
                    sum_r -= sum_out_r;
                    sum_g -= sum_out_g;
                    sum_b -= sum_out_b;
                    stack_start = sp + div - radius;
                    if (stack_start >= div) {
                        stack_start -= div;
                    }

                    sum_out_r -= (long) (stack[stack_start] >>> 16 & 255);
                    sum_out_g -= (long) (stack[stack_start] >>> 8 & 255);
                    sum_out_b -= (long) (stack[stack_start] & 255);
                    if (yp < hm) {
                        src_i += w;
                        ++yp;
                    }

                    stack[stack_start] = src[src_i];
                    sum_in_r += (long) (src[src_i] >>> 16 & 255);
                    sum_in_g += (long) (src[src_i] >>> 8 & 255);
                    sum_in_b += (long) (src[src_i] & 255);
                    sum_r += sum_in_r;
                    sum_g += sum_in_g;
                    sum_b += sum_in_b;
                    ++sp;
                    if (sp >= div) {
                        sp = 0;
                    }

                    sum_out_r += (long) (stack[sp] >>> 16 & 255);
                    sum_out_g += (long) (stack[sp] >>> 8 & 255);
                    sum_out_b += (long) (stack[sp] & 255);
                    sum_in_r -= (long) (stack[sp] >>> 16 & 255);
                    sum_in_g -= (long) (stack[sp] >>> 8 & 255);
                    sum_in_b -= (long) (stack[sp] & 255);
                }
            }
        }

    }

    private static class BlurTask implements Callable<Void> {
        private final int[] _src;
        private final int _w;
        private final int _h;
        private final int _radius;
        private final int _totalCores;
        private final int _coreIndex;
        private final int _round;

        public BlurTask(int[] src, int w, int h, int radius, int totalCores, int coreIndex, int round) {
            this._src = src;
            this._w = w;
            this._h = h;
            this._radius = radius;
            this._totalCores = totalCores;
            this._coreIndex = coreIndex;
            this._round = round;
        }

        public Void call() throws Exception {
            GaussianBlur.blurIteration(this._src, this._w, this._h, this._radius, this._totalCores, this._coreIndex, this._round);
            return null;
        }
    }
}
