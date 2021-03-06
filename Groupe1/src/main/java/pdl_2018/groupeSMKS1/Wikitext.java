package pdl_2018.groupeSMKS1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sweble.wikitext.engine.ExpansionCallback;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtTable;
import org.sweble.wikitext.parser.nodes.WtTableCell;
import org.sweble.wikitext.parser.nodes.WtTableRow;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.WtXmlAttribute;
import org.sweble.wikitext.parser.nodes.WtBody;
import org.sweble.wikitext.parser.nodes.WtXmlAttributes;
import org.wikipedia.Wiki;

public class Wikitext extends Extracteur {
	private String domain;
	private String sousDomain;
	private char delimit;
	private String cheminCSV;
	private String nomCSV;
	private boolean extraHTML;
	private boolean extraWiki;
	private ArrayList<Tableau> lesTableaux;
	private Map<Integer, WtBody> lesWikitab;
	private int compteur = 0;

	public Wikitext(String domain, String sousDomain, char delimit, String cheminCSV, String nomCSV, boolean extraHTML,
			boolean extraWiki) {
		this.domain = domain;
		this.sousDomain = sousDomain;
		this.delimit = delimit;
		this.nomCSV = nomCSV;
		this.cheminCSV = cheminCSV;
		this.extraHTML = extraHTML;
		this.extraWiki = extraWiki;
		lesTableaux = new ArrayList<Tableau>();
		lesWikitab = new HashMap<Integer, WtBody>();

	}

	@Override
	public void recuperationPage() {
		try {
			Wiki wikisweble = new Wiki("fr.wikipedia.org");
			String contenu = wikisweble.getPageText(sousDomain);

			wikiconfig(contenu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void wikiconfig(String contenu) {

		try {
			WikiConfig config = DefaultConfigEnWp.generate();

			WtEngineImpl engine = new WtEngineImpl(config);
			PageTitle pageTitle = PageTitle.make(config, "title");
			PageId pageId = new PageId(pageTitle, -1);
			ExpansionCallback callback = null;
			EngProcessedPage parse = engine.parse(pageId, contenu, callback);

			parcourirNode(parse);
			System.out.println(compteur);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean findClassWikitable(WtXmlAttributes e) {

		int compteur = 0;
		while (e.size() > compteur) {

			WtXmlAttribute attribut = (WtXmlAttribute) e.get(compteur);
			if (attribut.toString().contains("wikitable")) {
				return true;
			}

			compteur++;
		}

		return false;
	}

	private void parcourirNode(WtNode fils) {
		for (Iterator<WtNode> l = fils.iterator(); l.hasNext();) {
			fils = l.next();

			if (fils.getNodeType() == WtTable.NT_TABLE) {
				WtTable table = (WtTable) fils;

				WtXmlAttributes e = table.getXmlAttributes();
				if (findClassWikitable(e)) {

					compteur++;
					lesWikitab.put(compteur, table.getBody());
				}
			}
			parcourirNode(fils);

		}

	}

	public void rechercheColRow(WtNode fils, String[][] tab) {

		for (Iterator<WtNode> l = fils.iterator(); l.hasNext();) {
			WtNode node = l.next();
			if (node.getNodeType() == WtTableRow.NT_TABLE_ROW) {
				// System.out.println("R");

			}
			if (node.getNodeType() == WtTableRow.NT_TABLE_HEADER) {
				// System.out.println("H");
			}

			if (node.getNodeType() == WtTableRow.NT_TABLE_CELL) {
				// System.out.println("C");
			}
			if (node.getNodeType() == WtText.NT_TEXT) {

				WtText text = (WtText) node;
				System.out.println(text.getContent());
			}

			rechercheColRow(node, tab);
		}
	}

	public void traitementMap2() {

		Set cles = lesWikitab.keySet();
		Iterator<Integer> it = cles.iterator();
		while (it.hasNext()) {
			Integer cle = it.next();
			WtBody ensemble = lesWikitab.get(cle);
			// System.out.println(ensemble);
			String[][] tab = new String[100][100];
			rechercheColRow(ensemble, tab);
		}
		System.out.println(lesWikitab.size());
	}

	/**
	 * 
	 * @return
	 */
	public void TraitementMap() {

		Set cles = lesWikitab.keySet();
		Iterator<Integer> it = cles.iterator();
		while (it.hasNext()) {
			Integer cle = it.next();
			WtBody ensemble = lesWikitab.get(cle);
			int counter = 0;

			while (ensemble.size() > counter) {
				if (ensemble.get(counter).getNodeType() == WtTableRow.NT_TABLE_ROW) {

					WtTableRow row = (WtTableRow) ensemble.get(counter);
					int counterrow = 0;
					while (row.size() > counterrow) {
						System.out.println(row.get(counterrow));
						System.out.println(row.get(counterrow));
						// System.out.println(WtTableCell.NT_TABLE_CELL);

						if (row.get(counterrow).getNodeType() == WtTableCell.NT_TABLE_CELL) {
							WtTableCell cell = (WtTableCell) row.get(counterrow);
							// System.out.println(cell.toString());
						}
						counterrow++;
					}

				}
				// System.out.println(ensemble.get(counter).toString());
				counter++;

			}

		}
	}

	@Override
	public void removeTableau() {
	}

	@Override
	public String getNomTableau() {
		return "";
	}

	public String getDomain() {
		return domain;
	}

	public String getSousDomain() {
		return sousDomain;
	}

	@Override
	public void addTableau(Tableau leTableau) {
		if (!lesTableaux.contains(leTableau)) {
			lesTableaux.add(leTableau);
		}
	}

	@Override
	public Tableau constructeurTableau(char delimit, String cheminCSV, String NomCSV, boolean extraHTML,
			boolean extraWiki) {
		return new Tableau();
	}

	/**
	 * 
	 * @return le d�limiteur choisit par l'utilisateur
	 */
	public char getDelimit() {
		return this.delimit;
	}

	/**
	 * 
	 * @return le chemin de sauvegarde du fichier choisit par l'utilisateur
	 */
	public String getCheminCSV() {
		return this.cheminCSV;
	}

	/**
	 * 
	 * @return le nom du fichier CSV choisit par l'utilisateur
	 */
	public String getNomCSV() {
		return this.nomCSV;
	}

	/**
	 * 
	 * @return un boolean qui indique si l'extraction doit �tre faire en HTML (true)
	 *         ou non (false)
	 */
	public boolean getExtraHTML() {
		return this.extraHTML;
	}

	/**
	 * 
	 * @return Un boolean qui indique si l'extraction doit �tre faire en
	 *         wikicode(true) ou non (false)
	 */
	public boolean getExtraWiki() {
		return this.extraWiki;
	}

	public static void main(String[] args) {
		Wikitext t = new Wikitext("fr.wikipedia.org", "Équipe_de_France_masculine_de_football", ';', "chemin",
				" nomCSV", false, true);
		t.recuperationPage();
		t.traitementMap2();
//		Set cles = t.lesWikitab.keySet();
//		Iterator<Integer> it = cles.iterator();
//		while (it.hasNext()) {
//			Integer cle = it.next();
//			WtBody ensemble = t.lesWikitab.get(cle);
//			// System.out.println(ensemble);
//			t.TraitementMap();
//		}

	}
}
