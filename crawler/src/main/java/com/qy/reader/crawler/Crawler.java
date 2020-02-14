package com.qy.reader.crawler;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.entity.source.SourceEnable;
import com.qy.reader.common.entity.source.SourceID;
import com.qy.reader.common.utils.SPUtils;
import com.qy.reader.common.utils.StringUtils;
import com.qy.reader.crawler.source.callback.ChapterCallback;
import com.qy.reader.crawler.source.callback.ContentCallback;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.crawler.source.callback.SearchCallback;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.entity.source.Source;
import com.qy.reader.common.entity.source.SourceConfig;
import com.qy.reader.crawler.source.SourceManager;
import com.qy.reader.crawler.xpath.exception.XpathSyntaxErrorException;
import com.qy.reader.crawler.xpath.model.JXDocument;
import com.qy.reader.crawler.xpath.model.JXNode;

import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫
 * <p>
 * Created by yuyuhang on 2018/1/8.
 */
public class Crawler {

    public static final String TAG = "qy.Crawler";

    public static void search(@NonNull String keyword, SearchCallback callback) {
        SparseBooleanArray checkedMap = SourceManager.getSourceEnableSparseArray();

        for (int i = 0; i < SourceManager.CONFIGS.size(); i++) {
            int id = SourceManager.CONFIGS.keyAt(i);
            SourceConfig config = SourceManager.CONFIGS.valueAt(i);
            Source source = SourceManager.SOURCES.get(id);
            if (!checkedMap.get(id)) {
                continue;
            }

            List<JXNode> rs;
            String url;
            try {
                if (!TextUtils.isEmpty(config.search.charset)) {
                    url = String.format(source.searchURL, URLEncoder.encode(keyword, config.search.charset));
                } else {
                    url = String.format(source.searchURL, keyword);
                }
                Log.i(TAG, "url=" + url);
                JXDocument jxDocument = new JXDocument(Jsoup.connect(url).validateTLSCertificates(false).get());
                rs = jxDocument.selN(config.search.xpath);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                continue;
            }

            if (rs == null) {
                continue;
            }

            List<SearchBook> books = new ArrayList<>();
            try { // 提高容错性
                for (JXNode jxNode : rs) {
                    SearchBook book = new SearchBook();

                    book.cover = urlVerification(getNodeStr(jxNode, config.search.coverXpath), url);
                    Log.i(TAG, "cover=" + book.cover);

                    book.title = getNodeStr(jxNode, config.search.titleXpath);
                    Log.i(TAG, "title=" + book.title);

                    String link = urlVerification(getNodeStr(jxNode, config.search.linkXpath), url);
                    if (source.id == SourceID.CHINESEWUZHOU ||
                            source.id == SourceID.YANMOXUAN ||
                            source.id == SourceID.QIANQIANXIAOSHUO ||
                            source.id == SourceID.PIAOTIANWENXUE) {
                        link = link.substring(0, link.lastIndexOf('/') + 1);
                    }
                    Log.i(TAG, "link=" + link);
                    book.sources.add(new SearchBook.SL(link, source));

                    book.author = getNodeStr(jxNode, config.search.authorXpath);
                    if (source.id == SourceID.CHINESEZHUOBI || source.id == SourceID.CHINESEXIAOSHUO) {
                        book.author = book.author.replace("作者：", "");
                    }
                    Log.i(TAG, "author=" + book.author);

                    book.desc = getNodeStr(jxNode, config.search.descXpath).trim();
                    Log.i(TAG, "desc=" + book.desc);

                    if (!TextUtils.isEmpty(link)) {//过滤无效信息
                        books.add(book);
                    }
                }

                if (callback != null) {
                    callback.onResponse(keyword, books);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                if (callback != null) {
                    callback.onError(e.toString());
                    return;
                }
            }
        }

        if (callback != null) {
            callback.onFinish();
        }
    }

    public static void catalog(SearchBook.SL sl, ChapterCallback callback) {
        if (sl == null || sl.source == null || TextUtils.isEmpty(sl.link)) {
            callback.onError("");
            return;
        }

        int sourceId = sl.source.id;
        SourceConfig config = SourceManager.CONFIGS.get(sourceId);
        Source source = SourceManager.SOURCES.get(sourceId);

        if (config.catalog == null) {
            return;
        }

        if (sourceId == SourceID.CHINESEWUZHOU) { // 梧州中文台
            int ba = sl.link.indexOf("ba");
            int shtml = sl.link.lastIndexOf(".");
            if (ba > 0 && shtml > ba) {
                String id = sl.link.substring(ba + 2, shtml);
                String front = id.substring(0, 2);
                try {
                    URI original = new URI(sl.link);
                    URI uri = new URI(original.getScheme(), original.getAuthority(), "/" + front + "/" + id + "/", null, null);
                    sl.link = uri.toString();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        } else if (sourceId == SourceID.AIQIWENXUE) { // https://m.i7wx.com/book/3787.html --> https://m.i7wx.com/3/3787/
            String id = sl.link.substring(sl.link.lastIndexOf("/") + 1, sl.link.lastIndexOf("."));
            String front = id.substring(0, 1);
            try {
                URI original = new URI(sl.link);
                URI uri = new URI(original.getScheme(), original.getAuthority(), "/" + front + "/" + id + "/", null, null);
                sl.link = uri.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        List<JXNode> rs = null;
        try {
            JXDocument jxDocument = new JXDocument(Jsoup.connect(sl.link).validateTLSCertificates(false).get());
            rs = jxDocument.selN(config.catalog.xpath);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (rs == null || rs.isEmpty()) {
            callback.onError("请求失败");
            return;
        }

        List<Chapter> chapters = new ArrayList<>();
        try {
            for (JXNode jxNode : rs) {
                Chapter chapter = new Chapter();

                String link = getNodeStr(jxNode, config.catalog.linkXpath);
                if (!TextUtils.isEmpty(link)) {
                    chapter.link = urlVerification(link, sl.link);
                    Log.i(TAG, "link=" + chapter.link);

                    chapter.title = getNodeStr(jxNode, config.catalog.titleXpath);
                    Log.i(TAG, "title=" + chapter.title);
                }

                chapters.add(chapter);
            }

            if (callback != null) {
                callback.onResponse(chapters);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            callback.onError("请求失败");
        }
    }

    public static void content(SearchBook.SL sl, String url, ContentCallback callback) {
        Log.i(TAG, "content  url=" + url);
        if (sl == null || sl.source == null || TextUtils.isEmpty(sl.link) || TextUtils.isEmpty(url)) {
            if (callback != null) {
                callback.onError("");
            }
            return;
        }
        int sourceId = sl.source.id;
        SourceConfig config = SourceManager.CONFIGS.get(sourceId);
        Source source = SourceManager.SOURCES.get(sourceId);

        if (config.content == null) {
            if (callback != null) {
                callback.onError("");
            }
            return;
        }

        try {
            String link = urlVerification(url, sl.link);
            Log.i(TAG, "link =   " + link);
            JXDocument jxDocument = new JXDocument(Jsoup.connect(link).validateTLSCertificates(false).get());

            String content = getNodeStr(jxDocument, config.content.xpath);

            // 换行
            StringBuilder builder = new StringBuilder();
            String[] lines = content.split(" ");
            for (String line : lines) {
                line = StringUtils.trim(line);
                if (!TextUtils.isEmpty(line)) {
                    builder.append("        ").append(line).append("\n");
                }
            }

            content = builder.toString();
            Log.i(TAG, "content =" + content);
            if (callback != null) {
                callback.onResponse(content);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    /**
     * 获取 通过xpath 查找到的字符串
     *
     * @param startNode 只有JXDocument   和  JXNode 两种
     * @param xpath
     * @return
     */
    private static String getNodeStr(Object startNode, String xpath) {
        StringBuilder rs = new StringBuilder();
        try {
            List<?> list;
            if (startNode instanceof JXDocument) {
                list = ((JXDocument) startNode).sel(xpath);
            } else if (startNode instanceof JXNode) {
                list = ((JXNode) startNode).sel(xpath);
            } else {
                return "";
            }

            for (Object node : list) {
                rs.append(node.toString());
            }

        } catch (XpathSyntaxErrorException e) {
            Log.e(TAG, e.toString());
        }
        return rs.toString();
    }

    private static String urlVerification(String link, String linkWithHost) throws URISyntaxException {
        if (TextUtils.isEmpty(link)) {
            return link;
        }
        if (link.startsWith("/")) {
            URI original = new URI(linkWithHost);
            URI uri = new URI(original.getScheme(), original.getAuthority(), link, null);
            link = uri.toString();
        } else if (!link.startsWith("http://") && !link.startsWith("https://")) {
            if (linkWithHost.endsWith("html") || linkWithHost.endsWith("htm")) {
                linkWithHost = linkWithHost.substring(0, linkWithHost.lastIndexOf("/") + 1);
            } else if (!linkWithHost.endsWith("/")) {
                linkWithHost = linkWithHost + "/";
            }
            link = linkWithHost + link;
        }
        return link;
    }

    public static void main(String args[]) {

    }
}
