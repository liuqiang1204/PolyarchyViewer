package imdb2db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Get film metadata from imdb.com
 * 
 * @author Qiang Liu
 * 
 */
public class IMDBFilms {

	public ArrayList<Film> films = new ArrayList<Film>();

	String MySQLDriver = "com.mysql.jdbc.Driver";
	Connection mysqlConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/IMDB?user=root&password=admin";

	public IMDBFilms() {

	}

	public void writeAllToMySql() {
		/**
		 * limitations: only after running the imdb.sql!!!
		 */
		try {
			Class.forName(MySQLDriver).newInstance();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);

			Statement st = mysqlConn.createStatement();
			mysqlConn.setAutoCommit(true);

			for (Film f : films) {
				System.out.println("-----"+f.filmID);
				// 0.table films
				System.out.println("0.table films");
				String sql = "insert into films values(" + f.filmID + ",'"
						+ f.filmName + "','" + f.languages + "','" + f.plot
						+ "')";
				st.execute(sql);

				// 1.actors_hierachy and films_actors --need update gender
				System.out.println("1.actors");
				for (String ac : f.actors) {
					sql = "Select idhierarchy from actors_hierarchy where Label = '"
							+ ac + "'";
					ResultSet rs = st.executeQuery(sql);
					int actorID = -1;
					if (rs.next()) {
						actorID = rs.getInt(1);
					} else {
						String sql1 = "INSERT INTO actors_hierarchy(parentid, Level, Label) "
								+ "VALUES (1, 2, '" + ac + "')";
						st.execute(sql1);
						rs = st.executeQuery(sql);
						if (rs.next())
							actorID = rs.getInt(1);
					}

					sql = "insert into films_actors(filmID, actorID, weighted_sum) VALUES ("
							+ f.filmID + ", " + actorID + ", 1)";
					// System.out.println(sql);
					st.execute(sql);
				}

				// 2.directors_hierarchy & films_directors --need update gender
				System.out.println("2.directors");

				String dr = f.director;
				sql = "Select idhierarchy from directors_hierarchy where Label = '"
						+ dr + "'";
				ResultSet rs = st.executeQuery(sql);
				int drID = -1;
				if (rs.next()) {
					drID = rs.getInt(1);
				} else {
					String sql1 = "INSERT INTO directors_hierarchy(parentid, Level, Label) "
							+ "VALUES (1, 2, '" + dr + "')";
					st.execute(sql1);
					rs = st.executeQuery(sql);
					if (rs.next())
						drID = rs.getInt(1);
				}

				sql = "insert into films_directors(filmID, directorID, weighted_sum) VALUES ("
						+ f.filmID + ", " + drID + ", 1)";
				// System.out.println(sql);
				st.execute(sql);

				// 3.genres
				System.out.println("3.genres");
				for (String gr : f.genres) {
					sql = "Select idhierarchy from genres_hierarchy where Label = '"
							+ gr + "'";
					rs = st.executeQuery(sql);
					int genresID = -1;
					if (rs.next()) {
						genresID = rs.getInt(1);
					} else {
						String sql1 = "INSERT INTO genres_hierarchy(parentid, Level, Label) "
								+ "VALUES (0, 1, '" + gr + "')";
						st.execute(sql1);
						rs = st.executeQuery(sql);
						if (rs.next())
							genresID = rs.getInt(1);
					}

					sql = "insert into films_genres(filmID, genresID, weighted_sum) VALUES ("
							+ f.filmID + ", " + genresID + ", 1)";
					st.execute(sql);
				}
				// 4.country --need to update
				System.out.println("4.country");
				String ct = f.country;
				sql = "Select idhierarchy from countries_hierarchy where Label = '"
						+ ct + "'";
				rs = st.executeQuery(sql);
				int ctID = -1;
				if (rs.next()) {
					ctID = rs.getInt(1);
				} else {
					String sql1 = "INSERT INTO countries_hierarchy(parentid, Level, Label) "
							+ "VALUES (-1, 2, '" + ct + "')";
					st.execute(sql1);
					rs = st.executeQuery(sql);
					if (rs.next())
						ctID = rs.getInt(1);
				}

				sql = "insert into films_countries(filmID, countryID, weighted_sum) VALUES ("
						+ f.filmID + ", " + ctID + ", 1)";
				st.execute(sql);
				// 5.awards
				System.out.println("5.awards");
				for (Award a : f.awards) {
					// a.AwardName; 1
					// a.category; 3
					// a.result; 2
					// a.year;
					sql = "Select idhierarchy from awards_hierarchy where Label ='"
							+ a.AwardName + "' and parentID=0";
					rs = st.executeQuery(sql);
					int l1ID = -1;
					if (rs.next())
						l1ID = rs.getInt(1);
					else {
						String sql1 = "INSERT INTO awards_hierarchy(parentid, Level, Label) "
								+ "VALUES (0, 1, '" + a.AwardName + "')";
						st.execute(sql1);
						rs = st.executeQuery(sql);
						if (rs.next())
							l1ID = rs.getInt(1);
					}

					sql = "Select idhierarchy from awards_hierarchy where Label ='"
							+ a.result + "' and parentid=" + l1ID;
					rs = st.executeQuery(sql);
					int l2ID = -1;
					if (rs.next())
						l2ID = rs.getInt(1);
					else {
						String sql1 = "INSERT INTO awards_hierarchy(parentid, Level, Label) "
								+ "VALUES ("
								+ l1ID
								+ ", 2, '"
								+ a.result
								+ "')";
						// System.out.println(sql1);
						st.execute(sql1);
						rs = st.executeQuery(sql);
						if (rs.next())
							l2ID = rs.getInt(1);
					}

					sql = "Select idhierarchy from awards_hierarchy where Label ='"
							+ a.category + "' and parentid=" + l2ID;
					rs = st.executeQuery(sql);
					int l3ID = -1;
					if (rs.next())
						l3ID = rs.getInt(1);
					else {
						String sql1 = "INSERT INTO awards_hierarchy(parentid, Level, Label) "
								+ "VALUES ("
								+ l2ID
								+ ", 3, '"
								+ a.category
								+ "')";
						st.execute(sql1);
						// System.out.println(sql1);
						// System.out.println(sql);
						rs = st.executeQuery(sql);
						if (rs.next())
							l3ID = rs.getInt(1);
					}

					sql = "insert into films_awards(filmID, awardID, weighted_sum) VALUES ("
							+ f.filmID + ", " + l3ID + ", 1)";
					// System.out.println(sql);
					st.execute(sql);
				}
				// 6.ranks 1)1-20 2)21-50 3)51-100 4)100-150 5)150-200 6)others
				System.out.println("6.ranks");
				int rank = f.rank;
				int ranksID;
				if (rank > 0 && rank < 21)
					ranksID = 1;
				else if (rank > 20 && rank < 51)
					ranksID = 2;
				else if (rank > 50 && rank < 101)
					ranksID = 3;
				else if (rank > 100 && rank < 151)
					ranksID = 4;
				else if (rank > 150 && rank < 201)
					ranksID = 5;
				else
					ranksID = 6;

				sql = "insert into films_ranks(filmID, rankID, weighted_sum) VALUES ("
						+ f.filmID + ", " + ranksID + ", '1')";
				st.execute(sql);
				// 7.years XXXXs
				System.out.println("7.years");
				int yr = f.year;
				sql = "Select idhierarchy from years_hierarchy where Label = '"
						+ yr + "'";
				rs = st.executeQuery(sql);
				int yrID = -1;
				if (rs.next()) {
					yrID = rs.getInt(1);
				} else {
					int decadeID = yr / 10 - 191;
					if (decadeID > 10)
						decadeID = 10;
					if (decadeID < 1)
						decadeID = 1;
					String sql1 = "INSERT INTO years_hierarchy(parentid, Level, Label) "
							+ "VALUES (" + decadeID + ", 2, '" + yr + "')";
					st.execute(sql1);
					rs = st.executeQuery(sql);
					if (rs.next())
						yrID = rs.getInt(1);
				}

				sql = "insert into films_years(filmID, yearID, weighted_sum) VALUES ("
						+ f.filmID + "," + yrID + ",1)";
				st.execute(sql);
			}
			mysqlConn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getFilmsByURLFile(String filepath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			int rank = 1;
			String line = br.readLine();
			while (line != null) {
				Film f = new Film();
				f.imdbURL = line.split(",")[0];
				f.rank = rank++;
				f.filmID = f.rank;
				films.add(f);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {

		IMDBFilms imdb = new IMDBFilms();
		imdb.getFilmsByURLFile("./database/IMDB_TOP250_URL.txt");
		// Film f = imdb.films.get(0);
		// f.getInfoFromIMDB();
		// System.out.println(f.toFormattedString());

		for (Film f : imdb.films) {
			f.getInfoFromIMDB();
			System.out.println(f.filmID);
		}

		// imdb.films.clear();
		// imdb.films.add(f);
		imdb.writeAllToMySql();
	}

}
