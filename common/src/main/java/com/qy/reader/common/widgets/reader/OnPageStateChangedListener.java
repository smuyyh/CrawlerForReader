package com.qy.reader.common.widgets.reader;

public interface OnPageStateChangedListener {

    void onCenterClick();

    void onChapterChanged(int currentChapter, int fromChapter, boolean fromUser);

    void onPageChanged(int currentPage, int currentChapter);

    void onChapterLoadFailure(int currentChapter);
}
