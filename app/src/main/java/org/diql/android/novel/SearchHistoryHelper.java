package org.diql.android.novel;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.utils.FileIOUtils;
import com.qy.reader.common.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索历史
 */
public class SearchHistoryHelper {

    private final Type type;

    private final int limitCount = 5;

    public SearchHistoryHelper() {
        type = new TypeToken<List<String>>() {
        }.getType();
    }

    public List<String> loadSearchHistory() {
        String data = FileIOUtils.readFile2String(getSearchHistoryFile());
        if (TextUtils.isEmpty(data)) {
            return new ArrayList<String>(0);
        }
        List<String> history = new Gson().fromJson(data, type);
        return history;
    }

    public boolean saveSearchHistory(List<String> history) {
        if (history == null) {
            return false;
        }
        if (history.size() >= limitCount) {
            history = history.subList(0, limitCount - 1);
        }
        File file = getSearchHistoryFile();
        String content = new Gson().toJson(history);
        boolean result = FileIOUtils.writeFileFromString(file, content);
        return result;
    }

    private File getSearchHistoryFile() {
        String fileName = "search_history";
        return FileUtils.getAppStorageFile(fileName);
    }
}
