package br.com.chrosyn.tools.repmcp.adapter.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonClasspathLoader {

	private final ObjectMapper objectMapper;
	private final ResourceLoader resourceLoader;

	public JsonClasspathLoader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
		this.objectMapper = objectMapper;
		this.resourceLoader = resourceLoader;
	}

	public <T> List<T> loadList(String location, TypeReference<List<T>> typeReference) {
		Resource resource = resourceLoader.getResource(location);
		try (InputStream inputStream = resource.getInputStream()) {
			return objectMapper.readValue(inputStream, typeReference);
		}
		catch (IOException exception) {
			throw new IllegalStateException("Unable to read simulated API data from " + location, exception);
		}
	}

}
