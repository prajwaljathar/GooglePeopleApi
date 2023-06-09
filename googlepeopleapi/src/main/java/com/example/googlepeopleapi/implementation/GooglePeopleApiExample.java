package com.example.googlepeopleapi.implementation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//import com.fasterxml.jackson.core.sym.Name;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.PeopleService;

import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
 



public class GooglePeopleApiExample {
	private static final String APPLICATION_NAME = "peopleapi";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS);
	private static final String CREDENTIALS_FILE_PATH = "/details.json";
//	private static final String APIKEY ="AIzaSyBNVBS8Tg0-TdNWhtINKELks2xfjHknaD8";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 * 
	 */

private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		
		// Load client secrets.
		InputStream in = GooglePeopleApiExample.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flows = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flows, receiver).authorize("user");
	}
	


	public static void main(String... args) throws IOException, GeneralSecurityException {
		
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		PeopleService service = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		
		

		
		// Request 10 connections.
		ListConnectionsResponse response = service.people().connections().list("people/me").setPageSize(10)
				.setPersonFields("names,emailAddresses").execute();

		
		// Print display name of connections if available.

		List<Person> connections = response.getConnections();
		if (connections != null && connections.size() > 0) {
			for (Person person : connections) {
				List<com.google.api.services.people.v1.model.Name> names1 = person.getNames();
				if (names1 != null && names1.size() > 0) {
					System.out.println("Name: " + person.getNames().get(0).getDisplayName());
				} else {
					System.out.println("No names available for connection.");
				}
			}
		} else {
			System.out.println("No connections found.");

		}

		
		Person contactToCreate = new Person();
		
		PhoneNumber phoneNumber = new PhoneNumber();
		
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		List<Name> names = new ArrayList<>();
		phoneNumber.setValue("9167536959");
		
		names.add(new Name().setGivenName("John").setFamilyName("Doe"));
		phoneNumbers.add(phoneNumber);
		contactToCreate.setNames(names);

		contactToCreate.setPhoneNumbers(phoneNumbers);
		
		Person createdContact = service.people().createContact(contactToCreate).execute();
			
		System.out.println(createdContact.toString());
		
	


	}
}