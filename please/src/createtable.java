
	import com.rethinkdb.RethinkDB;
	import com.rethinkdb.RethinkDB;
	import com.rethinkdb.gen.exc.ReqlError;
	import com.rethinkdb.gen.exc.ReqlQueryLogicError;
	import com.rethinkdb.model.MapObject;
	import com.rethinkdb.net.Connection;


	
public class createtable {

	public static final RethinkDB r = RethinkDB.r;
	
	public static void main(String[] args) {
		
		Connection conn = r.connection().hostname("ec2-54-173-220-171.compute-1.amazonaws.com").port(28015).connect();
		
//		Connection conn = r.connection().hostname("localhost").port(28015).connect();
		
//		Connection conn = r.connection().hostname("192.168.2.18").port(28015).connect();
	
		
		r.db("test").tableCreate("packet").run(conn);
		
		r.table("packet").insert(r.hashMap("unitNo", "353218071614755").with("lat", "12.96160").with("lng","77.58332")).run(conn);
		r.table("packet").insert(r.hashMap("unitNo", "355217044865989").with("lat", "12.96167").with("lng","77.58372")).run(conn);
		
		conn.close();
		
//		r.table("packet").insert(r.hashMap("unitNo", "355217044879733").with("lat", "12.97200").with("lng","77.64863")).run(conn);
/*		r.table("packet").insert(r.hashMap("unitNo", "355217044897479").with("lat", "12.97200").with("lng","77.64863")).run(conn);
		r.table("packet").insert(r.hashMap("unitNo", "355217044865989").with("	lat", "12.97200").with("lng","77.64863")).run(conn);
*/		/*r.table("books").insert(r.hashMap("unitNo", "353218071614755").with("lat", "12.96232").with("lng","77.64808")).run(conn);
		r.table("books").insert(r.hashMap("unitNo", "353218071614755").with("lat", "12.96223").with("lng","77.64822")).run(conn);
		r.table("books").insert(r.hashMap("unitNo", "353218071614755").with("lat", "12.96432").with("lng","77.64823")).run(conn);
		r.table("books").insert(r.hashMap("unitNo", "353218071614755").with("lat", "12.96532").with("lng","77.6482")).run(conn);*/
		
		conn.close(false);
		/*r.table("authors").insert(r.array(
			    r.hashMap("name", "William Adama")
			     .with("tv_show", "Battlestar Galactica")
			     .with("posts", r.array(
			        r.hashMap("title", "Decommissioning speech")
			         .with("content", "The Cylon War is long over..."),
			        r.hashMap("title", "We are at war")
			         .with("content", "Moments ago, this ship received..."),
			        r.hashMap("title", "The new Earth")
			         .with("content", "The discoveries of the past few days...")
			        )
			    ),
			    r.hashMap("name", "Laura Roslin")
			     .with("tv_show", "Battlestar Galactica")
			     .with("posts", r.array(
			        r.hashMap("title", "The oath of office")
			         .with("content", "I, Laura Roslin, ..."),
			        r.hashMap("title", "They look like us")
			         .with("content", "The Cylons have the ability...")
			        )
			    ),
			    r.hashMap("name", "Jean-Luc Picard")
			     .with("tv_show", "Star Trek TNG")
			     .with("posts", r.array(
			        r.hashMap("title", "Civil rights")
			         .with("content", "There are some words I've known since...")
			        )
			    )
			)).run(conn);
	
		
*/	}
}
