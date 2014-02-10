package com.example.scubclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetJwcInfo {
	private final static String TURL="http://jwc.scu.edu.cn/jwc/frontPage.action";
	private final static String INFOURL="http://jwc.scu.edu.cn/jwc/";
	
	//获取标题的html
	public String getTitlehtml(){
		String html=null;
		HttpGet get=new HttpGet(TURL);
		try{
			HttpResponse httpresponse=new DefaultHttpClient().execute(get);
			if(httpresponse==null){
				return null;
			}
			HttpEntity entity=httpresponse.getEntity();
			html=EntityUtils.toString(entity);
		}catch(Exception e){
			e.printStackTrace();
		}
		return html;
	}
	
	//获取教务处信息标题
	public List<Map<String,Object>> filterTitleHtml(String html){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		if(html==null){
			return null;
		}
		Document doc=Jsoup.parse(html);
		Elements trs=doc.select("table[width=440]").first().select("tr");
		for(Element tr:trs){
			Elements tds=tr.select("td");
			String link=tds.get(0).select("a").attr("href");
			String title=tds.get(0).select("a").select("span").text();
			String time=tds.get(1).text();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("link", link);
			map.put("title", title);
			map.put("time", time);
			list.add(map);
		}
		return list;
	}
	
	//获取具体信息的html
	public String getContextHtml(String newsid){
		String html=null;
		String url=INFOURL+newsid;
		HttpGet get=new HttpGet(url);
		try{
			HttpResponse httpResponse=new DefaultHttpClient().execute(get);
			HttpEntity entity=httpResponse.getEntity();
			html=EntityUtils.toString(entity);
		}catch(Exception e){
			e.printStackTrace();
		}
		return html;
	}
	
	//获取具体内容
	public Map<String,Object> filterContextHtml(String html){
		Map<String,Object> map=new HashMap<String,Object>();
		if(html==null){
			return null;
		}
		Document doc=Jsoup.parse(html);
		Element tb=doc.select("table[width=900]").first();
		Elements trs=tb.select("tr");
		String title=trs.get(1).select("td").text();
		String time=trs.get(3).select("td").text();
		String contextvalue=doc.select("input").attr("value");
		contextvalue="<span>"+contextvalue+"</span>";
		Document dc=Jsoup.parse(contextvalue);
		String context=dc.select("span").text();
		map.put("title", title);
		map.put("time", time);
		map.put("context", context);
		return map;
	}
}
