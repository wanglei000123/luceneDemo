package com.lucene.test;  
  
import java.io.File;  
import java.io.IOException;  
import java.io.StringReader;  
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.SimpleAnalyzer;  
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field;  
import org.apache.lucene.index.CorruptIndexException;  
import org.apache.lucene.index.IndexReader;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
import org.apache.lucene.queryParser.MultiFieldQueryParser;  
import org.apache.lucene.queryParser.ParseException;  
import org.apache.lucene.queryParser.QueryParser;  
import org.apache.lucene.search.IndexSearcher;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.ScoreDoc;  
import org.apache.lucene.search.Sort;  
import org.apache.lucene.search.SortField;  
import org.apache.lucene.search.TopDocs;  
import org.apache.lucene.search.highlight.Highlighter;  
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;  
import org.apache.lucene.search.highlight.QueryScorer;  
import org.apache.lucene.search.highlight.SimpleFragmenter;  
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;  
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.FSDirectory;  
import org.apache.lucene.store.SimpleFSDirectory;  
import org.apache.lucene.util.Version;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  
import com.lucene.entity.Article;  
  
public class SimpleFSDirectoryDemo {  
    /* 创建简单中文分析器 创建索引使用的分词器必须和查询时候使用的分词器一样，否则查询不到想要的结果 */  
    private Analyzer analyzer = new IKAnalyzer(true);  
    // 索引保存目录  
    private File indexFile = new File("./indexDir/");  
  
