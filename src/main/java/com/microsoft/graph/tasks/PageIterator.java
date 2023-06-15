package com.microsoft.graph.tasks;

import com.microsoft.graph.CoreConstants;
import com.microsoft.graph.exceptions.ServiceException;
import com.microsoft.graph.requests.IBaseClient;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class PageIterator<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> {

    protected PageIterator() {
    }


    private RequestAdapter requestAdapter;
    private TCollectionPage currentPage;
    private ParsableFactory<TCollectionPage> collectionPageFactory;
    private Queue<TEntity> pageItemQueue;
    private Function<TEntity, Boolean> processPageItemCallback;
    private Function<TEntity, CompletableFuture<Boolean>> asyncProcessPageItemCallback;
    private UnaryOperator<RequestInformation> requestConfigurator;


    private String deltaLink;

    public String getDeltaLink() {
        return deltaLink;
    }

    private String nextLink;
    public String getNextLink() {
        return nextLink;
    }
    private PageIteratorState state;
    public PageIteratorState getPageIteratorState() {
        return state;
    }
    public void setPageIteratorState(PageIteratorState state) {
        this.state = state;
    }
    protected boolean isProcessPageItemCallbackAsync;

    protected void setRequestAdapter(RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    protected void setCollectionPageFactory(ParsableFactory<TCollectionPage> collectionPageFactory) {
        this.collectionPageFactory = collectionPageFactory;
    }
    protected void setRequestConfigurator(UnaryOperator<RequestInformation> requestConfigurator) {
        this.requestConfigurator = requestConfigurator;
    }
    protected void setCurrentPage(TCollectionPage currentPage) {
        this.currentPage = currentPage;
    }
    protected void setProcessPageItemCallback(Function<TEntity, Boolean> processPageItemCallback) {
        this.processPageItemCallback = processPageItemCallback;
        isProcessPageItemCallbackAsync = false;
    }
    protected void setAsyncProcessPageItemCallback(Function<TEntity, CompletableFuture<Boolean>> asyncProcessPageItemCallback) {
        this.asyncProcessPageItemCallback = asyncProcessPageItemCallback;
        isProcessPageItemCallbackAsync = true;
    }
    protected void setPageItemQueue(Queue<TEntity> pageItemQueue) {
        this.pageItemQueue = pageItemQueue;
    }

    public static class BuilderWithAsyncProcess<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> implements PageIteratorBuilder<TEntity, TCollectionPage>{
        private RequestAdapter requestAdapter;
        private TCollectionPage currentPage;
        private ParsableFactory<TCollectionPage> collectionPageFactory;
        private UnaryOperator<RequestInformation> requestConfigurator;
        private Function<TEntity, CompletableFuture<Boolean>> asyncProcessPageItemCallback;

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
        private Function<TEntity, CompletableFuture<Boolean>> getAsyncProcessPageItemCallback() {
            return this.asyncProcessPageItemCallback;
        }
        @Override
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> client(@Nonnull IBaseClient client) {
            requestAdapter(client.getRequestAdapter());
            return this;
        }
        @Override
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> requestAdapter(@Nonnull RequestAdapter requestAdapter) {
            this.requestAdapter = Objects.requireNonNull(requestAdapter);
            return this;
        }
        @Override
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> collectionPage(@Nonnull TCollectionPage collectionPage) {
            this.currentPage = Objects.requireNonNull(collectionPage);
            return this;
        }
        @Override
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> collectionPageFactory(@Nonnull ParsableFactory<TCollectionPage> collectionPageFactory) {
            this.collectionPageFactory = Objects.requireNonNull(collectionPageFactory);
            return this;
        }
        @Override
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> requestConfigurator(@Nonnull UnaryOperator<RequestInformation> requestConfigurator) {
            this.requestConfigurator = Objects.requireNonNull(requestConfigurator);
            return this;
        }
        public BuilderWithAsyncProcess<TEntity, TCollectionPage> asyncProcessPageItemCallback(@Nonnull Function<TEntity, CompletableFuture<Boolean>> asyncProcessPageItemCallback) {
            this.asyncProcessPageItemCallback = Objects.requireNonNull(asyncProcessPageItemCallback);
            return this;
        }
        private PageIterator<TEntity, TCollectionPage> build(@Nonnull PageIterator<TEntity, TCollectionPage> instance) throws InvocationTargetException, IllegalAccessException {
            Objects.requireNonNull(instance);
            if(!this.currentPage.getFieldDeserializers().containsKey("value")) {
                throw new IllegalArgumentException("The collection page must contain a value field");
            }
            instance.setRequestAdapter(Objects.requireNonNull(this.getRequestAdapter()));
            instance.setCurrentPage(Objects.requireNonNull(this.getCollectionPage()));
            instance.setCollectionPageFactory(Objects.requireNonNull(this.getCollectionPageFactory()));
            instance.setRequestConfigurator(this.getRequestConfigurator());
            instance.setAsyncProcessPageItemCallback(Objects.requireNonNull(this.getAsyncProcessPageItemCallback()));
            instance.setPageIteratorState(PageIteratorState.NOT_STARTED);

            Queue<TEntity> currentCollection = new LinkedList<>(extractEntityListFromParsable(this.getCollectionPage()));
            instance.setPageItemQueue(currentCollection);
            return instance;
        }
        @Override
        public PageIterator<TEntity, TCollectionPage> build() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            return this.build(new PageIterator<>());
        }
    }

    public static class BuilderWithSyncProcess<TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> implements PageIteratorBuilder<TEntity, TCollectionPage>{
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
        public BuilderWithSyncProcess<TEntity, TCollectionPage> client(@Nonnull IBaseClient client) {
            this.requestAdapter = Objects.requireNonNull(client).getRequestAdapter();
            return this;
        }

        @Override
        public BuilderWithSyncProcess<TEntity, TCollectionPage> requestAdapter(@Nonnull RequestAdapter requestAdapter) {
            this.requestAdapter = Objects.requireNonNull(requestAdapter);
            return this;
        }
        @Override
        public BuilderWithSyncProcess<TEntity, TCollectionPage> collectionPage(@Nonnull TCollectionPage collectionPage) {
            this.currentPage = Objects.requireNonNull(collectionPage);
            return this;
        }
        @Override
        public BuilderWithSyncProcess<TEntity, TCollectionPage> collectionPageFactory(@Nonnull ParsableFactory<TCollectionPage> collectionPageFactory) {
            this.collectionPageFactory = Objects.requireNonNull(collectionPageFactory);
            return this;
        }
        @Override
        public BuilderWithSyncProcess<TEntity, TCollectionPage> requestConfigurator(@Nonnull UnaryOperator<RequestInformation> requestConfigurator) {
            this.requestConfigurator = Objects.requireNonNull(requestConfigurator);
            return this;
        }
        public BuilderWithSyncProcess<TEntity, TCollectionPage> processPageItemCallback(@Nonnull Function<TEntity, Boolean> processPageItemCallback) {
            this.processPageItemCallback = Objects.requireNonNull(processPageItemCallback);
            return this;
        }
        private PageIterator<TEntity, TCollectionPage> build(@Nonnull PageIterator<TEntity, TCollectionPage> instance) throws InvocationTargetException, IllegalAccessException {
            Objects.requireNonNull(instance);
            if(!this.currentPage.getFieldDeserializers().containsKey("value")) {
                throw new IllegalArgumentException("The collection page must contain a value field");
            }
            instance.setRequestAdapter(Objects.requireNonNull(this.getRequestAdapter()));
            instance.setCurrentPage(Objects.requireNonNull(this.getCollectionPage()));
            instance.setCollectionPageFactory(Objects.requireNonNull(this.getCollectionPageFactory()));
            instance.setRequestConfigurator(this.getRequestConfigurator());
            instance.setProcessPageItemCallback(Objects.requireNonNull(this.getProcessPageItemCallback()));
            instance.setPageIteratorState(PageIteratorState.NOT_STARTED);

            Queue<TEntity> currentCollection = new LinkedList<>(extractEntityListFromParsable(this.getCollectionPage()));
            instance.setPageItemQueue(currentCollection);
            return instance;
        }
        @Override
        public PageIterator<TEntity, TCollectionPage> build() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            return this.build(new PageIterator<>());
        }
    }
    private CompletableFuture<Boolean> intrapageIterate() throws InvocationTargetException, IllegalAccessException {
        this.state = PageIteratorState.INTRAPAGE_ITERATION;
        while (!this.pageItemQueue.isEmpty()) {
            boolean shouldContinue;
            if (isProcessPageItemCallbackAsync) {
                shouldContinue = this.asyncProcessPageItemCallback.apply(this.pageItemQueue.remove()).join();
            } else {
                shouldContinue = this.processPageItemCallback.apply(this.pageItemQueue.remove());
            }
            if (!shouldContinue) {
                this.state = PageIteratorState.PAUSED;
                return CompletableFuture.completedFuture(false);
            }
        }

        String extractedNextLink = extractNextLinkFromParsable(this.currentPage, null);
        if (!nextLink.isEmpty()) {
            this.nextLink = extractedNextLink;
            deltaLink = "";
            return CompletableFuture.completedFuture(true);
        }

        if (currentPage.getAdditionalData().containsKey(CoreConstants.OdataInstanceAnnotations.DELTA_LINK)) {
            this.deltaLink = (String) currentPage.getAdditionalData().get(CoreConstants.OdataInstanceAnnotations.DELTA_LINK);
            this.state = PageIteratorState.DELTA;
            this.nextLink = "";
            return CompletableFuture.completedFuture(true);
        }

        String extractedDeltaLink = extractNextLinkFromParsable(this.currentPage, CoreConstants.CollectionResponseMethods.GET_ODATA_DELTA_LINK);
        if (!extractedDeltaLink.isEmpty()) {
            this.deltaLink = extractedDeltaLink;
            this.state = PageIteratorState.DELTA;
            this.nextLink = "";
            return CompletableFuture.completedFuture(false);
        } else {
            this.state = PageIteratorState.COMPLETE;
            this.nextLink = "";
            this.deltaLink = "";
            return CompletableFuture.completedFuture(false);
        }
    }
    private CompletableFuture<Void> interpageIterate() throws InvocationTargetException, IllegalAccessException, ServiceException {
        this.state = PageIteratorState.INTERPAGE_ITERATION;
        if(!this.nextLink.isEmpty() || !this.deltaLink.isEmpty()) {
            RequestInformation nextPageRequestInformation = new RequestInformation();
            nextPageRequestInformation.httpMethod = HttpMethod.GET;
            nextPageRequestInformation.urlTemplate = this.nextLink.isEmpty() ? deltaLink : nextLink;

            nextPageRequestInformation = requestConfigurator == null ? nextPageRequestInformation : requestConfigurator.apply(nextPageRequestInformation);
            currentPage = Objects.requireNonNull(this.requestAdapter.sendAsync(nextPageRequestInformation, this.collectionPageFactory, null)).join();
            List<TEntity> pageItems = extractEntityListFromParsable(this.currentPage);
            if(!pageItems.isEmpty()) {
                this.pageItemQueue.addAll(pageItems);
            }
        }
        if(this.nextLink.equals(extractNextLinkFromParsable(this.currentPage, null))) {
            throw new ServiceException("Detected a nextLink loop. NextLink value: " + this.nextLink);
        }
        return CompletableFuture.completedFuture(null);
    }
    public CompletableFuture<Void> iterate() throws ServiceException, InvocationTargetException, IllegalAccessException {
        if(this.state == PageIteratorState.DELTA) {
            interpageIterate().join();
        }
        boolean shouldContinueInterpageIteration = intrapageIterate().join();
        while (shouldContinueInterpageIteration) {
            interpageIterate().join();
            shouldContinueInterpageIteration = intrapageIterate().join();
        }
        return CompletableFuture.completedFuture(null);
    }
    public CompletableFuture<Void> resume() throws ServiceException, InvocationTargetException, IllegalAccessException {
        return CompletableFuture.completedFuture(iterate().join());
    }
    @SuppressFBWarnings
    private static <TEntity extends Parsable, TCollectionPage extends Parsable & AdditionalDataHolder> List<TEntity> extractEntityListFromParsable(TCollectionPage parsableCollection) throws   IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        try{
            return (List<TEntity>) parsableCollection.getClass().getDeclaredMethod("getValue").invoke(parsableCollection);
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The Parsable does not contain a collection property");
        }
    }
    private static <TCollectionPage extends Parsable & AdditionalDataHolder> String extractNextLinkFromParsable(TCollectionPage parsableCollection, @Nullable String getNextLinkMethodName) throws   IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String methodName = getNextLinkMethodName == null ? CoreConstants.CollectionResponseMethods.GET_ODATA_NEXT_LINK : getNextLinkMethodName;
        try{
            String nextLink = (String) parsableCollection.getClass().getDeclaredMethod(methodName).invoke(parsableCollection);
            if(!nextLink.isEmpty()) {
                return nextLink;
            } else {
                nextLink = (String) parsableCollection.getAdditionalData().get(CoreConstants.OdataInstanceAnnotations.NEXT_LINK);
                return nextLink != null ? nextLink : "";
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The Parsable does not contain a odataNextLink property");
        }
    }
    public enum PageIteratorState {
        NOT_STARTED,
        PAUSED,
        INTERPAGE_ITERATION,
        INTRAPAGE_ITERATION,
        DELTA,
        COMPLETE
    }
}
