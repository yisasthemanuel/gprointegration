package org.jlobato.gpro.gprointegration;

import java.util.ArrayList;
import java.util.List;

import org.jlobato.gpro.api.StaffApi;
import org.jlobato.gpro.client.ApiClient;
import org.jlobato.gpro.model.AgeDriRange;
import org.jlobato.gpro.model.AvailDriversResponse;
import org.jlobato.gpro.model.AvailDriversResponseDriversInner;
import org.jlobato.gpro.model.DriverProfileResponseFavTrack1;
import org.jlobato.gpro.model.ExpRange;
import org.jlobato.gpro.model.GetDriverProfile200Response;
import org.jlobato.gpro.model.LangOptions;
import org.jlobato.gpro.model.MinSalRange;
import org.jlobato.gpro.model.OffRange;
import org.jlobato.gpro.model.SkillRange;
import org.jlobato.gpro.model.SortDriver;
import org.jlobato.gpro.model.WeiRange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class GproIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GproIntegrationApplication.class, args);
		log.info("GproIntegrationApplication is UP!!!!");

		ApiClient client = new ApiClient();
		client.setBasePath("https://gpro.net/");
		client.setBearerToken(
				"Bearer eyJ0eXAiOiJKV1QiLCAiYWxnIjoiSFMyNTYifQ.eyJpZCI6IDExMzYxMiwiY3JlYXRlZCI6IlN1biBOb3YgMTcgMTY6MzQ6MTYgVVRDKzAxMDAgMjAyNCJ9.5KfLXI-B_12pJ9FSXP8jP26ssmlW_BIFUtSoj9qZ7C0");
		log.info("API Base Path: " + client.getBasePath());

		StaffApi api = new StaffApi(client);
		// uncomment below to test the function
		LangOptions lang = LangOptions.GB;
		Integer page = null;
		Integer minOA = null;
		Integer maxOA = null;
		SortDriver sort = null;
		SortDriver sort2 = null;
		SortDriver sort3 = null;
		SkillRange agr = null;
		SkillRange cha = null;
		SkillRange con = null;
		SkillRange mot = null;
		SkillRange rep = null;
		SkillRange sta = null;
		SkillRange tal = null;
		SkillRange tei = null;
		ExpRange exp = null;
		AgeDriRange age = null;
		MinSalRange minsal = null;
		OffRange offRange = null;
		WeiRange wei = null;
		AvailDriversResponse response = api.getAvailDrivers(lang, page, minOA, maxOA, sort, sort2, sort3, agr, cha, con,
				mot, rep, sta, tal, tei, exp, age, minsal, offRange, wei).block();
		
		Integer pageCount = response.getPageCount();
		Integer currentPage = response.getPageIndex();
		int driversRead = 0;

		List<GetDriverProfile200Response> buenosAires = new ArrayList<>();
		List<GetDriverProfile200Response> lagunaSeca = new ArrayList<>();
		List<GetDriverProfile200Response> both = new ArrayList<>();

		Integer minStamina = 85;
		Integer minConcentration = 170;
		Integer minExperience = 125;

		while (currentPage <= pageCount) {
			log.debug("Number of drivers read {}", response.getDrivers().size());
			driversRead += response.getDrivers().size();

			for(AvailDriversResponseDriversInner driver: response.getDrivers()) {
				Integer driverID = driver.getDriId();

				GetDriverProfile200Response driverProfile = api.getDriverProfile(lang, driverID).block();

				DriverProfileResponseFavTrack1 favTrack1 = driverProfile.getFavTrack1();
				DriverProfileResponseFavTrack1 favTrack2 = driverProfile.getFavTrack2();
				DriverProfileResponseFavTrack1 favTrack3 = driverProfile.getFavTrack3();

				boolean hasBuenosAires = false;
				boolean hasLagunaSeca = false;

				if (Integer.valueOf(2).equals(favTrack1.getId())) {
					hasBuenosAires = true;
				}
				if (Integer.valueOf(35).equals(favTrack1.getId())) {
					hasLagunaSeca = true;
				}
				if (Integer.valueOf(2).equals(favTrack2.getId())) {
					hasBuenosAires = true;
				}
				if (Integer.valueOf(35).equals(favTrack2.getId())) {
					hasLagunaSeca = true;
				}
				if (Integer.valueOf(2).equals(favTrack3.getId())) {
					hasBuenosAires = true;
				}
				if (Integer.valueOf(35).equals(favTrack3.getId())) {
					hasLagunaSeca = true;
				}

				if (hasBuenosAires && hasLagunaSeca) {
					both.add(driverProfile);
					log.debug("BINGO!!! The driver with id {} has Buenos Aires and Laguna Seca as FT!!!", driverID);
				}
				else if (hasBuenosAires) {
					buenosAires.add(driverProfile);
					log.debug("LINE!! The driver with id {} has Buenos Aires as FT!!!", driverID);
				}
				else if (hasLagunaSeca) {
					lagunaSeca.add(driverProfile);
					log.debug("LINE!! The driver with id {} has Laguna Seca as FT!!!", driverID);
				}

				boolean discarded = false;

				if (hasBuenosAires || hasLagunaSeca) {
					if (driverProfile.getStamina() < minStamina) {
						buenosAires.remove(driverProfile);
						lagunaSeca.remove(driverProfile);
						both.remove(driverProfile);
						discarded = true;
						log.info("Driver with id {} discarded because of stamina {}", driverID, driverProfile.getStamina());
					}
					else if (driverProfile.getExperience() < minExperience) {
						buenosAires.remove(driverProfile);
						lagunaSeca.remove(driverProfile);
						both.remove(driverProfile);
						discarded = true;
						log.info("Driver with id {} discarded because of experience {}", driverID, driverProfile.getExperience());
					}
					else if (driverProfile.getConcentration() < minConcentration) {
						buenosAires.remove(driverProfile);
						lagunaSeca.remove(driverProfile);
						both.remove(driverProfile);
						discarded = true;
						log.info("Driver with id {} discarded because of concentration {}", driverID, driverProfile.getConcentration());
					}

					if (!discarded) {
						log.info("Driver match: id {}, name {}, OA {}, Con {}, Tal {}, Agr{}, Exp {}, Tei {}, Sta {}, Cha {}, Mot {}, Rep {}, Wei {}, Age {}, Min sign fee {}, Min salary {}, Offers {}"
							, driverID
							, driverProfile.getDriName()
							, driverProfile.getOverall()
							, driverProfile.getConcentration()
							, driverProfile.getTalent()
							, driverProfile.getAggressiveness()
							, driverProfile.getExperience()
							, driverProfile.getTechInsight()
							, driverProfile.getStamina()
							, driverProfile.getCharisma()
							, driverProfile.getMotivation()
							, driverProfile.getReputation()
							, driverProfile.getWeight()
							, driverProfile.getAge()
							, driverProfile.getOffersFrom().size()
						);
					}
				}
			}

			// Next page of drivers
			log.debug("Response pageCount({}), pageSize({}), pageIndex({})", response.getPageCount(), response.getPageSize(), response.getPageIndex());
			currentPage++;
			response = api.getAvailDrivers(lang, currentPage, minOA, maxOA, sort, sort2, sort3, agr, cha, con,
				mot, rep, sta, tal, tei, exp, age, minsal, offRange, wei).block();
		}
		log.info("Drivers read from API {}", driversRead);
		log.info("Drivers with both tracks as FT: {}", both.size());
		log.info("Drivers with Buenos Aires track as FT: {}", buenosAires.size());
		log.info("Drivers with Laguna Seca track as FT: {}", lagunaSeca.size());
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.filter(logRequest())
				.filter(logResponse())
				.build();
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(request -> {
			log.info("REQUEST: {} {}", request.method(), request.url());
			request.headers().forEach((name, values) -> values.forEach(value -> log.info("{}: {}", name, value)));
			return Mono.just(request);
		});
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {

			return response.bodyToMono(String.class)
					.defaultIfEmpty("")
					.flatMap(body -> {
						log.info("RESPONSE STATUS: {}", response.statusCode());
						log.info("RESPONSE BODY: {}", body);

						// reconstruimos response porque el body ya fue consumido
						ClientResponse newResponse = ClientResponse.create(response.statusCode())
								.headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
								.body(body)
								.build();

						return Mono.just(newResponse);
					});
		});
	}

}