    /** 
     * 创建索引文件到磁盘中永久保存 
     */  
    public void createIndexFile() {  
        long startTime = System.currentTimeMillis();  
        System.out.println("*****************创建索引开始**********************");  
        Directory directory = null;  
        IndexWriter indexWriter = null;  
        try {  
            // 创建哪个版本的IndexWriterConfig，根据参数可知lucene是向下兼容的，选择对应的版本就好  
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);  
            // 创建磁盘目录对象  
            directory = new SimpleFSDirectory(indexFile);  
            indexWriter = new IndexWriter(directory, indexWriterConfig);  
            // indexWriter = new IndexWriter(directory, analyzer, true,IndexWriter.MaxFieldLength.UNLIMITED);  
            // 这上面是使用内存保存索引的创建索引写入对象的例子，和这里的实现方式不一样，但是效果是一样的  
  
            Article article0 = new Article(1, "Simple Analyzer","这个分词是一段一段话进行分 ");  
            Article article1 = new Article(2, "Standard Analyzer","标准分词拿来分中文和ChineseAnalyzer一样的效果");  
            Article article2 = new Article(3, "PerField AnalyzerWrapper","这个很有意思，可以封装很多分词方式，还可以于先设置field用那个分词分！牛 ");  
            Article article3 = new Article(4, "CJK Analyzer","这个分词方式是正向退一分词(二分法分词)，同一个字会和它的左边和右边组合成一个次，每个人出现两次，除了首字和末字 ");  
            Article article4 = new Article(5, "Chinese Analyzer","这个是专业的中文分词器，一个一个字分 ");  
            Article article5 = new Article(6, " BrazilianAnalyzer", "巴西语言分词 ");  
            Article article6 = new Article(7, " CzechAnalyzer", "捷克语言分词 ");  
            Article article7 = new Article(8, "DutchAnalyzer", "荷兰语言分词 ");  
            Article article8 = new Article(9, "FrenchAnalyzer", "法国语言分词 ");  
            Article article9 = new Article(10, "沪K123", "这是一个车牌号，包含中文，字母，数字");  
            Article article10 = new Article(11, "沪K345", "上海~！@~！@");  
           Article article11 = new Article(12, "沪B678", "京津沪");  
           Article article12 = new Article(13, "沪A3424", "沪K345 沪K3 沪K123 沪K111111111 沪ABC");  
           Article article13 = new Article(14, "沪 B2222", "");  
           Article article14 = new Article(15, "沪K3454653", "沪K345");  
           Article article15 = new Article(16, "123 123 1 2 23 3", "沪K123");  
           List<Article> articleList = new ArrayList<Article>();  
           articleList.add(article0);  
           articleList.add(article1);  
           articleList.add(article2);  
           articleList.add(article3);  
           articleList.add(article4);  
           articleList.add(article5);  
           articleList.add(article6);  
           articleList.add(article7);  
           articleList.add(article8);  
           articleList.add(article9);  
           articleList.add(article10);  
           articleList.add(article11);  
           articleList.add(article12);  
           articleList.add(article13);  
           articleList.add(article14);  
           articleList.add(article15);  
           // 为了避免重复插入数据，每次测试前 先删除之前的索引  
           indexWriter.deleteAll();  
           // 获取实体对象  
            for (int i = 0; i < articleList.size(); i++) {  
                Article article = articleList.get(i);  
                // indexWriter添加索引  
                Document doc = new Document();  
                doc.add(new Field("id", article.getId().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED));  
                doc.add(new Field("title", article.getTitle().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("content", article.getContent().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                // 添加到索引中去  
                indexWriter.addDocument(doc);  
                System.out.println("索引添加成功：第" + (i + 1) + "次！！");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexWriter != null) {  
                try {  
                    indexWriter.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (directory != null) {  
                try {  
                    directory.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        long endTime = System.currentTimeMillis();  
        System.out.println("创建索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        System.out.println("*****************创建索引结束**********************");  
    }  
  
    /** 
     * 直接读取索引文件，查询索引记录 
     *  
     * @throws IOException 
     */  
    public void openIndexFile() {  
        long startTime = System.currentTimeMillis();  
        System.out.println("*****************读取索引开始**********************");  
        List<Article> articles = new ArrayList<Article>();  
        // 得到索引的目录  
        Directory directory = null;  
        IndexReader indexReader = null;  
        try {  
            directory = new SimpleFSDirectory(indexFile);  
            // 根据目录打开一个indexReader  
            indexReader = IndexReader.open(directory);  
            //indexReader = IndexReader.open(directory,false);  
            System.out.println("在索引文件中总共插入了" + indexReader.maxDoc() + "条记录。");  
            // 获取第一个插入的document对象  
            Document minDoc = indexReader.document(0);  
            // 获取最后一个插入的document对象  
            Document maxDoc = indexReader.document(indexReader.maxDoc() - 1);  
            // document对象的get(字段名称)方法获取字段的值  
            System.out.println("第一个插入的document对象的标题是：" + minDoc.get("title"));  
            System.out.println("最后一个插入的document对象的标题是：" + maxDoc.get("title"));  
            //indexReader.deleteDocument(0);  
            int docLength = indexReader.maxDoc();  
            for (int i = 0; i < docLength; i++) {  
                Document doc = indexReader.document(i);  
                Article article = new Article();  
                if (doc.get("id") == null) {  
                    System.out.println("id为空");  
                } else {  
                    article.setId(Integer.parseInt(doc.get("id")));  
                    article.setTitle(doc.get("title"));  
                    article.setContent(doc.get("content"));  
                    articles.add(article);  
                }  
            }  
            System.out.println("显示所有插入的索引记录：");  
            for (Article article : articles) {  
                System.out.println(article);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexReader != null) {  
                try {  
                    indexReader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (directory != null) {  
                try {  
                    directory.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        long endTime = System.currentTimeMillis();  
        System.out.println("直接读取索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        System.out.println("*****************读取索引结束**********************");  
    }  
  
    /** 
     * 查看IKAnalyzer 分词器是如何将一个完整的词组进行分词的 
     *  
     * @param text 
     * @param isMaxWordLength 
     */  
    public void splitWord(String text, boolean isMaxWordLength) {  
        try {  
            // 创建分词对象  
            Analyzer analyzer = new IKAnalyzer(isMaxWordLength);  
            StringReader reader = new StringReader(text);  
            // 分词  
            TokenStream ts = analyzer.tokenStream("", reader);  
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
            // 遍历分词数据  
            System.out.print("IKAnalyzer把关键字拆分的结果是：");  
            while (ts.incrementToken()) {  
                System.out.print("【" + term.toString() + "】");  
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        System.out.println();  
    }  
  
    /** 
     * 根据关键字实现全文检索 
     */  
    public void searchIndexFile(String keyword) {  
        long startTime = System.currentTimeMillis();  
        System.out.println("*****************查询索引开始**********************");  
        IndexReader indexReader = null;  
        IndexSearcher indexSearcher = null;  
        List<Article> articleList = new ArrayList<Article>();  
        try {  
            indexReader = IndexReader.open(FSDirectory.open(indexFile));  
            // 创建一个排序对象，其中SortField构造方法中，第一个是排序的字段，第二个是指定字段的类型，第三个是是否升序排列，true：升序，false：降序。  
            Sort sort = new Sort(new SortField[] {new SortField("title", SortField.STRING, false),new SortField("content", SortField.STRING, false) });  
            //Sort sort = new Sort();  
            // 创建搜索类  
            indexSearcher = new IndexSearcher(indexReader);  
            // 下面是创建QueryParser 查询解析器  
            // QueryParser支持单个字段的查询，但是MultiFieldQueryParser可以支持多个字段查询，建议用后者这样可以实现全文检索的功能。  
            // QueryParser queryParser = new QueryParser(Version.LUCENE_36, "title", analyzer);  
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, new String[] { "title", "content" },analyzer);  
            // 利用queryParser解析传递过来的检索关键字，完成Query对象的封装  
            Query query = queryParser.parse(keyword);  
            splitWord(keyword, true); // 显示拆分结果  
            // 执行检索操作  
            TopDocs topDocs = indexSearcher.search(query, 5, sort);  
            System.out.println("一共查到:" + topDocs.totalHits + "记录");  
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
            // 像百度，谷歌检索出来的关键字如果有，除了显示在列表中之外还会高亮显示。Lucenen也支持高亮功能，正常应该是<font color='red'></font>这里用【】替代，使效果更加明显  
            SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("【", "】");  
            // 具体怎么实现的不用管，直接拿来用就好了。  
            Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query));  
  
            for (int i = 0; i < scoreDoc.length; i++) {  
                // 内部编号 ,和数据库表中的唯一标识列一样  
                int doc = scoreDoc[i].doc;  
                // 根据文档id找到文档  
                Document mydoc = indexSearcher.doc(doc);  
  
                String id = mydoc.get("id");  
                String title = mydoc.get("title");  
                String content = mydoc.get("content");  
                TokenStream tokenStream = null;  
                if (title != null && !title.equals("")) {  
                    tokenStream = analyzer.tokenStream("title",new StringReader(title));  
                    title = highlighter.getBestFragment(tokenStream, title);  
                }  
                if (content != null && !content.equals("")) {  
                    tokenStream = analyzer.tokenStream("content",new StringReader(content));  
                    // 传递的长度表示检索之后匹配长度，这个会导致返回的内容不全  
                    //highlighter.setTextFragmenter(new SimpleFragmenter(content.length()));   
                    content = highlighter.getBestFragment(tokenStream, content);  
                }  
                // 需要注意的是 如果使用了高亮显示的操作，查询的字段中没有需要高亮显示的内容 highlighter会返回一个null回来。  
                articleList.add(new Article(Integer.valueOf(id),title == null ? mydoc.get("title") : title,content == null ? mydoc.get("content") : content));  
            }  
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InvalidTokenOffsetsException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexSearcher != null) {  
                try {  
                    indexSearcher.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (indexReader != null) {  
                try {  
                    indexReader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        System.out.println("根据关键字" + keyword + "检索到的结果如下：");  
        for (Article article : articleList) {  
            System.out.println(article);  
        }  
        long endTime = System.currentTimeMillis();  
        System.out.println("全文索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        System.out.println("*****************查询索引结束**********************");  
    }  
  
    public static void main(String[] args) {  
        SimpleFSDirectoryDemo luceneInstance = new SimpleFSDirectoryDemo();  
        // 建立要索引的文件  
         luceneInstance.createIndexFile();  
        // 从索引文件中查询数据  
         luceneInstance.openIndexFile();  
        // 查看IKAnalyzer分词结果  
	        String[] keywords = new 
			String[]{"IKAnalyzer是一个基于java语言开发的轻量级的中文分词工具包" 
			,"我正在学习Lucene3.6，看一下效果如何" 
			,"鄂尔多斯"," Java做服务器端时如何接收和处理android客户端base64编码过的图片呢？"}; 
			luceneInstance.splitWord(keywords[0], true); 
			luceneInstance.splitWord(keywords[0], false); 
			luceneInstance.splitWord(keywords[1], true); 
			luceneInstance.splitWord(keywords[1], false); 
			luceneInstance.splitWord(keywords[2], true); 
			luceneInstance.splitWord(keywords[2], false); 
			luceneInstance.splitWord(keywords[3], true); 
			luceneInstance.splitWord(keywords[3], false); 
        // 获得结果，然后交由相关应用程序处理  
        String[] searchKeywords = new String[]{"analyzer","沪B123","沪K123","沪K123 上海","沪K3454653"};  
        luceneInstance.searchIndexFile(searchKeywords[1]); 
    }  
} 