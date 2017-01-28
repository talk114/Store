package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.functions.Func1;

@Deprecated
public class ProxyStore<Parsed> implements Store<Parsed> {

    private final InternalStore<Parsed, BarCode> internalStore;

    ProxyStore(InternalStore<Parsed, BarCode> internalStore) {
        this.internalStore = internalStore;
    }

    public ProxyStore(Fetcher<Parsed, BarCode> fetcher) {
        internalStore = new RealInternalStore<>(fetcher, new NoopPersister<Parsed, BarCode>(),
                new NoopParserFunc<Parsed, Parsed>());
    }

    public ProxyStore(Fetcher<Parsed, BarCode> fetcher, Persister<Parsed, BarCode> persister) {
        internalStore = new RealInternalStore<>(fetcher, persister,
                new NoopParserFunc<Parsed, Parsed>());
    }

    public <Raw> ProxyStore(Fetcher<Raw, BarCode> fetcher,
                            Persister<Raw, BarCode> persister,
                            Parser<Raw, Parsed> parser) {
        internalStore = new RealInternalStore<>(fetcher, persister, parser);
    }


    public <Raw> ProxyStore(Fetcher<Raw, BarCode> fetcher,
                            Persister<Raw, BarCode> persister,
                            Func1<Raw, Parsed> parser, Cache<BarCode, Observable<Parsed>> memCache) {
        internalStore = new RealInternalStore<>(fetcher, persister, parser, memCache);
    }


    public <Raw> ProxyStore(Fetcher<Raw, BarCode> fetcher,
                            Persister<Raw, BarCode> persister,
                            Cache<BarCode, Observable<Parsed>> memCache) {
        internalStore = new RealInternalStore<>(fetcher, persister, new NoopParserFunc<Raw, Parsed>(), memCache);
    }


    @NotNull
    @Override
    public Observable<Parsed> get(@NotNull final BarCode barCode) {
        return internalStore.get(barCode);
    }

    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to nerwork
     *
     * @return data from fetch and store it in memory and persister
     */
    @NotNull
    @Override
    public Observable<Parsed> fetch(@NotNull final BarCode barCode) {
        return internalStore.fetch(barCode);
    }

    @NotNull
    @Override
    public Observable<Parsed> stream() {
        return internalStore.stream();
    }

    @NotNull
    @Override
    public Observable<Parsed> stream(BarCode id) {
        return internalStore.stream(id);
    }

    @Override
    public void clearMemory() {
        internalStore.clearMemory();
    }

    /**
     * Clear memory by id
     *
     * @param barCode of data to clear
     */
    @Override
    public void clearMemory(@NotNull final BarCode barCode) {
        internalStore.clearMemory(barCode);
    }

    protected Observable<Parsed> memory(@NotNull BarCode id) {
        return internalStore.memory(id);
    }

    @NotNull
    protected Observable<Parsed> disk(@NotNull BarCode id) {
        return internalStore.disk(id);
    }

}
