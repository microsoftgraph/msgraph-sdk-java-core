package com.microsoft.graph.core.tasks;

import com.microsoft.graph.core.CoreConstants;
import com.microsoft.graph.core.requests.IBaseClient;
import com.microsoft.kiota.*;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A class for iterating through pages of a collection
 * Uses to automatically pages through result sets across multiple calls and process each item in the result set.
 * @param <TEntity> The type of the entity returned in the collection. This type must implement {@link Parsable}
 * @param <TCollectionPage> The Microsoft Graph collection response type returned in the collection response. This type must implement {@link Parsable} and {@link AdditionalDataHolder}
 */
public class PageIterator<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> {
    /**
     * Creates a new instance of the PageIterator class
     */
    protected PageIterator() {
        // default constructor
    }
    private static final String NO_COLLECTION_PROPERTY_ERROR = "The Parsable does not contain a collection property.";
    private RequestAdapter requestAdapter;
    private TCollectionPage currentPage;
    private ParsableFactory<TCollectionPage> collectionPageFactory;
    private Queue<TEntity> pageItemQueue;
    private Function<TEntity, Boolean> processPageItemCallback;
    private UnaryOperator<RequestInformation> requestConfigurator;


    private String deltaLink;

    /**
     * The deltaLink returned from a delta query.
     * @return the deltaLink from the delta query
     */
    @Nullable
    public String getDeltaLink() {
        return deltaLink;
    }
    private String nextLink;
    /**
     * The nextLink returned from a collection query.
     * @return the nextLink from the collection query
     */
    @Nullable
    public String getNextLink() {
        return nextLink;
    }
    private PageIteratorState state = PageIteratorState.NOT_STARTED;
    /**
     * The state of the page iterator
     * @return the state of the page iterator
     */
    @Nonnull
    public PageIteratorState getPageIteratorState() {
        return state;
    }
    /**
     * Sets the request adapter to use for requests in the page iterator.
     * @param requestAdapter the request adapter to use for requests.
     */
    protected void setRequestAdapter(@Nonnull RequestAdapter requestAdapter) {
        this.requestAdapter = Objects.requireNonNull(requestAdapter);
    }
    /**
     * The factory to use for creating the collection page instance.
     * @param collectionPageFactory the factory to use for creating the collection page.
     */
    protected void setCollectionPageFactory(@Nonnull ParsableFactory<TCollectionPage> collectionPageFactory) {
        this.collectionPageFactory = Objects.requireNonNull(collectionPageFactory);
    }
    /**
     * The request configurator to use for requests in the page iterator.
     * @param requestConfigurator the request configurator to use when modifying requests.
     */
    protected void setRequestConfigurator(@Nullable UnaryOperator<RequestInformation> requestConfigurator) {
        this.requestConfigurator = requestConfigurator;
    }
    /**
     * The current page of the collection.
     * @param currentPage the current page of the collection.
     */
    protected void setCurrentPage(@Nonnull TCollectionPage currentPage) {
        this.currentPage = Objects.requireNonNull(currentPage);
    }
    /**
     * The processPageItemCallback to use for processing each item in the collection.
     * @param processPageItemCallback the processPageItemCallback to use for processing each item in the collection.
     */
    protected void setProcessPageItemCallback(@Nonnull Function<TEntity, Boolean> processPageItemCallback) {
        this.processPageItemCallback = Objects.requireNonNull(processPageItemCallback);
    }
    /**
     * The queue of items in the current page.
     * @param pageItemQueue the queue of items in the current page.
     */
    protected void setPageItemQueue(@Nonnull Queue<TEntity> pageItemQueue) {
        this.pageItemQueue = Objects.requireNonNull(pageItemQueue);
    }
    /**
     * A builder class for building a PageIterator.
     * @param <TEntity> The type of the entity returned in the collection. This type must implement {@link Parsable}
     * @param <TCollectionPage> The Microsoft Graph collection response type returned in the collection response. This type must implement {@link Parsable} and {@link AdditionalDataHolder}
     */
    public static class Builder<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> implements PageIteratorBuilder<TEntity, TCollectionPage>{
        /**
         * Constructor for the Builder class of a PageIterator.
         */
        public Builder() {
            // Default constructor
        }
        private RequestAdapter requestAdapter;
        private TCollectionPage currentPage;
        private ParsableFactory<TCollectionPage> collectionPageFactory;
        private UnaryOperator<RequestInformation> requestConfigurator;
        private Function<TEntity, Boolean> processPageItemCallback;
        private RequestAdapter getRequestAdapter() {
            return this.requestAdapter;
        }
        private TCollectionPage getCollectionPage() {
            return this.currentPage;
        }
        private ParsableFactory<TCollectionPage> getCollectionPageFactory() {
            return this.collectionPageFactory;
        }

