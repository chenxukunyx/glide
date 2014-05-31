package com.bumptech.glide.load.data.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.Resource;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.DecodeFormat;

import java.io.InputStream;

//TODO(actually fill out this stub)
public class StreamBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private final Downsampler downsampler;
    private BitmapPool bitmapPool;
    private DecodeFormat decodeFormat;

    public StreamBitmapDecoder(BitmapPool bitmapPool) {
        this(Downsampler.AT_LEAST, bitmapPool, DecodeFormat.PREFER_RGB_565);
    }

    public StreamBitmapDecoder(Downsampler downsampler, BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this.downsampler = downsampler;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;
    }

    @Override
    public Resource<Bitmap> decode(InputStream source, int width, int height) {
        Bitmap bitmap = downsampler.decode(source, bitmapPool, width, height, decodeFormat);
        if (bitmap == null) {
            return null;
        } else {
            return new BitmapResource(bitmap, bitmapPool);
        }
    }

    @Override
    public String getId() {
        return new StringBuilder()
                .append("com.bumptech.glide.load.data.bitmap.StreamBitmapDecoder")
                .append(downsampler.getId())
                .append(decodeFormat.name())
                .toString();
    }
}
