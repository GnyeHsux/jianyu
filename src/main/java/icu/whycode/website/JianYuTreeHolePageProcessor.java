package icu.whycode.website;

import icu.whycode.entity.TreeHole;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫框架文档
 * https://github.com/code4craft/webmagic/blob/master/README-zh.md
 */
public class JianYuTreeHolePageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("https://jandan.net/treehole");
    List<TreeHole> treeHoleList = new ArrayList<>();
    String nextPageUrl = "";

    @Override
    public void process(Page page) {
        List<Selectable> commentList = page.getHtml().xpath("//ol[@class='commentlist']/li").nodes();
        if (commentList != null && commentList.size() > 0) {
            for (Selectable selectable : commentList) {
                TreeHole treeHole = new TreeHole();
                treeHole.setAuthor(selectable.xpath("//div[@class='author']/strong/text()").toString());
                treeHole.setText(selectable.xpath("//div[@class='text']/p/text()").toString());
                treeHoleList.add(treeHole);
            }
            nextPageUrl = page.getHtml().xpath("//div[@class='cp-pagenavi']/a[@class='previous-comment-page']/@href").toString();

            page.putField("nextPageUrl", nextPageUrl);
            page.putField("treeHoleList", treeHoleList);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }
}
