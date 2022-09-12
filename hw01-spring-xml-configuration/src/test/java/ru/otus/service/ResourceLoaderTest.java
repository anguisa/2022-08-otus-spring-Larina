package ru.otus.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Function;

import static org.mockito.Mockito.mock;

public class ResourceLoaderTest {

    private ResourceLoaderService resourceLoader;

    @BeforeEach
    public void beforeEach() {
        resourceLoader = mock(ResourceLoaderService.class);
    }

    @Test
    public void shouldParseCorrectData() {
        List<Set<String>> expectedList = List.of(Set.of("abc1", "bcd1", "def1"), Set.of("abc2", "bcd2", "def2", "efg2"));
        String[] line1 = new String[expectedList.get(0).size()];
        expectedList.get(0).toArray(line1);
        String[] line2 = new String[expectedList.get(1).size()];
        expectedList.get(1).toArray(line2);
        Mockito.doAnswer(invocation -> {
            Function<String[], Set<String>> parseLineToObject = invocation.getArgument(2);
            List<Set<String>> parsedData = invocation.getArgument(3);
            parsedData.add(parseLineToObject.apply(line1));
            parsedData.add(parseLineToObject.apply(line2));
            return null;
        }).when(resourceLoader).loadData(Mockito.anyString(), Mockito.anyChar(), Mockito.any(), Mockito.any());
        List<Set<String>> actualList = new ArrayList<>();
        resourceLoader.loadData("test", '.', line -> new HashSet<>(Arrays.asList(line)), actualList);
        Assertions.assertIterableEquals(expectedList, actualList, "Wrong parsed data");
    }
}
