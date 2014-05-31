package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.ParcelFileDescriptor;
import android.view.animation.Animation;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.bitmap.Downsampler;
import com.bumptech.glide.load.data.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.provider.LoadProvider;
import com.bumptech.glide.request.bitmap.RequestListener;

import java.io.InputStream;

/**
 * A class for creating a request to load a bitmap for an image or from a video. Sets a variety of type independent
 * options including resizing, animations, and placeholders.
 *
 * @param <ModelType> The type of model that will be loaded into the target.
 */
@SuppressWarnings("unused") //public api
public class RequestBuilder<ModelType> extends GenericRequestBuilder<ModelType, InputStream, ParcelFileDescriptor> {
    private final BitmapPool bitmapPool;
    private Downsampler downsampler = Downsampler.AT_LEAST;
    private DecodeFormat decodeFormat = DecodeFormat.PREFER_RGB_565;

    RequestBuilder(Context context, ModelType model, LoadProvider<ModelType, InputStream, Bitmap> streamLoadProvider,
            LoadProvider<ModelType, ParcelFileDescriptor, Bitmap> fileDescriptorLoadProvider, BitmapPool bitmapPool) {
        super(context, model, streamLoadProvider, fileDescriptorLoadProvider);
        this.bitmapPool = bitmapPool;
    }

    /**
     * Load images at a size near the size of the target using {@link Downsampler#AT_LEAST}.
     *
     * @see #downsample(Downsampler)
     *
     * @return This RequestBuilder
     */
    public RequestBuilder<ModelType> approximate() {
        return downsample(Downsampler.AT_LEAST);
    }

    /**
     * Load images at their original size using {@link Downsampler#NONE}.
     *
     * @see #downsample(Downsampler)
     *
     * @return This RequestBuilder
     */
    public RequestBuilder<ModelType> asIs() {
        return downsample(Downsampler.NONE);
    }

    /**
     * Load images using the given {@link Downsampler}. Replaces any existing image decoder. Defaults to
     * {@link Downsampler#AT_LEAST}. Will be ignored if the data represented by the model is a video.
     *
     * @see #imageDecoder
     *
     * @param downsampler The downsampler
     * @return This RequestBuilder
     */
    private RequestBuilder<ModelType> downsample(Downsampler downsampler) {
        this.downsampler = downsampler;
        super.imageDecoder(new StreamBitmapDecoder(downsampler, bitmapPool, decodeFormat));
        return this;
    }

    @Override
    public RequestBuilder<ModelType> thumbnail(float sizeMultiplier) {
        super.thumbnail(sizeMultiplier);
        return this;
    }

    public RequestBuilder<ModelType> thumbnail(RequestBuilder<ModelType> thumbnailRequest) {
        super.thumbnail(thumbnailRequest);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> sizeMultiplier(float sizeMultiplier) {
        super.sizeMultiplier(sizeMultiplier);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> imageDecoder(ResourceDecoder<InputStream, Bitmap> decoder) {
        super.imageDecoder(decoder);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> videoDecoder(ResourceDecoder<ParcelFileDescriptor, Bitmap> decoder) {
        super.videoDecoder(decoder);
        return this;
    }

    /**
     * Sets the preferred format for {@link Bitmap}s decoded in this request. Defaults to
     * {@link DecodeFormat#PREFER_RGB_565}.
     *
     * <p>
     *     Note - If using a {@link Transformation} that expect bitmaps to support transparency, this should always be
     *     set to ALWAYS_ARGB_8888. RGB_565 requires fewer bytes per pixel and is generally preferable, but it does not
     *     support transparency.
     * </p>
     *
     * @see DecodeFormat
     *
     * @param format The format to use.
     * @return This request builder.
     */
    public RequestBuilder<ModelType> format(DecodeFormat format) {
        this.decodeFormat = format;
        super.imageDecoder(new StreamBitmapDecoder(downsampler, bitmapPool, format));
        return this;
    }

    @Override
    public RequestBuilder<ModelType> priority(Priority priority) {
        super.priority(priority);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> centerCrop() {
        super.centerCrop();
        return this;
    }

    @Override
    public RequestBuilder<ModelType> fitCenter() {
        super.fitCenter();
        return this;
    }

    @Override
    public RequestBuilder<ModelType> transform(Transformation<Bitmap> transformation) {
        super.transform(transformation);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> animate(int animationId) {
        super.animate(animationId);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> animate(Animation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> placeholder(int resourceId) {
        super.placeholder(resourceId);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> placeholder(Drawable drawable) {
        super.placeholder(drawable);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> error(int resourceId) {
        super.error(resourceId);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> error(Drawable drawable) {
        super.error(drawable);
        return this;
    }

    @Override
    public RequestBuilder<ModelType> listener(RequestListener<ModelType> requestListener) {
        super.listener(requestListener);
        return this;
    }
}
