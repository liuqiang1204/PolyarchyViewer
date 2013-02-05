package devUtils;

public class GenSQL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "CREATE TABLE films_actors (\n" +
				"	idfilms_actors INTEGER  NOT NULL,\n" +
				"	filmID INTEGER,\n	actorID INTEGER,\n" +
				"	weighted_sum DECIMAL(10,2)," +
				"\n	PRIMARY KEY (idfilms_actors)\n);";
		String n[] = {"actor","director","genres","country","award","year","rank"};
		String ns[] = {"actors","directors","genres","countries","awards","years","ranks"};
		
		for(int i=0;i<n.length;i++){
			String out = s.replaceAll("actors", ns[i]);
			out = out.replaceAll("actor", n[i]);
			System.out.println("");
			System.out.println(out);
		}
	}

}
