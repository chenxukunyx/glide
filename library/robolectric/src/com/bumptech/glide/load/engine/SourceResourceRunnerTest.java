package com.bumptech.glide.load.engine;

import com.bumptech.glide.Priority;
import com.bumptech.glide.Resource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.ResourceFetcher;
import com.bumptech.glide.request.ResourceCallback;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SourceResourceRunnerTest {
    private SourceResourceHarness harness;

    @Before
    public void setUp() {
        harness = new SourceResourceHarness();
    }

    @Test
    public void testResourceFetcherIsCalled() throws Exception {
        harness.runner.run();

        verify(harness.fetcher).loadResource(eq(harness.priority));
    }

    @Test
    public void testDecoderIsCalledIfFetched() throws Exception {
        Object fetched = new Object();
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(fetched);

        harness.runner.run();

        verify(harness.decoder).decode(eq(fetched), eq(harness.width), eq(harness.height));
    }

    @Test
    public void testCallbackIsCalledIfFetchedAndDecoded() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(is);
        when(harness.decoder.decode(eq(is), eq(harness.width), eq(harness.height))).thenReturn(harness.result);

        harness.runner.run();

        verify(harness.cb).onResourceReady(eq(harness.result));
    }


    @Test
    public void testResourceIsWrittenToCacheIfFetchedAndDecoded() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(is);
        when(harness.decoder.decode(eq(is), eq(harness.width), eq(harness.height))).thenReturn(harness.result);

        final OutputStream expected = new ByteArrayOutputStream();

        harness.runner.run();
        harness.runner.write(expected);

        verify(harness.encoder).encode(eq(harness.result), eq(expected));
    }

    @Test
    public void testResourceIsTransformedBeforeBeingWrittenToCache() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(is);
        when(harness.decoder.decode(eq(is), eq(harness.width), eq(harness.height))).thenReturn(harness.result);
        Resource transformed = mock(Resource.class);
        when(harness.transformation.transform(eq(harness.result), eq(harness.width), eq(harness.height)))
                .thenReturn(transformed);

        OutputStream expected = new ByteArrayOutputStream();
        harness.runner.run();
        harness.runner.write(expected);

        verify(harness.encoder).encode(eq(transformed), eq(expected));
    }

    @Test
    public void testDecodedResourceIsRecycledIfTransformedResourceIsDifferent() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(is);
        when(harness.decoder.decode(eq(is), eq(harness.width), eq(harness.height))).thenReturn(harness.result);
        Resource transformed = mock(Resource.class);
        when(harness.transformation.transform(eq(harness.result), eq(harness.width), eq(harness.height)))
                .thenReturn(transformed);

        harness.runner.run();

        verify(harness.result).recycle();
    }

    @Test
    public void testDecodedResourceIsNotRecycledIfResourceIsNotTransformed() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(is);
        when(harness.decoder.decode(eq(is), eq(harness.width), eq(harness.height))).thenReturn(harness.result);

        harness.runner.run();

        verify(harness.result, never()).recycle();
    }

    @Test
    public void testCallbackIsCalledIfFetchFails() throws Exception {
        Exception expected = new Exception("Test");
        when(harness.fetcher.loadResource(eq(harness.priority))).thenThrow(expected);

        harness.runner.run();

        verify(harness.cb).onException(eq(expected));
    }

    @Test
    public void testCallbackIsCalledIfDecodeFails() throws Exception {
        when(harness.fetcher.loadResource(eq(harness.priority))).thenReturn(new Object());
        when(harness.decoder.decode(anyObject(), anyInt(), anyInt())).thenReturn(null);

        harness.runner.run();

        verify(harness.cb).onException((Exception) isNull());
    }

    @Test
    public void testResourceFetcherCancelIsCalledWhenCancelled() {
        harness.runner.cancel();

        verify(harness.fetcher).cancel();
    }

    @Test
    public void testFetcherNotCalledIfCancelled() throws Exception {
        harness.runner.cancel();
        harness.runner.run();

        verify(harness.fetcher, never()).loadResource(any(Priority.class));
    }

    @Test
    public void testPriorityMatchesPriority() {
        harness.priority = Priority.LOW;
        assertEquals(harness.priority.ordinal(), harness.runner.getPriority());

    }

    @SuppressWarnings("unchecked")
    private static class SourceResourceHarness {
        ResourceFetcher<Object> fetcher = mock(ResourceFetcher.class);
        ResourceDecoder<Object, Object> decoder = mock(ResourceDecoder.class);
        ResourceEncoder<Object> encoder = mock(ResourceEncoder.class);
        DiskCache diskCache = mock(DiskCache.class);
        Priority priority = Priority.LOW;
        ResourceCallback<Object> cb = mock(ResourceCallback.class);
        Resource<Object> result = mock(Resource.class);
        Transformation<Object> transformation = mock(Transformation.class);
        int width = 150;
        int height = 200;
        SourceResourceRunner<Object, Object> runner = new SourceResourceRunner<Object, Object>(mock(Key.class), width,
                height, fetcher, decoder, transformation, encoder, diskCache, priority, cb);

        public SourceResourceHarness() {
            when(transformation.transform(eq(result), eq(width), eq(height))).thenReturn(result);
        }
    }
}
