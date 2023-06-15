package com.microsoft.graph.tasks;

import com.microsoft.graph.requests.BaseGraphRequestAdapter;
import com.microsoft.graph.testModels.*;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;

public class PageIteratorTest {

    private PageIterator<TestEventItem, TestEventsResponse> pageIterator;

    @Test
    void createPageIterator() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PageIterator<TestDriveItem, TestDrive> pageIterator = new PageIterator.BuilderWithAsyncProcess<TestDriveItem, TestDrive>()
            .collectionPage(new TestDrive())
            .requestAdapter(new BaseGraphRequestAdapter(mock(AnonymousAuthenticationProvider.class)))
            .collectionPageFactory(TestDrive::createFromDiscriminatorValue)
            .asyncProcessPageItemCallback(item -> CompletableFuture.completedFuture(true))
            .requestConfigurator(requestInformation -> requestInformation)
            .build();
    }
    @Test
    void givenNonCollectionParsableItThrowsArgumentException() {
        TestEvent testEvent = new TestEvent();

    }


}
