package com.qy.reader.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.qy.reader.common.utils.Nav
import com.qy.reader.common.utils.ToastUtils
import com.qy.reader.search.source.SourceSettingActivity
import org.diql.android.novel.R


/**
 * Created by xiaoshu on 2018/1/9.
 */
class CustomSearchBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    private var mEtSearch: EditText? = null
    private var mIvDelete: ImageView? = null
    private var mTvSearch: TextView? = null
    private var mIvSource: ImageView? = null

    init {
        initView(context)
    }

    private fun initView(context: Context?) {
        val contentView = LayoutInflater.from(context).inflate(R.layout.view_search_bar, this, true)
        mEtSearch = contentView.findViewById<View>(R.id.et_search_bar_view) as EditText
        mIvDelete = contentView.findViewById<View>(R.id.iv_search_bar_delete) as ImageView
        mTvSearch = contentView.findViewById<View>(R.id.tv_search_bar_search) as TextView
        mIvSource = contentView.findViewById<View>(R.id.iv_search_bar_source) as ImageView
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        mEtSearch!!.setOnKeyListener(OnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search()
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        mIvDelete!!.setOnClickListener({ mEtSearch!!.setText("") })

        mTvSearch!!.setOnClickListener({ search() })

        mIvSource!!.setOnClickListener({
            val intent = Intent(context, SourceSettingActivity::class.java)
            context?.startActivity(intent)
        })
    }

    private fun search() {
        val text = mEtSearch!!.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle()
        bundle.putString("text", text)
        Nav.from(context).setExtras(bundle).start("novel://searchresult")
    }
}
