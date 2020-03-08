package com.qy.reader.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.qy.reader.common.utils.Nav
import com.qy.reader.widgets.CustomSearchBar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.diql.android.novel.R
import org.diql.android.novel.SearchHistoryObservable

/**
 * Created by xiaoshu on 2018/1/9.
 */
class SearchFragment : Fragment() {

    private lateinit var toolBar: Toolbar
    private lateinit var historyView : TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_search, container, false)
        toolBar = contentView.findViewById<View>(R.id.common_toolbar) as Toolbar
        toolBar.navigationIcon = null
        val mSearchBar: CustomSearchBar? = CustomSearchBar(context)
        toolBar.addView(mSearchBar)

        historyView = contentView.findViewById(R.id.tv_history_0) as TextView
       return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyView.setOnClickListener(View.OnClickListener {
            if (!isAdded)
                return@OnClickListener
            val bundle = Bundle()
            bundle.putString("text", historyView.text.toString())
            Nav.from(context).setExtras(bundle).start("novel://searchresult")
        })
    }

    override fun onResume() {
        super.onResume()
        Observable.create(SearchHistoryObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(Consumer {
                    historyView.text = it[0]
                })
                .subscribe();
    }
}
