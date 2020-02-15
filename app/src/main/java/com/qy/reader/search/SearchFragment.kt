package com.qy.reader.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.qy.reader.widgets.CustomSearchBar
import org.diql.android.novel.R

/**
 * Created by xiaoshu on 2018/1/9.
 */
class SearchFragment : Fragment() {

    private lateinit var mToolBar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_search, container, false)
        mToolBar = contentView.findViewById<View>(R.id.common_toolbar) as Toolbar
        mToolBar.navigationIcon = null
        val mSearchBar: CustomSearchBar? = CustomSearchBar(context)
        mToolBar.addView(mSearchBar)
        return contentView
    }
}
