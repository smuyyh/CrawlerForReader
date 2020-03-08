package org.diql.android.novel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class SearchHistoryObservable implements ObservableOnSubscribe<List<String>> {

    @Override
    public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
        List<String> history = new SearchHistoryHelper().loadSearchHistory();
        emitter.onNext(history);
        emitter.onComplete();
    }
}
