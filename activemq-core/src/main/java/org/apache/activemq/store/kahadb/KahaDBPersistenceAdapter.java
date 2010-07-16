begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerServiceAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ConnectionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ProducerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|MessageStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|TopicMessageStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|TransactionStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link PersistenceAdapter} designed for use with a  * {@link Journal} and then check pointing asynchronously on a timeout with some  * other long term persistent storage.  *   * @org.apache.xbean.XBean element="kahaDB"  * @version $Revision: 1.17 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBPersistenceAdapter
implements|implements
name|PersistenceAdapter
implements|,
name|BrokerServiceAware
block|{
specifier|private
specifier|final
name|KahaDBStore
name|letter
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
comment|/**      * @param context      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#beginTransaction(org.apache.activemq.broker.ConnectionContext)      */
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|letter
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param sync      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#checkpoint(boolean)      */
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|letter
operator|.
name|checkpoint
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param context      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#commitTransaction(org.apache.activemq.broker.ConnectionContext)      */
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|letter
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param destination      * @return MessageStore      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#createQueueMessageStore(org.apache.activemq.command.ActiveMQQueue)      */
specifier|public
name|MessageStore
name|createQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|createQueueMessageStore
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/**      * @param destination      * @return TopicMessageStore      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#createTopicMessageStore(org.apache.activemq.command.ActiveMQTopic)      */
specifier|public
name|TopicMessageStore
name|createTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|createTopicMessageStore
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/**      * @return TrandactionStore      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#createTransactionStore()      */
specifier|public
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|createTransactionStore
argument_list|()
return|;
block|}
comment|/**      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#deleteAllMessages()      */
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|letter
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return destinations      * @see org.apache.activemq.store.PersistenceAdapter#getDestinations()      */
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getDestinations
argument_list|()
return|;
block|}
comment|/**      * @return lastMessageBrokerSequenceId      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#getLastMessageBrokerSequenceId()      */
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getLastMessageBrokerSequenceId
argument_list|()
return|;
block|}
specifier|public
name|long
name|getLastProducerSequenceId
parameter_list|(
name|ProducerId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getLastProducerSequenceId
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * @param destination      * @see org.apache.activemq.store.PersistenceAdapter#removeQueueMessageStore(org.apache.activemq.command.ActiveMQQueue)      */
specifier|public
name|void
name|removeQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|removeQueueMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param destination      * @see org.apache.activemq.store.PersistenceAdapter#removeTopicMessageStore(org.apache.activemq.command.ActiveMQTopic)      */
specifier|public
name|void
name|removeTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|removeTopicMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param context      * @throws IOException      * @see org.apache.activemq.store.PersistenceAdapter#rollbackTransaction(org.apache.activemq.broker.ConnectionContext)      */
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|letter
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param brokerName      * @see org.apache.activemq.store.PersistenceAdapter#setBrokerName(java.lang.String)      */
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param usageManager      * @see org.apache.activemq.store.PersistenceAdapter#setUsageManager(org.apache.activemq.usage.SystemUsage)      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the size of the store      * @see org.apache.activemq.store.PersistenceAdapter#size()      */
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * @throws Exception      * @see org.apache.activemq.Service#start()      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|letter
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws Exception      * @see org.apache.activemq.Service#stop()      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|letter
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Get the journalMaxFileLength      *       * @return the journalMaxFileLength      */
specifier|public
name|int
name|getJournalMaxFileLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getJournalMaxFileLength
argument_list|()
return|;
block|}
comment|/**      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can      * be used      *       * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryIntPropertyEditor"      */
specifier|public
name|void
name|setJournalMaxFileLength
parameter_list|(
name|int
name|journalMaxFileLength
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setJournalMaxFileLength
argument_list|(
name|journalMaxFileLength
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the max number of producers (LRU cache) to track for duplicate sends      */
specifier|public
name|void
name|setMaxFailoverProducersToTrack
parameter_list|(
name|int
name|maxFailoverProducersToTrack
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setMaxFailoverProducersToTrack
argument_list|(
name|maxFailoverProducersToTrack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxFailoverProducersToTrack
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getMaxFailoverProducersToTrack
argument_list|()
return|;
block|}
comment|/**      * set the audit window depth for duplicate suppression (should exceed the max transaction      * batch)      */
specifier|public
name|void
name|setFailoverProducersAuditDepth
parameter_list|(
name|int
name|failoverProducersAuditDepth
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setFailoverProducersAuditDepth
argument_list|(
name|failoverProducersAuditDepth
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getFailoverProducersAuditDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|getFailoverProducersAuditDepth
argument_list|()
return|;
block|}
comment|/**      * Get the checkpointInterval      *       * @return the checkpointInterval      */
specifier|public
name|long
name|getCheckpointInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getCheckpointInterval
argument_list|()
return|;
block|}
comment|/**      * Set the checkpointInterval      *       * @param checkpointInterval      *            the checkpointInterval to set      */
specifier|public
name|void
name|setCheckpointInterval
parameter_list|(
name|long
name|checkpointInterval
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setCheckpointInterval
argument_list|(
name|checkpointInterval
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the cleanupInterval      *       * @return the cleanupInterval      */
specifier|public
name|long
name|getCleanupInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getCleanupInterval
argument_list|()
return|;
block|}
comment|/**      * Set the cleanupInterval      *       * @param cleanupInterval      *            the cleanupInterval to set      */
specifier|public
name|void
name|setCleanupInterval
parameter_list|(
name|long
name|cleanupInterval
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setCleanupInterval
argument_list|(
name|cleanupInterval
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the indexWriteBatchSize      *       * @return the indexWriteBatchSize      */
specifier|public
name|int
name|getIndexWriteBatchSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getIndexWriteBatchSize
argument_list|()
return|;
block|}
comment|/**      * Set the indexWriteBatchSize      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can be used      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      * @param indexWriteBatchSize      *            the indexWriteBatchSize to set      */
specifier|public
name|void
name|setIndexWriteBatchSize
parameter_list|(
name|int
name|indexWriteBatchSize
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setIndexWriteBatchSize
argument_list|(
name|indexWriteBatchSize
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the journalMaxWriteBatchSize      *       * @return the journalMaxWriteBatchSize      */
specifier|public
name|int
name|getJournalMaxWriteBatchSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getJournalMaxWriteBatchSize
argument_list|()
return|;
block|}
comment|/**      * Set the journalMaxWriteBatchSize      *  * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can be used      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      * @param journalMaxWriteBatchSize      *            the journalMaxWriteBatchSize to set      */
specifier|public
name|void
name|setJournalMaxWriteBatchSize
parameter_list|(
name|int
name|journalMaxWriteBatchSize
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setJournalMaxWriteBatchSize
argument_list|(
name|journalMaxWriteBatchSize
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the enableIndexWriteAsync      *       * @return the enableIndexWriteAsync      */
specifier|public
name|boolean
name|isEnableIndexWriteAsync
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|isEnableIndexWriteAsync
argument_list|()
return|;
block|}
comment|/**      * Set the enableIndexWriteAsync      *       * @param enableIndexWriteAsync      *            the enableIndexWriteAsync to set      */
specifier|public
name|void
name|setEnableIndexWriteAsync
parameter_list|(
name|boolean
name|enableIndexWriteAsync
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setEnableIndexWriteAsync
argument_list|(
name|enableIndexWriteAsync
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the directory      *       * @return the directory      */
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getDirectory
argument_list|()
return|;
block|}
comment|/**      * @param dir      * @see org.apache.activemq.store.PersistenceAdapter#setDirectory(java.io.File)      */
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the enableJournalDiskSyncs      *       * @return the enableJournalDiskSyncs      */
specifier|public
name|boolean
name|isEnableJournalDiskSyncs
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|isEnableJournalDiskSyncs
argument_list|()
return|;
block|}
comment|/**      * Set the enableJournalDiskSyncs      *       * @param enableJournalDiskSyncs      *            the enableJournalDiskSyncs to set      */
specifier|public
name|void
name|setEnableJournalDiskSyncs
parameter_list|(
name|boolean
name|enableJournalDiskSyncs
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setEnableJournalDiskSyncs
argument_list|(
name|enableJournalDiskSyncs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the indexCacheSize      *       * @return the indexCacheSize      */
specifier|public
name|int
name|getIndexCacheSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|getIndexCacheSize
argument_list|()
return|;
block|}
comment|/**      * Set the indexCacheSize      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can be used      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      * @param indexCacheSize      *            the indexCacheSize to set      */
specifier|public
name|void
name|setIndexCacheSize
parameter_list|(
name|int
name|indexCacheSize
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setIndexCacheSize
argument_list|(
name|indexCacheSize
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the ignoreMissingJournalfiles      *       * @return the ignoreMissingJournalfiles      */
specifier|public
name|boolean
name|isIgnoreMissingJournalfiles
parameter_list|()
block|{
return|return
name|this
operator|.
name|letter
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
return|;
block|}
comment|/**      * Set the ignoreMissingJournalfiles      *       * @param ignoreMissingJournalfiles      *            the ignoreMissingJournalfiles to set      */
specifier|public
name|void
name|setIgnoreMissingJournalfiles
parameter_list|(
name|boolean
name|ignoreMissingJournalfiles
parameter_list|)
block|{
name|this
operator|.
name|letter
operator|.
name|setIgnoreMissingJournalfiles
argument_list|(
name|ignoreMissingJournalfiles
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isChecksumJournalFiles
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isChecksumJournalFiles
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isCheckForCorruptJournalFiles
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isCheckForCorruptJournalFiles
argument_list|()
return|;
block|}
specifier|public
name|void
name|setChecksumJournalFiles
parameter_list|(
name|boolean
name|checksumJournalFiles
parameter_list|)
block|{
name|letter
operator|.
name|setChecksumJournalFiles
argument_list|(
name|checksumJournalFiles
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCheckForCorruptJournalFiles
parameter_list|(
name|boolean
name|checkForCorruptJournalFiles
parameter_list|)
block|{
name|letter
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
name|checkForCorruptJournalFiles
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|letter
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isArchiveDataLogs
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isArchiveDataLogs
argument_list|()
return|;
block|}
specifier|public
name|void
name|setArchiveDataLogs
parameter_list|(
name|boolean
name|archiveDataLogs
parameter_list|)
block|{
name|letter
operator|.
name|setArchiveDataLogs
argument_list|(
name|archiveDataLogs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|File
name|getDirectoryArchive
parameter_list|()
block|{
return|return
name|letter
operator|.
name|getDirectoryArchive
argument_list|()
return|;
block|}
specifier|public
name|void
name|setDirectoryArchive
parameter_list|(
name|File
name|directoryArchive
parameter_list|)
block|{
name|letter
operator|.
name|setDirectoryArchive
argument_list|(
name|directoryArchive
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isConcurrentStoreAndDispatchQueues
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isConcurrentStoreAndDispatchQueues
argument_list|()
return|;
block|}
specifier|public
name|void
name|setConcurrentStoreAndDispatchQueues
parameter_list|(
name|boolean
name|concurrentStoreAndDispatch
parameter_list|)
block|{
name|letter
operator|.
name|setConcurrentStoreAndDispatchQueues
argument_list|(
name|concurrentStoreAndDispatch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isConcurrentStoreAndDispatchTopics
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isConcurrentStoreAndDispatchTopics
argument_list|()
return|;
block|}
specifier|public
name|void
name|setConcurrentStoreAndDispatchTopics
parameter_list|(
name|boolean
name|concurrentStoreAndDispatch
parameter_list|)
block|{
name|letter
operator|.
name|setConcurrentStoreAndDispatchTopics
argument_list|(
name|concurrentStoreAndDispatch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the concurrentStoreAndDispatchTransactions      */
specifier|public
name|boolean
name|isConcurrentStoreAndDispatchTransactions
parameter_list|()
block|{
return|return
name|letter
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
return|;
block|}
comment|/**      * @param concurrentStoreAndDispatchTransactions      *            the concurrentStoreAndDispatchTransactions to set      */
specifier|public
name|void
name|setConcurrentStoreAndDispatchTransactions
parameter_list|(
name|boolean
name|concurrentStoreAndDispatchTransactions
parameter_list|)
block|{
name|letter
operator|.
name|setConcurrentStoreAndDispatchTransactions
argument_list|(
name|concurrentStoreAndDispatchTransactions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxAsyncJobs
parameter_list|()
block|{
return|return
name|letter
operator|.
name|getMaxAsyncJobs
argument_list|()
return|;
block|}
comment|/**      * @param maxAsyncJobs      *            the maxAsyncJobs to set      */
specifier|public
name|void
name|setMaxAsyncJobs
parameter_list|(
name|int
name|maxAsyncJobs
parameter_list|)
block|{
name|letter
operator|.
name|setMaxAsyncJobs
argument_list|(
name|maxAsyncJobs
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the databaseLockedWaitDelay      */
specifier|public
name|int
name|getDatabaseLockedWaitDelay
parameter_list|()
block|{
return|return
name|letter
operator|.
name|getDatabaseLockedWaitDelay
argument_list|()
return|;
block|}
comment|/**      * @param databaseLockedWaitDelay the databaseLockedWaitDelay to set      */
specifier|public
name|void
name|setDatabaseLockedWaitDelay
parameter_list|(
name|int
name|databaseLockedWaitDelay
parameter_list|)
block|{
name|letter
operator|.
name|setDatabaseLockedWaitDelay
argument_list|(
name|databaseLockedWaitDelay
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|getForceRecoverIndex
parameter_list|()
block|{
return|return
name|letter
operator|.
name|getForceRecoverIndex
argument_list|()
return|;
block|}
specifier|public
name|void
name|setForceRecoverIndex
parameter_list|(
name|boolean
name|forceRecoverIndex
parameter_list|)
block|{
name|letter
operator|.
name|setForceRecoverIndex
argument_list|(
name|forceRecoverIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|path
init|=
name|getDirectory
argument_list|()
operator|!=
literal|null
condition|?
name|getDirectory
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|"DIRECTORY_NOT_SET"
decl_stmt|;
return|return
literal|"KahaDBPersistenceAdapter["
operator|+
name|path
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

