package com.qy.reader.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qy.reader.R
import com.qy.reader.widgets.CustomSearchBar

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
