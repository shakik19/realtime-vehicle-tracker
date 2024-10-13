package bd.shakik.realtime.tracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@EnableScheduling
public class DataPoller {
	private static final Logger logger = LoggerFactory.getLogger(DataPoller.class);
	
	@Value("${realtime.apiKey}")
	String apiKey;
	
	public byte[] downloadZipFile(URL url) throws URISyntaxException {
		logger.info("Downloading ZIP file from URL: {}", url);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url.toURI(), byte[].class);
	}
	
	public String extractXmlFromZip(byte[] zipData) throws IOException {
		logger.info("Extracting XML from ZIP file");
		File xmlFile = new File("siri.xml");
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().equals("siri.xml")) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(xmlFile)) {
						byte[] buffer = new byte[1024];
						int length;
						while ((length = zipInputStream.read(buffer)) > 0) {
							fileOutputStream.write(buffer, 0, length);
						}
						logger.info("Extracted XML to {}", xmlFile.getAbsolutePath());
						return xmlFile.getAbsolutePath();
					}
				}
			}
		}
		throw new IOException("siri.xml not found in the zip file");
	}
	
	public JsonNode xmlFileToJson(String xmlFilePath) throws IOException {
		logger.info("Converting XML file to JSON: {}", xmlFilePath);
		XmlMapper xmlMapper = new XmlMapper();
		try (InputStream xmlInputStream = new FileInputStream(xmlFilePath)) {
			return xmlMapper.readTree(xmlInputStream);
		}
	}
	
	//! Redundant: Delete Later
	public void writeJsonToFile(JsonNode jsonNode, String filePath) throws IOException {
		logger.info("Writing JSON to file: {}", filePath);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), jsonNode);
	}
	
	@Scheduled(cron = "*/15 * * * * *")
	public JsonNode getBusFeed() throws IOException, URISyntaxException {
		String url = "https://data.bus-data.dft.gov.uk/api/v1/datafeed/?api_key" + apiKey;
		URL positionURL = new URL(url);
		logger.info("Processing SIRI XML");
		
		final byte[] zipFile = downloadZipFile(positionURL);
		String xmlFilePath = extractXmlFromZip(zipFile);
		JsonNode json = xmlFileToJson(xmlFilePath);
		JsonNode vehicleActivities = json
						.path("ServiceDelivery")
						.path("VehicleMonitoringDelivery")
						.path("VehicleActivity");
		for (JsonNode node : vehicleActivities) {
			String s = node
							.path("MonitoredVehicleJourney")
							.path("VehicleLocation").toPrettyString();
			System.out.println(s);
		}
		
		String outputPath = "output/siri.json";
		File outputDir = new File("output");
		
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		//! Redundant: Delete Later
		writeJsonToFile(json, outputPath);
		deleteTemporaryFile(xmlFilePath);
		
		return json;
	}
	
	private void deleteTemporaryFile(String xmlFilePath) {
		File xmlFile = new File(xmlFilePath);
		if (xmlFile.exists() && xmlFile.delete()) {
			logger.info("Deleted temporary XML file: {}", xmlFilePath);
		} else {
			logger.warn("Failed to delete temporary XML file: {}", xmlFilePath);
		}
	}
}
