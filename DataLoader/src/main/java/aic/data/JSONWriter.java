package aic.data;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import aic.data.dto.Tweet;


public class JSONWriter implements ITweetWriter {
	
	private Set<String> noiseWords=new HashSet<String>();
	private PrintWriter out;
	private boolean first=true;

	public JSONWriter(OutputStream os) {
		out=new PrintWriter(os);
		
		//copied from http://drupal.org/node/1202
		String noiseList="about,after,all,also,an,and,another,any,are,as,at,be,because,been,before" + 
						 "being,between,both,but,by,came,can,come,could,did,do,each,for,from,get" + 
						 "got,has,had,he,have,her,here,him,himself,his,how,if,in,into,is,it,like" + 
						 "make,many,me,might,more,most,much,must,my,never,now,of,on,only,or,other" + 
						 "our,out,over,said,same,see,should,since,some,still,such,take,than,that" + 
						 "the,their,them,then,there,these,they,this,those,through,to,too,under,up" + 
						 "very,was,way,we,well,were,what,where,which,while,who,with,would,you,your,a" + 
						 "b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,$,1,2,3,4,5,6,7,8,9,0,_";
		for(String word : noiseList.split(",")){
			noiseWords.add(word);
		}
		
		out.println("{");
		out.println("\"tweets\": [");
	}
	
	private String[] getKeywords(String text){
		Set<String> out=new HashSet<String>();
		//split on word boundary
		String[] words=text.split("\\W+");
		for(String word : words){
			word=word.toLowerCase().trim();
			if(word.length()>2 && !"".equals(word) && !noiseWords.contains(word)){
				out.add(word);
			}
		}
		return out.toArray(new String[0]);
	}

	@Override
	public void write(Tweet tweet) {
		if(!first){
			out.print(",");
		}
		first=false;
		out.println("{");
		out.println("\"name\": \"" + tweet.getUsername() +"\",");
		out.println("\"text\": \"" + tweet.getText() +"\",");
		out.print("\"keywords\": [");
		boolean firstword=true;
		for(String word : getKeywords(tweet.getText())){
			if(!firstword)
				out.print(",");
			firstword=false;
			out.print("\"" + word  +   "\"");
		}
		out.println("],");
		out.println("\"sentiment\": " + tweet.getSentiment());
		out.print("}");
	}

	@Override
	public void index() {
		//do nothing
	}

	@Override
	public void close() {
		out.println("]");
		out.println("}");
		out.close();
	}
}
