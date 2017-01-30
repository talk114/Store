package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class SequentialTest {

    private int networkCalls = 0;
    private Store<Integer> store;

    @Before
    public void setup() {
        networkCalls = 0;
        store = StoreBuilder.<Integer>builder()
                .fetcher(new Fetcher<Integer>() {
                    @NotNull
                    @Override
                    public Observable<Integer> fetch(BarCode barCode) {
                        return Observable.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception {
                                return networkCalls++;
                            }
                        });
                    }
                })
                .open();
    }

    @Test
    public void sequentially() {
        BarCode b = new BarCode("one", "two");
        store.get(b).test().awaitTerminalEvent();
        store.get(b).test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void parallel() {
        BarCode b = new BarCode("one", "two");
        Observable<Integer> first = store.get(b);
        Observable<Integer> second = store.get(b);

        first.test().awaitTerminalEvent();
        second.test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }
}
