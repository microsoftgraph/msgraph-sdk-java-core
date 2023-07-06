package com.microsoft.graph.tasks;

import com.microsoft.graph.BaseClient;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.testModels.*;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

public class PageIteratorTest {

    private PageIterator<TestEventItem, TestEventsResponse> pageIterator;
    final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(mock(AuthenticationProvider.class));
    BaseClient baseClient = new BaseClient(adapter);

    @Test
    void given_NonCollection_Parsable_Will_Throw_ArgumentException() {
        try {
            PageIterator<TestEventItem, TestEventItem> pageIterator = new PageIterator.BuilderWithSyncProcess<TestEventItem, TestEventItem>()
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
            pageIterator = new PageIterator.BuilderWithSyncProcess<TestEventItem, TestEventsResponse>()
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
    void given_Null_Async_Delegate_Will_Throw_NullPointerException() {
        try{
            pageIterator = new PageIterator.BuilderWithAsyncProcess<TestEventItem, TestEventsResponse>()
                .client(baseClient)
                .collectionPage(new TestEventsResponse())
                .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
                .asyncProcessPageItemCallback(null)
                .requestConfigurator(requestInformation -> requestInformation)
                .build();
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }
    @Test
    void given_Null_Delegate_Will_Throw_NullPointerException() {
        try{
            pageIterator = new PageIterator.BuilderWithSyncProcess<TestEventItem, TestEventsResponse>()
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
    void given_Concrete_Generated_Collection_Page_Will_Iterate_PageItems() throws ReflectiveOperationException, ServiceException {
        int inputEventCount = 17;

        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<TestEventItem>());
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }
        List<TestEventItem> testEventItems = new LinkedList<TestEventItem>();
        pageIterator = new PageIterator.BuilderWithSyncProcess<TestEventItem, TestEventsResponse>()
            .client(baseClient)
            .collectionPage(originalPage)
            .collectionPageFactory(TestEventsResponse::createFromDiscriminatorValue)
            .processPageItemCallback(item -> {
                testEventItems.add(item);
                return true; })
            .build();

        assertFalse(pageIterator.isProcessPageItemCallbackAsync);
        pageIterator.iterate().join();

        assertFalse(testEventItems.isEmpty());
        assertEquals(inputEventCount, testEventItems.size());
    }

    @Test
    void given_Concrete_Generated_CollectionPage_It_Stops_Iterating_PageItems() throws ReflectiveOperationException {
        int inputEventCount = 10;

        TestEventsResponse originalPage = new TestEventsResponse();
        originalPage.setValue(new LinkedList<TestEventItem>());
        for(int i = 0; i < inputEventCount; i++) {
            TestEventItem testEventItem = new TestEventItem();
            testEventItem.setSubject("Test Event: " + i);
            originalPage.getValue().add(testEventItem);
        }
        List<TestEventItem> testEventItems = new LinkedList<TestEventItem>();
        pageIterator = new PageIterator.BuilderWithSyncProcess<TestEventItem, TestEventsResponse>()
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



    }




}
