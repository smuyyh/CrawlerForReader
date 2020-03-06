package org.diql.android.novel;

import android.content.Context;

import com.qy.reader.App;
import com.qy.reader.common.entity.book.SearchBook;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class BookcaseObservableOnSubscribe implements ObservableOnSubscribe<List<SearchBook>> {

    private Context appContext;

    public BookcaseObservableOnSubscribe(Context context) {
        this.appContext = context;
    }

    @Override
    public void subscribe(ObservableEmitter<List<SearchBook>> emitter) throws Exception {
        List<SearchBook> books = App.getInstance().getBookList();
        if (books == null) {
            books = new BookListHelper().loadBookList(appContext);
        }
        emitter.onNext(books);
        emitter.onComplete();
    }
}
