package com.microsoft.graph.core.tasks;

import com.microsoft.graph.core.BaseClient;
import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.testModels.TestEventItem;
import com.microsoft.graph.core.testModels.TestEventsDeltaResponse;
import com.microsoft.graph.core.testModels.TestEventsResponse;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PageIteratorTest {

    private PageIterator<TestEventItem, TestEventsResponse> pageIterator;
    final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(mock(AuthenticationProvider.class));
    BaseClient baseClient = new BaseClient(adapter);

    @Test
    void given_NonCollection_Parsable_Will_Throw_ArgumentException() {
        try {
            new PageIterator.Builder<TestEventItem, TestEventItem>()
                .client(baseClient)
                .collectionPage(new TestEventItem())
                .collectionPageFactory(TestEventItem::createFromDiscriminatorValue)
                .processPageItemCallback(item -> true)
                .requestConfigurator(requestInformation -> requestInformation)
                .build();
        } catch (Exception e) {
            assertEquals("The Parsable does not contain a collection property.", e.getMessage());
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    @Test
    void given_Null_Collection_Page_Will_Throw_NullPointerException() {
        try {
            pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
                .client(baseClient)
                .collectionPage(null)
                .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
                .processPageItemCallback(item -> true)
                .requestConfigurator(requestInformation -> requestInformation)
                .build();
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }
    @Test
    void given_Null_Delegate_Will_Throw_NullPointerException() {
        try{
            pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
                .client(baseClient)
                .collectionPage(new TestEventsResponse())
                .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
                .processPageItemCallback(null)
                .requestConfigurator(requestInformation -> requestInformation)
                .build();
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }
    @Test
    void given_Concrete_Generated_Collection_Page_Will_Iterate_PageItems() throws ReflectiveOperationException, ApiException {
        int inputEventCount = 17;

        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<TestEventItem>());
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }
        List<TestEventItem> testEventItems = new LinkedList<TestEventItem>();
        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .client(baseClient)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(item -> {
                testEventItems.add(item);
                return true; })
            .build();

        pageIterator.iterate();

        assertFalse(testEventItems.isEmpty());
        assertEquals(inputEventCount, testEventItems.size());
    }

    @Test
    void given_Concrete_Generated_CollectionPage_It_Stops_Iterating_PageItems() throws ReflectiveOperationException, ApiException {
        int inputEventCount = 10;

        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<TestEventItem>());
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }
        List<TestEventItem> testEventItems = new LinkedList<TestEventItem>();
        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .client(baseClient)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(item -> {
                if(item.getSubject().equals("Test Event: 7")) {
                    return false;
                }
                testEventItems.add(item);
                return true;
            }).build();
        pageIterator.iterate();

        assertEquals(7, testEventItems.size());
        assertEquals(PageIterator.PageIteratorState.PAUSED, pageIterator.getPageIteratorState());
    }
    @Test
    void given_CollectionPage_Without_NextLink_Property_It_Iterates_Across_Pages() throws ReflectiveOperationException, ApiException {
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        HashMap<String, Object> additionalData = new HashMap<>();
        additionalData.put(CoreConstants.OdataInstanceAnnotations.NEXT_LINK, "http://localhost/events?$skip=11");
        originalPage.setAdditionalData(additionalData);

        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        TestEventsResponse secondPage = new TestEventsResponse();
        secondPage.setValue(new LinkedList<TestEventItem>());
        int secondPageEventCount = 5;
        for(int i = 0; i < secondPageEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Second Page Test Event: " + i);
            secondPage.getValue().add(testEventItem);
        }

        AtomicInteger totalItemsProcessed = new AtomicInteger(0);

        Function<TestEventItem, Boolean> processPageItemCallback = item -> {
            totalItemsProcessed.incrementAndGet();
            return true;
        };

        MockAdapter mockAdapter = new MockAdapter(mock(AuthenticationProvider.class), secondPage);
        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .requestAdapter(mockAdapter)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(processPageItemCallback)
            .build();

        pageIterator.iterate();

        assertEquals(PageIterator.PageIteratorState.COMPLETE, pageIterator.getPageIteratorState());
        assertEquals("", pageIterator.getNextLink());
        assertEquals(inputEventCount + secondPageEventCount, totalItemsProcessed.get());
    }
    @Test
    void given_CollectionPage_Delta_Link_Property_It_Iterates_Across_Pages() throws ReflectiveOperationException, ApiException {
        TestEventsDeltaResponse originalPage = new TestEventsDeltaResponse();
        originalPage.setValue(new LinkedList<>());
        originalPage.setOdataDeltaLink("http://localhost/events?$skip=11");
        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        Function<TestEventItem, Boolean> processPageItemCallback = item -> true;

        PageIterator<TestEventItem, TestEventsDeltaResponse> pageIterator = new PageIterator.Builder<TestEventItem, TestEventsDeltaResponse>()
            .client(baseClient)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsDeltaResponse::createFromDiscriminatorValue)
            .processPageItemCallback(processPageItemCallback)
            .build();

        pageIterator.iterate();

        assertEquals(PageIterator.PageIteratorState.DELTA, pageIterator.getPageIteratorState());
        assertEquals("http://localhost/events?$skip=11", pageIterator.getDeltaLink());
    }

    @Test
    void given_CollectionPage_It_Iterates_Across_Pages() throws ReflectiveOperationException, ApiException{
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        originalPage.setOdataNextLink("http://localhost/events?$skip=11");
        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        TestEventsResponse secondPage = new TestEventsResponse();
        secondPage.setValue(new LinkedList<>());
        int secondPageEventCount = 5;
        for(int i = 0; i < secondPageEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Second Page Test Event: " + i);
            secondPage.getValue().add(testEventItem);
        }

        boolean[] reachedNextPage = {false};

        Function<TestEventItem, Boolean> processPageItemCallback = item -> {
            if(item.getSubject().contains("Second Page Test Event")) {

                reachedNextPage[0] = true;
                return false;
            }
            return true;
        };

        MockAdapter mockAdapter = new MockAdapter(mock(AuthenticationProvider.class), secondPage);

        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .requestAdapter(mockAdapter)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(processPageItemCallback)
            .build();

        pageIterator.iterate();

        assertTrue(reachedNextPage[0]);
        assertEquals(PageIterator.PageIteratorState.PAUSED, pageIterator.getPageIteratorState());
    }
    @Test
    void given_CollectionPage_It_Detects_Next_Link_Loop() throws ReflectiveOperationException {
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        originalPage.setOdataNextLink("http://localhost/events?$skip=11");
        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        TestEventsResponse secondPage = new TestEventsResponse();
        secondPage.setValue(new LinkedList<>());
        secondPage.setOdataNextLink("http://localhost/events?$skip=11");
        int secondPageEventCount = 5;
        for(int i = 0; i < secondPageEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Second Page Test Event: " + i);
            secondPage.getValue().add(testEventItem);
        }

        Function<TestEventItem, Boolean> processPageItemCallback = item -> true;

        MockAdapter mockAdapter = new MockAdapter(mock(AuthenticationProvider.class), secondPage);

        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .requestAdapter(mockAdapter)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(processPageItemCallback)
            .build();

        try{
            pageIterator.iterate();
        } catch (Exception e) {
            assertEquals(ApiException.class, e.getClass());
            assertTrue(e.getMessage().contains("Detected a nextLink loop. NextLink value:"));
        }
    }
    @Test
    void given_CollectionPage_It_Handles_Empty_NextPage() throws ReflectiveOperationException {
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        originalPage.setOdataNextLink("http://localhost/events?$skip=11");
        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        TestEventsResponse secondPage = new TestEventsResponse();
        secondPage.setValue(new LinkedList<>());

        Function<TestEventItem, Boolean> processPageItemCallback = item -> true;

        MockAdapter mockAdapter = new MockAdapter(mock(AuthenticationProvider.class), secondPage);

        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .requestAdapter(mockAdapter)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(processPageItemCallback)
            .build();

        try{
            pageIterator.iterate();
        } catch (ApiException e) {
            fail("Should not throw exception");
        }
    }
    @Test
    void given_PageIterator_It_Has_NotStarted_PagingState() throws ReflectiveOperationException {
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .client(baseClient)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(item-> true)
            .build();
        assertEquals(PageIterator.PageIteratorState.NOT_STARTED, pageIterator.getPageIteratorState());
    }
    @Test
    void given_RequestConfigurator_It_Is_Invoked() throws ReflectiveOperationException, ApiException {
        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<>());
        originalPage.setOdataNextLink("http://localhost/events?$skip=11");
        int inputEventCount = 17;
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }

        TestEventsResponse secondPage = new TestEventsResponse();
        secondPage.setValue(new LinkedList<>());
        int secondPageEventCount = 5;
        for(int i = 0; i < secondPageEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Second Page Test Event: " + i);
            secondPage.getValue().add(testEventItem);
        }

        boolean[] requestConfiguratorInvoked = {false};

        UnaryOperator<RequestInformation> requestConfigurator = request -> {
            requestConfiguratorInvoked[0] = true;
            return request;
        };

        MockAdapter mockAdapter = new MockAdapter(mock(AuthenticationProvider.class), secondPage);

        pageIterator = new PageIterator.Builder<TestEventItem, TestEventsResponse>()
            .requestAdapter(mockAdapter)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(item -> true)
            .requestConfigurator(requestConfigurator)
            .build();

        pageIterator.iterate();

        assertTrue(requestConfiguratorInvoked[0]);
    }
}
    class MockAdapter extends OkHttpRequestAdapter {
        Object mockResponse;
        public MockAdapter(@Nonnull AuthenticationProvider authenticationProvider, Object response) {
            super(authenticationProvider);
            mockResponse = response;
        }

        public <T extends Parsable> T send(@Nonnull RequestInformation request, @Nullable final HashMap<String, ParsableFactory<? extends Parsable>> errorMappings, @Nonnull ParsableFactory<T> parsableFactory) {
            return (T) this.mockResponse;
        }
    }
