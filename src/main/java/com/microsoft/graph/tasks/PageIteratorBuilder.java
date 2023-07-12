package com.microsoft.graph.tasks;

import com.microsoft.graph.requests.IBaseClient;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.function.UnaryOperator;

interface PageIteratorBuilder<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> {
    /**
     * Sets the client for the PageIteratorBuilder.
     * @param client the client to set.
     */
    public PageIteratorBuilder<TEntity, TCollectionPage> client(@Nonnull IBaseClient client);
    /**
     * Sets the request adapter for the PageIteratorBuilder.
     * @param requestAdapter the request adapter to set.
     */
    public PageIteratorBuilder<TEntity, TCollectionPage> requestAdapter(@Nonnull RequestAdapter requestAdapter);
    /**
     * Sets the page to be iterated over.
     * @param collectionPage the page to be iterated over.
     */
    public PageIteratorBuilder<TEntity, TCollectionPage> collectionPage(@Nonnull TCollectionPage collectionPage) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
    /**
     * Sets factory to use for creating a collection page.
     * @param collectionPageFactory the factory to use for creating a collection page.
     */
    public PageIteratorBuilder<TEntity, TCollectionPage> collectionPageFactory(@Nonnull ParsableFactory<TCollectionPage> collectionPageFactory);
    /**
     * Sets the function to configure each subsequent request.
     * @param requestConfigurator function to configure each subsequent request.
     */
    public PageIteratorBuilder<TEntity, TCollectionPage> requestConfigurator(@Nonnull UnaryOperator<RequestInformation> requestConfigurator);
    /**
     * Build the PageIterator.
     * Should fail if request adapter is not set.
     * Should fail if current collection page is not set.
     * Should fail if collection page factory is not set.
     * Should fail if process page item callback is not set.
     * @return the built PageIterator.
     */
    PageIterator<TEntity, TCollectionPage> build() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;

}
