package com.lucene.test;  
  
import java.io.File;  
import java.io.IOException;  
import java.text.SimpleDateFormat;  
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.index.CorruptIndexException;  
import org.apache.lucene.index.Term;  
import org.apache.lucene.queryParser.MultiFieldQueryParser;  
import org.apache.lucene.queryParser.ParseException;  
import org.apache.lucene.queryParser.QueryParser;  
import org.apache.lucene.search.BooleanClause;  
import org.apache.lucene.search.BooleanQuery;  
import org.apache.lucene.search.Filter;  
import org.apache.lucene.search.FilteredQuery;  
import org.apache.lucene.search.IndexSearcher;  
import org.apache.lucene.search.PrefixQuery;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.QueryWrapperFilter;  
import org.apache.lucene.search.ScoreDoc;  
import org.apache.lucene.search.Sort;  
import org.apache.lucene.search.SortField;  
import org.apache.lucene.search.TermQuery;  
import org.apache.lucene.search.TopDocs;  
import org.apache.lucene.search.WildcardQuery;  
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.SimpleFSDirectory;  
import org.apache.lucene.util.Version;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  
import com.lucene.entity.Article;  
  
/** 
 * Lucene 检索各种索引的实现方式总结 
 * @author Administrator 
 * 
 */  
public class LuceneSearchDemo {  
      
    public static final String INDEX_DIR_PATH = "indexDir";  
    /* 创建简单中文分析器 创建索引使用的分词器必须和查询时候使用的分词器一样，否则查询不到想要的结果 */  
    private Analyzer analyzer = null;  
    // 索引保存目录  
    private File indexFile = null;  
    //目录对象，因为操作索引文件都要用到它，所以定义为全局变量  
    private Directory directory = null;  
    //索引搜索对象  
    private IndexSearcher indexSearcher;  
      
    /** 
     * 初始化方法 
     * @throws IOException  
     */  
    public void init() throws IOException{  
        analyzer = new IKAnalyzer(true);  
        indexFile = new File(INDEX_DIR_PATH);  
        directory = new SimpleFSDirectory(indexFile);  
        indexSearcher = new IndexSearcher(directory);  
          
        System.out.println("*****************初始化成功**********************");  
    }  
      
    /** 
     * 根据传递的结果集 封装成集合后显示出来 
     * @param scoreDocs 
     * @throws IOException  
     * @throws CorruptIndexException  
     */  
    public void showResult(ScoreDoc[] scoreDocs) throws CorruptIndexException, IOException{  
        List<Article> articles = new ArrayList<Article>();  
        for (int i = 0; i < scoreDocs.length; i++) {  
            int doc = scoreDocs[i].doc;//索引id  
            Document document = indexSearcher.doc(doc);  
              
            Article article = new Article();  
            if (document.get("id") == null) {  
                System.out.println("id为空");  
            } else {  
                article.setId(Integer.parseInt(document.get("id")));  
                article.setTitle(document.get("title"));  
                article.setContent(document.get("content"));  
                articles.add(article);  
            }             
        }  
        if(articles.size()!=0){  
            for (Article article : articles) {  
                System.out.println(article);  
            }  
        }else{  
            System.out.println("没有查到记录。");  
        }  
    }  
  
    /** 
     * 通过QueryParser绑定单个字段来检索索引记录 
     * @param keyword 
     * @throws ParseException  
     * @throws IOException  
     * @throws CorruptIndexException  
     */  
    public void searchByQueryParser(String keyword) throws ParseException, CorruptIndexException, IOException{  
        System.out.println("*****************通过QueryParser来检索索引记录**********************");  
        QueryParser queryParser = new QueryParser(Version.LUCENE_36, "title", analyzer);  
        Query query = queryParser.parse(keyword);  
        // public TopFieldDocs search(Query query, int n, Sort sort)  
        // 参数分别表示 Query查询对象，返回的查询数目，排序对象  
        TopDocs topDocs = indexSearcher.search(query, 10, new Sort());  
        showResult(topDocs.scoreDocs);  
    }  
  
    public static void main(String[] args) {  
        LuceneSearchDemo luceneInstance = new LuceneSearchDemo();  
        try {  
            luceneInstance.init();  
            luceneInstance.searchByQueryParser("沪K");  
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  