        private UnaryOperator<RequestInformation> getRequestConfigurator() {
            return this.requestConfigurator;
        }
        private Function<TEntity, Boolean> getProcessPageItemCallback() {
            return this.processPageItemCallback;
        }

        @Override
        @Nonnull
        public Builder<TEntity, TCollectionPage> client(@Nonnull IBaseClient client) {
            Objects.requireNonNull(client);
            return this.requestAdapter(client.getRequestAdapter());
        }
        @Override
        @Nonnull
        public Builder<TEntity, TCollectionPage> requestAdapter(@Nonnull RequestAdapter requestAdapter) {
            this.requestAdapter = Objects.requireNonNull(requestAdapter);
            return this;
        }
        @Override
        @Nonnull
        public Builder<TEntity, TCollectionPage> collectionPage(@Nonnull TCollectionPage collectionPage) {
            this.currentPage = Objects.requireNonNull(collectionPage);
            return this;
        }
        @Override
        @Nonnull
        public Builder<TEntity, TCollectionPage> collectionPageFactory(@Nonnull ParsableFactory<TCollectionPage> collectionPageFactory) {
            this.collectionPageFactory = Objects.requireNonNull(collectionPageFactory);
            return this;
        }
        @Override
        @Nonnull
        public Builder<TEntity, TCollectionPage> requestConfigurator(@Nonnull UnaryOperator<RequestInformation> requestConfigurator) {
            this.requestConfigurator = Objects.requireNonNull(requestConfigurator);
            return this;
        }
        /**
         * Sets the callback to be called for each item in the collection.
         * @param processPageItemCallback the callback to be called for each item in the collection.
         * @return the builder object itself
         */
        @Nonnull
        public Builder<TEntity, TCollectionPage> processPageItemCallback(@Nonnull Function<TEntity, Boolean> processPageItemCallback) {
            this.processPageItemCallback = Objects.requireNonNull(processPageItemCallback);
            return this;
        }
        /**
         * Builds the PageIterator object.
         * Will fail if request adapter is not set.
         * Will fail if current collection page is not set.
         * Will fail if collection page factory is not set.
         * Will fail if process page item callback is not set.
         */
        @Nonnull
        private PageIterator<TEntity, TCollectionPage> build(@Nonnull PageIterator<TEntity, TCollectionPage> instance) throws InvocationTargetException, IllegalAccessException {
            Objects.requireNonNull(instance);
            if(!this.currentPage.getFieldDeserializers().containsKey("value")) {
                throw new IllegalArgumentException(NO_COLLECTION_PROPERTY_ERROR);
            }
            instance.setRequestAdapter(Objects.requireNonNull(this.getRequestAdapter()));
            instance.setCurrentPage(Objects.requireNonNull(this.getCollectionPage()));
            instance.setCollectionPageFactory(Objects.requireNonNull(this.getCollectionPageFactory()));
            instance.setRequestConfigurator(this.getRequestConfigurator());
            instance.setProcessPageItemCallback(Objects.requireNonNull(this.getProcessPageItemCallback()));

            Queue<TEntity> currentCollection = new LinkedList<>(extractEntityListFromParsable(this.getCollectionPage()));
            instance.setPageItemQueue(currentCollection);
            return instance;
        }
        @Override
        @Nonnull
        public PageIterator<TEntity, TCollectionPage> build() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            return this.build(new PageIterator<>());
        }
    }
    private boolean intrapageIterate() throws ReflectiveOperationException {
        this.state = PageIteratorState.INTRAPAGE_ITERATION;
        while (!this.pageItemQueue.isEmpty()) {
            boolean shouldContinue= this.processPageItemCallback.apply(this.pageItemQueue.remove());
            if (!shouldContinue) {
                this.state = PageIteratorState.PAUSED;
                return false;
            }
        }

        String extractedNextLink = extractNextLinkFromParsable(this.currentPage, null);
        if (!Compatibility.isBlank(extractedNextLink)){
            this.nextLink = extractedNextLink;
            this.deltaLink = "";
            return true;
        }

        String extractedDeltaLink = extractNextLinkFromParsable(this.currentPage, CoreConstants.CollectionResponseMethods.GET_ODATA_DELTA_LINK);
        if (!Compatibility.isBlank(extractedDeltaLink)){
            this.deltaLink = extractedDeltaLink;
            this.state = PageIteratorState.DELTA;
        } else {
            this.state = PageIteratorState.COMPLETE;
        }
        this.nextLink = "";
        return false;
    }
    private void interpageIterate() throws ReflectiveOperationException {
        this.state = PageIteratorState.INTERPAGE_ITERATION;

        if(!Compatibility.isBlank(nextLink) || !Compatibility.isBlank(deltaLink)) {
            RequestInformation nextPageRequestInformation = new RequestInformation();
            nextPageRequestInformation.httpMethod = HttpMethod.GET;
            nextPageRequestInformation.urlTemplate = Compatibility.isBlank(nextLink) ? deltaLink : nextLink;

            nextPageRequestInformation = requestConfigurator == null ? nextPageRequestInformation : requestConfigurator.apply(nextPageRequestInformation);
            this.currentPage = Objects.requireNonNull(this.requestAdapter.send(nextPageRequestInformation, null, this.collectionPageFactory));
            List<TEntity> pageItems = extractEntityListFromParsable(this.currentPage);
            if(!pageItems.isEmpty()) {
                this.pageItemQueue.addAll(pageItems);
            }
        }
        if(!Compatibility.isBlank(nextLink) && this.nextLink.equals(extractNextLinkFromParsable(this.currentPage, null))) {
            throw new ApiException("Detected a nextLink loop. NextLink value: " + this.nextLink);
        }
    }

    /**
     * Iterates over the collection of entities in the collation page.
     * Will continues to iterate over the collection of entities in the next page, if there is a next page.
     * @throws ApiException if the request was unable to complete for any reason.
     * @throws ReflectiveOperationException if the entity or collection page could not be instantiated or if they are of invalid types.
     */
    public void iterate() throws ApiException, ReflectiveOperationException {
        if(this.state == PageIteratorState.DELTA) {
            interpageIterate();
        }
        boolean shouldContinueInterpageIteration = intrapageIterate();
        while (shouldContinueInterpageIteration) {
            interpageIterate();
            shouldContinueInterpageIteration = intrapageIterate();
        }
    }

    /**
     * Resumes the iteration over the collection of entities in the collation page.
     * @throws ApiException if the request was unable to complete for any reason.
     * @throws ReflectiveOperationException if the entity or collection page could not be instantiated or if they are of invalid types.
     */
    public void resume() throws ApiException, ReflectiveOperationException {
        iterate();
    }

    /**
     * Extracts the list of entities from the Parsable collection page.
     * @param parsableCollection the Parsable collection page.
     * @return the list of entities.
     * @param <TEntity> the type of the entity.
     * @param <TCollectionPage> the type of the collection page.
     * @throws IllegalAccessException if the Parsable does not contain a collection property.
     * @throws InvocationTargetException if the Parsable does not contain a collection property.
     */
    @Nonnull
    protected static <TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> List<TEntity> extractEntityListFromParsable(@Nonnull TCollectionPage parsableCollection) throws  IllegalAccessException, InvocationTargetException {
        try{
            return (List<TEntity>) parsableCollection.getClass().getDeclaredMethod("getValue").invoke(parsableCollection);
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("NO_COLLECTION_PROPERTY_ERROR");
        }
    }
    private static <TCollectionPage extends Parsable & AdditionalDataHolder> String extractNextLinkFromParsable(@Nonnull TCollectionPage parsableCollection, @Nullable String getNextLinkMethodName) throws ReflectiveOperationException {
        String methodName = getNextLinkMethodName == null ? CoreConstants.CollectionResponseMethods.GET_ODATA_NEXT_LINK : getNextLinkMethodName;
        Method[] methods = parsableCollection.getClass().getDeclaredMethods();
        String nextLink;
        if(Arrays.stream(methods).anyMatch(m -> m.getName().equals(methodName))) {
            try {
                nextLink = (String) parsableCollection.getClass().getDeclaredMethod(methodName).invoke(parsableCollection);
                if(!Compatibility.isBlank(nextLink)) {
                    return nextLink;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new ReflectiveOperationException("Could not extract nextLink from parsableCollection.");
            }
        }
        nextLink = (String) parsableCollection.getAdditionalData().get(CoreConstants.OdataInstanceAnnotations.NEXT_LINK);
        return nextLink == null ? "" : nextLink;
    }

    /**
     * Enum to represent the possible states of the PageIterator.
     */
    public enum PageIteratorState {
        /** The PageIterator has not started iterating. */
        NOT_STARTED,
        /** The PageIterator is currently paused. A callback returned false. Iterator can be resumed. */
        PAUSED,
        /** The PageIterator is currently iterating over paged requests. */
        INTERPAGE_ITERATION,
        /** The PageIterator is currently iterating over the contents of a page. */
        INTRAPAGE_ITERATION,
        /** A deltaToken was returned, the iterator is can be resumed. */
        DELTA,
        /** The PageIterator has completed iterating. */
        COMPLETE
    }
}
