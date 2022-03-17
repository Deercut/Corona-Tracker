/**
 * 
 */
package services;



import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import models.LocationStats;

/**
 * @author songo
 *
 */

// On vas ajouter nos annotations utiles comme service

@Service
public class CoronaVirusDataService {

	// C'est la classe qui vas nous fournir la data et faire les requêtes call pour 
	//Afficher les data.
	
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	
	
	
	//Post vas dire tu me fait ce qu'il y a avant le code et après tu fait appel à notre méthode fetch
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		
		List<LocationStats> newStats = new ArrayList<>();
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL))
				.build();
		// Ici on vas définir le paterne de la request et de la réponse
		
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(httpResponse.body());
		
		// Ici on vas donc faire une request et essayer d'avoir la réponse de notre DATA
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStats = new LocationStats();
		    locationStats.setState(record.get("Province/State"));
		    locationStats.setCountry(record.get("Country/Region"));
		    locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    System.out.println(locationStats);
		    newStats.add(locationStats);
		    
		}
		
		this.allStats = newStats;
		       
		
		
		
	}
	
	
	
}
