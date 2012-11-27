package aic.service.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.Mongo;

public class SimpleTest {
	
	private Mongo mongo;
	private DB db;
	private DBCollection tweetsCollection;
	private Set<String> noiseWords=new HashSet<String>();
	
	public SimpleTest(String dbHost, String dbName) throws UnknownHostException {
		mongo = new Mongo(dbHost);
		db = mongo.getDB(dbName);
		tweetsCollection = db.getCollection("tweets");
		
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
	

	public double analyseSentiment(String company) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("keywords", getKeywords(company));
		//dbo.put("text", Pattern.compile(".*"+company+".*"));
		//DBCursor cursor = tweetsCollection.find(dbo);
		
		
        //try out map/reduce
		String map = "function(){emit(null,{sentiment: this.sentiment});};";
		String reduce = "function(key,values){" + "var result = 0.0;"
				+ "values.forEach(function(value) {"
				+ "  result += value.sentiment;" + "});"
				+ "return { sentiment: result/values.length };" + "};";
		MapReduceOutput out = tweetsCollection.mapReduce(map, reduce, null,
				MapReduceCommand.OutputType.INLINE, dbo);
		return (Double) ((DBObject) out.results().iterator().next()
				.get("value")).get("sentiment");
	}
	
	public static void main(String[] args) throws Exception {
		final String[] searchTerms = { "Alina", "Adrian", "Amelie",
				"Alexander", "Angelina", "Ali", "Anna", "Benjamin", "Azra",
				"Daniel", "Clara", "David", "Elena", "Dominik", "Elif",
				"Elias", "Ella", "Emil", "Emilia", "Fabian", "Emily", "Felix",
				"Emma", "Filip", "Hanna", "Florian", "Hannah", "Gabriel",
				"Helena", "Jakob", "Isabella", "Jonas", "Johanna", "Jonathan",
				"Julia", "Julian", "Katharina", "Konstantin", "Lara", "Leo",
				"Larissa", "Leon", "Laura", "Luca", "Lea", "Luis", "Lena",
				"Luka", "Leonie", "Lukas", "Lina", "Marcel", "Lisa",
				"Matthias", "Livia", "Maximilian", "Magdalena", "Moritz",
				"Marie", "Muhammed", "Mia", "Nico", "Nina", "Niklas", "Sara",
				"Noah", "Sarah", "Paul", "Sofia", "Philipp", "Sophia",
				"Raphael", "Sophie", "Samuel", "Valentina", "Sebastian",
				"Valerie", "Simon", "Vanessa", "Stefan", "Victoria", "Tobias",
				"Viktoria", "Valentin", "apple", "microsoft",
				"nova science now", "youtube", "google", "sap", "ibm",
				"redhat", "linux", "ubuntu", "fedora", "disney", "lucas arts",
				"lucas film", "gorge lucas", "star wars", "clone wars",
				"star trek", "andreas", "austria", "germany", "america",
				"lettland", "russia", "europe", "irak", "arabic spring",
				"afghanistan", "pakistan", "china", "sex", "homosexuality",
				"dildo", "vibrator", "sex toy", "whore", "asshole", "ass",
				"fuck", "fuck you", "Smith", "Johnson", "Williams", "Brown",
				"Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson",
				"MARTINEZ", "ANDERSON", "TAYLOR", "THOMAS", "HERNANDEZ",
				"MOORE", "MARTIN", "JACKSON", "THOMPSON", "WHITE", "LOPEZ",
				"LEE", "GONZALEZ", "HARRIS", "CLARK", "LEWIS", "ROBINSON",
				"WALKER", "PEREZ", "HALL", "YOUNG", "ALLEN", "SANCHEZ",
				"WRIGHT", "KING", "SCOTT", "GREEN", "BAKER", "ADAMS", "NELSON",
				"HILL", "RAMIREZ", "CAMPBELL", "MITCHELL", "ROBERTS", "CARTER",
				"PHILLIPS", "EVANS", "TURNER", "TORRES", "PARKER", "COLLINS",
				"EDWARDS", "STEWART", "FLORES", "MORRIS", "NGUYEN", "MURPHY",
				"RIVERA", "COOK", "ROGERS", "MORGAN", "PETERSON", "COOPER",
				"REED", "BAILEY", "BELL", "GOMEZ", "KELLY", "HOWARD", "WARD",
				"COX", "DIAZ", "RICHARDSON", "WOOD", "WATSON", "BROOKS",
				"BENNETT", "GRAY", "JAMES", "REYES", "CRUZ", "HUGHES", "PRICE",
				"MYERS", "LONG", "FOSTER", "SANDERS", "ROSS", "MORALES",
				"POWELL", "SULLIVAN", "RUSSELL", "ORTIZ", "JENKINS",
				"GUTIERREZ", "PERRY", "BUTLER", "BARNES", "FISHER",
				"HENDERSON", "COLEMAN", "SIMMONS", "PATTERSON", "JORDAN",
				"REYNOLDS", "HAMILTON", "GRAHAM", "KIM", "GONZALES",
				"ALEXANDER", "RAMOS", "WALLACE", "GRIFFIN", "WEST", "COLE",
				"HAYES", "CHAVEZ", "GIBSON", "BRYANT", "ELLIS", "STEVENS",
				"MURRAY", "FORD", "MARSHALL", "OWENS", "MCDONALD", "HARRISON",
				"RUIZ", "KENNEDY", "WELLS", "ALVAREZ", "WOODS", "MENDOZA",
				"CASTILLO", "OLSON", "WEBB", "WASHINGTON", "TUCKER", "FREEMAN",
				"BURNS", "HENRY", "VASQUEZ", "SNYDER", "SIMPSON", "CRAWFORD",
				"JIMENEZ", "PORTER", "MASON", "SHAW", "GORDON", "WAGNER",
				"HUNTER", "ROMERO", "HICKS", "DIXON", "HUNT", "PALMER",
				"ROBERTSON", "BLACK", "HOLMES", "STONE", "MEYER", "BOYD",
				"MILLS", "WARREN", "FOX", "ROSE", "RICE", "MORENO", "SCHMIDT",
				"PATEL", "FERGUSON", "NICHOLS", "HERRERA", "MEDINA", "RYAN",
				"FERNANDEZ", "WEAVER", "DANIELS", "STEPHENS", "GARDNER",
				"PAYNE", "KELLEY", "DUNN", "PIERCE", "ARNOLD", "TRAN",
				"SPENCER", "PETERS", "HAWKINS", "GRANT", "HANSEN", "CASTRO",
				"HOFFMAN", "HART", "ELLIOTT", "CUNNINGHAM", "KNIGHT",
				"BRADLEY", "CARROLL", "HUDSON", "DUNCAN", "ARMSTRONG", "BERRY",
				"ANDREWS", "JOHNSTON", "RAY", "LANE", "RILEY", "CARPENTER",
				"PERKINS", "AGUILAR", "SILVA", "RICHARDS", "WILLIS",
				"MATTHEWS", "CHAPMAN", "LAWRENCE", "GARZA", "VARGAS",
				"WATKINS", "WHEELER", "LARSON", "CARLSON", "HARPER", "GEORGE",
				"GREENE", "BURKE", "GUZMAN", "MORRISON", "MUNOZ", "JACOBS",
				"OBRIEN", "LAWSON", "FRANKLIN", "LYNCH", "BISHOP", "CARR",
				"SALAZAR", "AUSTIN", "MENDEZ", "GILBERT", "JENSEN",
				"WILLIAMSON", "MONTGOMERY", "HARVEY", "OLIVER", "HOWELL",
				"DEAN", "HANSON", "WEBER", "GARRETT", "SIMS", "BURTON",
				"FULLER", "SOTO", "MCCOY", "WELCH", "CHEN", "SCHULTZ",
				"WALTERS", "REID", "FIELDS", "WALSH", "LITTLE", "FOWLER",
				"BOWMAN", "DAVIDSON", "MAY", "DAY", "SCHNEIDER", "NEWMAN",
				"BREWER", "LUCAS", "HOLLAND", "WONG", "BANKS", "SANTOS",
				"CURTIS", "PEARSON", "DELGADO", "VALDEZ", "PENA", "RIOS",
				"DOUGLAS", "SANDOVAL", "BARRETT", "HOPKINS", "KELLER",
				"GUERRERO", "STANLEY", "BATES", "ALVARADO", "BECK", "ORTEGA",
				"WADE", "ESTRADA", "CONTRERAS", "BARNETT", "CALDWELL",
				"SANTIAGO", "LAMBERT", "POWERS", "CHAMBERS", "NUNEZ", "CRAIG",
				"LEONARD", "LOWE", "RHODES", "BYRD", "GREGORY", "SHELTON",
				"FRAZIER", "BECKER", "MALDONADO", "FLEMING", "VEGA", "SUTTON",
				"COHEN", "JENNINGS", "PARKS", "MCDANIEL", "WATTS", "BARKER",
				"NORRIS", "VAUGHN", "VAZQUEZ", "HOLT", "SCHWARTZ", "STEELE",
				"BENSON", "NEAL", "DOMINGUEZ", "HORTON", "TERRY", "WOLFE",
				"HALE", "LYONS", "GRAVES", "HAYNES", "MILES", "PARK", "WARNER",
				"PADILLA", "BUSH", "THORNTON", "MCCARTHY", "MANN", "ZIMMERMAN",
				"ERICKSON", "FLETCHER", "MCKINNEY", "PAGE", "DAWSON", "JOSEPH",
				"MARQUEZ", "REEVES", "KLEIN", "ESPINOZA", "BALDWIN", "MORAN",
				"LOVE", "ROBBINS", "HIGGINS", "BALL", "CORTEZ", "LE",
				"GRIFFITH", "BOWEN", "SHARP", "CUMMINGS", "RAMSEY", "HARDY",
				"SWANSON", "BARBER", "ACOSTA", "LUNA", "CHANDLER", "BLAIR",
				"DANIEL", "CROSS", "SIMON", "DENNIS", "OCONNOR", "QUINN",
				"GROSS", "NAVARRO", "MOSS", "FITZGERALD", "DOYLE",
				"MCLAUGHLIN", "ROJAS", "RODGERS", "STEVENSON", "SINGH", "YANG",
				"FIGUEROA", "HARMON", "NEWTON", "PAUL", "MANNING", "GARNER",
				"MCGEE", "REESE", "FRANCIS", "BURGESS", "ADKINS", "GOODMAN",
				"CURRY", "BRADY", "CHRISTENSEN", "POTTER", "WALTON", "GOODWIN",
				"MULLINS", "MOLINA", "WEBSTER", "FISCHER", "CAMPOS", "AVILA",
				"SHERMAN", "TODD", "CHANG", "BLAKE", "MALONE", "WOLF",
				"HODGES", "JUAREZ", "GILL", "FARMER", "HINES", "GALLAGHER",
				"DURAN", "HUBBARD", "CANNON", "MIRANDA", "WANG", "SAUNDERS",
				"TATE", "MACK", "HAMMOND", "CARRILLO", "TOWNSEND", "WISE",
				"INGRAM", "BARTON", "MEJIA", "AYALA", "SCHROEDER", "HAMPTON",
				"ROWE", "PARSONS", "FRANK", "WATERS", "STRICKLAND", "OSBORNE",
				"MAXWELL", "CHAN", "DELEON", "NORMAN", "HARRINGTON", "CASEY",
				"PATTON", "LOGAN", "BOWERS", "MUELLER", "GLOVER", "FLOYD",
				"HARTMAN", "BUCHANAN", "COBB", "FRENCH", "KRAMER", "MCCORMICK",
				"CLARKE", "TYLER", "GIBBS", "MOODY", "CONNER", "SPARKS",
				"MCGUIRE", "LEON", "BAUER", "NORTON", "POPE", "FLYNN", "HOGAN",
				"ROBLES", "SALINAS", "YATES", "LINDSEY", "LLOYD", "MARSH",
				"MCBRIDE", "OWEN", "SOLIS", "PHAM", "LANG", "PRATT", "LARA",
				"BROCK", "BALLARD", "TRUJILLO", "SHAFFER", "DRAKE", "ROMAN",
				"AGUIRRE", "MORTON", "STOKES", "LAMB", "PACHECO", "PATRICK",
				"COCHRAN", "SHEPHERD", "CAIN", "BURNETT", "HESS", "LI",
				"CERVANTES", "OLSEN", "BRIGGS", "OCHOA", "CABRERA",
				"VELASQUEZ", "MONTOYA", "ROTH", "MEYERS", "CARDENAS",
				"FUENTES", "WEISS", "HOOVER", "WILKINS", "NICHOLSON",
				"UNDERWOOD", "SHORT", "CARSON", "MORROW", "COLON", "HOLLOWAY",
				"SUMMERS", "BRYAN", "PETERSEN", "MCKENZIE", "SERRANO",
				"WILCOX", "CAREY", "CLAYTON", "POOLE", "CALDERON", "GALLEGOS",
				"GREER", "RIVAS", "GUERRA", "DECKER", "COLLIER", "WALL",
				"WHITAKER", "BASS", "FLOWERS", "DAVENPORT", "CONLEY",
				"HOUSTON", "HUFF", "COPELAND", "HOOD", "MONROE", "MASSEY",
				"ROBERSON", "COMBS", "FRANCO", "LARSEN", "PITTMAN", "RANDALL",
				"SKINNER", "WILKINSON", "KIRBY", "CAMERON", "BRIDGES",
				"ANTHONY", "RICHARD", "KIRK", "BRUCE", "SINGLETON", "MATHIS",
				"BRADFORD", "BOONE", "ABBOTT", "CHARLES", "ALLISON", "SWEENEY",
				"ATKINSON", "HORN", "JEFFERSON", "ROSALES", "YORK",
				"CHRISTIAN", "PHELPS", "FARRELL", "CASTANEDA", "NASH",
				"DICKERSON", "BOND", "WYATT", "FOLEY", "CHASE", "GATES",
				"VINCENT", "MATHEWS", "HODGE", "GARRISON", "TREVINO",
				"VILLARREAL", "HEATH", "DALTON", "VALENCIA", "CALLAHAN",
				"HENSLEY", "ATKINS", "HUFFMAN", "ROY", "BOYER", "SHIELDS",
				"LIN", "HANCOCK", "GRIMES", "GLENN", "CLINE", "DELACRUZ",
				"CAMACHO", "DILLON", "PARRISH", "ONEILL", "MELTON", "BOOTH",
				"KANE", "BERG", "HARRELL", "PITTS", "SAVAGE", "WIGGINS",
				"BRENNAN", "SALAS", "MARKS", "RUSSO", "SAWYER", "BAXTER",
				"GOLDEN", "HUTCHINSON", "LIU", "WALTER", "MCDOWELL", "WILEY",
				"RICH", "HUMPHREY", "JOHNS", "KOCH", "SUAREZ", "HOBBS",
				"BEARD", "GILMORE", "IBARRA", "KEITH", "MACIAS", "KHAN",
				"ANDRADE", "WARE", "STEPHENSON", "HENSON", "WILKERSON", "DYER",
				"MCCLURE", "BLACKWELL", "MERCADO", "TANNER", "EATON", "CLAY",
				"BARRON", "BEASLEY", "ONEAL", "PRESTON", "SMALL", "WU",
				"ZAMORA", "MACDONALD", "VANCE", "SNOW", "MCCLAIN", "STAFFORD",
				"OROZCO", "BARRY", "ENGLISH", "SHANNON", "KLINE", "JACOBSON",
				"WOODARD", "HUANG", "KEMP", "MOSLEY", "PRINCE", "MERRITT",
				"HURST", "VILLANUEVA", "ROACH", "NOLAN", "LAM", "YODER",
				"MCCULLOUGH", "LESTER", "SANTANA", "VALENZUELA", "WINTERS",
				"BARRERA", "LEACH", "ORR", "BERGER", "MCKEE", "STRONG",
				"CONWAY", "STEIN", "WHITEHEAD", "BULLOCK", "ESCOBAR", "KNOX",
				"MEADOWS", "SOLOMON", "VELEZ", "ODONNELL", "KERR", "STOUT",
				"BLANKENSHIP", "BROWNING", "KENT", "LOZANO", "BARTLETT",
				"PRUITT", "BUCK", "BARR", "GAINES", "DURHAM", "GENTRY",
				"MCINTYRE", "SLOAN", "MELENDEZ", "ROCHA", "HERMAN", "SEXTON",
				"MOON", "HENDRICKS", "RANGEL", "STARK", "LOWERY", "HARDIN",
				"HULL", "SELLERS", "ELLISON", "CALHOUN", "GILLESPIE", "MORA",
				"KNAPP", "MCCALL", "MORSE", "DORSEY", "WEEKS", "NIELSEN",
				"LIVINGSTON", "LEBLANC", "MCLEAN", "BRADSHAW", "GLASS",
				"MIDDLETON", "BUCKLEY", "SCHAEFER", "FROST", "HOWE", "HOUSE",
				"MCINTOSH", "HO", "PENNINGTON", "REILLY", "HEBERT",
				"MCFARLAND", "HICKMAN", "NOBLE", "SPEARS", "CONRAD", "ARIAS",
				"GALVAN", "VELAZQUEZ", "HUYNH", "FREDERICK", "RANDOLPH",
				"CANTU", "FITZPATRICK", "MAHONEY", "PECK", "VILLA", "MICHAEL",
				"DONOVAN", "MCCONNELL", "WALLS", "BOYLE", "MAYER", "ZUNIGA",
				"GILES", "PINEDA", "PACE", "HURLEY", "MAYS", "MCMILLAN",
				"CROSBY", "AYERS", "CASE", "BENTLEY", "SHEPARD", "EVERETT",
				"PUGH", "DAVID", "MCMAHON", "DUNLAP", "BENDER", "HAHN",
				"HARDING", "ACEVEDO", "RAYMOND", "BLACKBURN", "DUFFY",
				"LANDRY", "DOUGHERTY", "BAUTISTA", "SHAH", "POTTS", "ARROYO",
				"VALENTINE", "MEZA", "GOULD", "VAUGHAN", "FRY", "RUSH",
				"AVERY", "HERRING", "DODSON", "CLEMENTS", "SAMPSON", "TAPIA",
				"BEAN", "LYNN", "CRANE", "FARLEY", "CISNEROS", "BENTON",
				"ASHLEY", "MCKAY", "FINLEY", "BEST", "BLEVINS", "FRIEDMAN",
				"MOSES", "SOSA", "BLANCHARD", "HUBER", "FRYE", "KRUEGER",
				"BERNARD", "ROSARIO", "RUBIO", "MULLEN", "BENJAMIN", "HALEY",
				"CHUNG", "MOYER", "CHOI", "HORNE", "YU", "WOODWARD", "ALI",
				"NIXON", "HAYDEN", "RIVERS", "ESTES", "MCCARTY", "RICHMOND",
				"STUART", "MAYNARD", "BRANDT", "OCONNELL", "HANNA", "SANFORD",
				"SHEPPARD", "CHURCH", "BURCH", "LEVY", "RASMUSSEN", "COFFEY",
				"PONCE", "FAULKNER", "DONALDSON", "SCHMITT", "NOVAK", "COSTA",
				"MONTES", "BOOKER", "CORDOVA", "WALLER", "ARELLANO", "MADDOX",
				"MATA", "BONILLA", "STANTON", "COMPTON", "KAUFMAN", "DUDLEY",
				"MCPHERSON", "BELTRAN", "DICKSON", "MCCANN", "VILLEGAS",
				"PROCTOR", "HESTER", "CANTRELL", "DAUGHERTY", "CHERRY", "BRAY",
				"DAVILA", "ROWLAND", "LEVINE", "MADDEN", "SPENCE", "GOOD",
				"IRWIN", "WERNER", "KRAUSE", "PETTY", "WHITNEY", "BAIRD",
				"HOOPER", "POLLARD", "ZAVALA", "JARVIS", "HOLDEN", "HAAS",
				"HENDRIX", "MCGRATH", "BIRD", "LUCERO", "TERRELL", "RIGGS",
				"JOYCE", "MERCER", "ROLLINS", "GALLOWAY", "DUKE", "ODOM",
				"ANDERSEN", "DOWNS", "HATFIELD", "BENITEZ", "ARCHER", "HUERTA",
				"TRAVIS", "MCNEIL", "HINTON", "ZHANG", "HAYS", "MAYO", "FRITZ",
				"BRANCH", "MOONEY", "EWING", "RITTER", "ESPARZA", "FREY",
				"BRAUN", "GAY", "RIDDLE", "HANEY", "KAISER", "HOLDER",
				"CHANEY", "MCKNIGHT", "GAMBLE", "VANG", "COOLEY", "CARNEY",
				"COWAN", "FORBES", "FERRELL", "DAVIES", "BARAJAS", "SHEA",
				"OSBORN", "BRIGHT", "CUEVAS", "BOLTON", "MURILLO", "LUTZ",
				"DUARTE", "KIDD", "KEY", "COOKE" };
		
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter database ip/hostname: ");
		final String host=in.readLine();
		System.out.println("Please enter database name(e.g.: tweets): ");
		final String name=in.readLine();
		System.out.println("Please enter # of db requests: ");
		final int req=Integer.parseInt(in.readLine());
		System.out.println("Please enter # of threads: ");
		int threads=Integer.parseInt(in.readLine());
		System.out.println("Please enter output filename for stats: ");
		final String filename=in.readLine();
		final AtomicInteger count=new AtomicInteger(0);
		
		if(req>0 && threads>0){
			for(int j=0;j<threads;j++){
				new Thread(new Runnable(){
					public void run(){
						BufferedWriter out = null;
						try {
							java.util.Random random = new java.util.Random();
							final int start = random.nextInt(searchTerms.length);
							final int end=req+start;
							
							out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + count.getAndIncrement() + ".dat")));
									
							SimpleTest test = new SimpleTest(host,name);
							for(int i=start;i<end;i++){
								long tmp = System.currentTimeMillis();
								test.analyseSentiment(searchTerms[i%searchTerms.length]);
								out.write((System.currentTimeMillis() - tmp) + "\n");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(out!=null){
								try {out.close();} catch (IOException e) {}
							}
						}
					}
				}).start();
			}
		}
		
	}
}