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
name|kahadaptor
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|MessageId
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
name|SubscriptionInfo
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
name|TransactionId
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
name|kaha
operator|.
name|CommandMarshaller
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
name|kaha
operator|.
name|ListContainer
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
name|kaha
operator|.
name|MapContainer
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
name|kaha
operator|.
name|MessageIdMarshaller
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
name|kaha
operator|.
name|Store
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
name|kaha
operator|.
name|StoreFactory
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
name|kaha
operator|.
name|impl
operator|.
name|StoreLockedExcpetion
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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|hash
operator|.
name|HashIndex
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
name|ReferenceStore
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
name|ReferenceStoreAdapter
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
name|TopicReferenceStore
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
name|amq
operator|.
name|AMQTx
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|KahaReferenceStoreAdapter
extends|extends
name|KahaPersistenceAdapter
implements|implements
name|ReferenceStoreAdapter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KahaReferenceStoreAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STORE_STATE
init|=
literal|"store-state"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_VERSION_NAME
init|=
literal|"INDEX_VERSION"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|INDEX_VERSION
init|=
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RECORD_REFERENCES
init|=
literal|"record-references"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSACTIONS
init|=
literal|"transactions-state"
decl_stmt|;
specifier|private
name|MapContainer
name|stateMap
decl_stmt|;
specifier|private
name|MapContainer
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|preparedTransactions
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|AtomicInteger
argument_list|>
name|recordReferences
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|AtomicInteger
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ListContainer
argument_list|<
name|SubscriptionInfo
argument_list|>
name|durableSubscribers
decl_stmt|;
specifier|private
name|boolean
name|storeValid
decl_stmt|;
specifier|private
name|Store
name|stateStore
decl_stmt|;
specifier|private
name|boolean
name|persistentIndex
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|indexBinSize
init|=
name|HashIndex
operator|.
name|DEFAULT_BIN_SIZE
decl_stmt|;
specifier|private
name|int
name|indexKeySize
init|=
name|HashIndex
operator|.
name|DEFAULT_KEY_SIZE
decl_stmt|;
specifier|private
name|int
name|indexPageSize
init|=
name|HashIndex
operator|.
name|DEFAULT_PAGE_SIZE
decl_stmt|;
specifier|public
name|KahaReferenceStoreAdapter
parameter_list|(
name|AtomicLong
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|MessageStore
name|createQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use createQueueReferenceStore instead"
argument_list|)
throw|;
block|}
specifier|public
specifier|synchronized
name|TopicMessageStore
name|createTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use createTopicReferenceStore instead"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|Store
name|store
init|=
name|getStateStore
argument_list|()
decl_stmt|;
name|boolean
name|empty
init|=
name|store
operator|.
name|getMapContainerIds
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|stateMap
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
literal|"state"
argument_list|,
name|STORE_STATE
argument_list|)
expr_stmt|;
name|stateMap
operator|.
name|load
argument_list|()
expr_stmt|;
name|storeValid
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|empty
condition|)
block|{
name|AtomicBoolean
name|status
init|=
operator|(
name|AtomicBoolean
operator|)
name|stateMap
operator|.
name|get
argument_list|(
name|STORE_STATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|storeValid
operator|=
name|status
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storeValid
condition|)
block|{
comment|//check what version the indexes are at
name|Integer
name|indexVersion
init|=
operator|(
name|Integer
operator|)
name|stateMap
operator|.
name|get
argument_list|(
name|INDEX_VERSION_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexVersion
operator|==
literal|null
operator|||
name|indexVersion
operator|.
name|intValue
argument_list|()
operator|<
name|INDEX_VERSION
operator|.
name|intValue
argument_list|()
condition|)
block|{
name|storeValid
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Indexes at an older version - need to regenerate"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storeValid
condition|)
block|{
if|if
condition|(
name|stateMap
operator|.
name|containsKey
argument_list|(
name|RECORD_REFERENCES
argument_list|)
condition|)
block|{
name|recordReferences
operator|=
operator|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|AtomicInteger
argument_list|>
operator|)
name|stateMap
operator|.
name|get
argument_list|(
name|RECORD_REFERENCES
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|stateMap
operator|.
name|put
argument_list|(
name|STORE_STATE
argument_list|,
operator|new
name|AtomicBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|stateMap
operator|.
name|put
argument_list|(
name|INDEX_VERSION_NAME
argument_list|,
name|INDEX_VERSION
argument_list|)
expr_stmt|;
name|durableSubscribers
operator|=
name|store
operator|.
name|getListContainer
argument_list|(
literal|"durableSubscribers"
argument_list|)
expr_stmt|;
name|durableSubscribers
operator|.
name|setMarshaller
argument_list|(
operator|new
name|CommandMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|preparedTransactions
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
literal|"transactions"
argument_list|,
name|TRANSACTIONS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// need to set the Marshallers here
name|preparedTransactions
operator|.
name|setKeyMarshaller
argument_list|(
name|Store
operator|.
name|COMMAND_MARSHALLER
argument_list|)
expr_stmt|;
name|preparedTransactions
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|AMQTxMarshaller
argument_list|(
name|wireFormat
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|stateMap
operator|.
name|put
argument_list|(
name|RECORD_REFERENCES
argument_list|,
name|recordReferences
argument_list|)
expr_stmt|;
name|stateMap
operator|.
name|put
argument_list|(
name|STORE_STATE
argument_list|,
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|stateMap
operator|.
name|put
argument_list|(
name|INDEX_VERSION_NAME
argument_list|,
name|INDEX_VERSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|stateStore
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|stateStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|stateMap
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
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
comment|//we don;t need to force on a commit - as the reference store
comment|//is rebuilt on a non clean shutdown
block|}
specifier|public
name|boolean
name|isStoreValid
parameter_list|()
block|{
return|return
name|storeValid
return|;
block|}
specifier|public
name|ReferenceStore
name|createQueueReferenceStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|ReferenceStore
name|rc
init|=
operator|(
name|ReferenceStore
operator|)
name|queues
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
operator|new
name|KahaReferenceStore
argument_list|(
name|this
argument_list|,
name|getMapReferenceContainer
argument_list|(
name|destination
argument_list|,
literal|"queue-data"
argument_list|)
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|messageStores
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// if(transactionStore!=null){
comment|// rc=transactionStore.proxy(rc);
comment|// }
name|queues
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|TopicReferenceStore
name|createTopicReferenceStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|TopicReferenceStore
name|rc
init|=
operator|(
name|TopicReferenceStore
operator|)
name|topics
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|Store
name|store
init|=
name|getStore
argument_list|()
decl_stmt|;
name|MapContainer
name|messageContainer
init|=
name|getMapReferenceContainer
argument_list|(
name|destination
argument_list|,
literal|"topic-data"
argument_list|)
decl_stmt|;
name|MapContainer
name|subsContainer
init|=
name|getSubsMapContainer
argument_list|(
name|destination
operator|.
name|toString
argument_list|()
operator|+
literal|"-Subscriptions"
argument_list|,
literal|"blob"
argument_list|)
decl_stmt|;
name|ListContainer
argument_list|<
name|TopicSubAck
argument_list|>
name|ackContainer
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|destination
operator|.
name|toString
argument_list|()
argument_list|,
literal|"topic-acks"
argument_list|)
decl_stmt|;
name|ackContainer
operator|.
name|setMarshaller
argument_list|(
operator|new
name|TopicSubAckMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
operator|new
name|KahaTopicReferenceStore
argument_list|(
name|store
argument_list|,
name|this
argument_list|,
name|messageContainer
argument_list|,
name|ackContainer
argument_list|,
name|subsContainer
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|messageStores
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|rc
argument_list|)
expr_stmt|;
comment|// if(transactionStore!=null){
comment|// rc=transactionStore.proxy(rc);
comment|// }
name|topics
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/*     public void buildReferenceFileIdsInUse() throws IOException {         recordReferences = new HashMap<Integer, AtomicInteger>();         Set<ActiveMQDestination> destinations = getDestinations();         for (ActiveMQDestination destination : destinations) {             if (destination.isQueue()) {                 KahaReferenceStore store = (KahaReferenceStore)createQueueReferenceStore((ActiveMQQueue)destination);                 store.addReferenceFileIdsInUse();             } else {                 KahaTopicReferenceStore store = (KahaTopicReferenceStore)createTopicReferenceStore((ActiveMQTopic)destination);                 store.addReferenceFileIdsInUse();             }         }     }     */
specifier|protected
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|ReferenceRecord
argument_list|>
name|getMapReferenceContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|Store
name|store
init|=
name|getStore
argument_list|()
decl_stmt|;
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|ReferenceRecord
argument_list|>
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|id
argument_list|,
name|containerName
argument_list|,
name|persistentIndex
argument_list|)
decl_stmt|;
name|container
operator|.
name|setIndexBinSize
argument_list|(
name|getIndexBinSize
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setIndexKeySize
argument_list|(
name|getIndexKeySize
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setIndexPageSize
argument_list|(
name|getIndexPageSize
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setKeyMarshaller
argument_list|(
operator|new
name|MessageIdMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|ReferenceRecordMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|container
return|;
block|}
specifier|synchronized
name|void
name|addInterestInRecordFile
parameter_list|(
name|int
name|recordNumber
parameter_list|)
block|{
name|Integer
name|key
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|recordNumber
argument_list|)
decl_stmt|;
name|AtomicInteger
name|rr
init|=
name|recordReferences
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|==
literal|null
condition|)
block|{
name|rr
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
name|recordReferences
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|rr
argument_list|)
expr_stmt|;
block|}
name|rr
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
name|void
name|removeInterestInRecordFile
parameter_list|(
name|int
name|recordNumber
parameter_list|)
block|{
name|Integer
name|key
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|recordNumber
argument_list|)
decl_stmt|;
name|AtomicInteger
name|rr
init|=
name|recordReferences
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|!=
literal|null
operator|&&
name|rr
operator|.
name|decrementAndGet
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|recordReferences
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return      * @throws IOException      * @see org.apache.activemq.store.ReferenceStoreAdapter#getReferenceFileIdsInUse()      */
specifier|public
name|Set
argument_list|<
name|Integer
argument_list|>
name|getReferenceFileIdsInUse
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|recordReferences
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      *       * @throws IOException      * @see org.apache.activemq.store.ReferenceStoreAdapter#clearMessages()      */
specifier|public
name|void
name|clearMessages
parameter_list|()
throws|throws
name|IOException
block|{
comment|//don't delete messages as it will clear state - call base
comment|//class method to clear out the data instead
name|super
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
comment|/**      *       * @throws IOException      * @see org.apache.activemq.store.ReferenceStoreAdapter#recoverState()      */
specifier|public
name|void
name|recoverState
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|SubscriptionInfo
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|SubscriptionInfo
argument_list|>
argument_list|(
name|this
operator|.
name|durableSubscribers
argument_list|)
decl_stmt|;
for|for
control|(
name|SubscriptionInfo
name|info
range|:
name|set
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering subscriber state for durable subscriber: "
operator|+
name|info
argument_list|)
expr_stmt|;
name|TopicReferenceStore
name|ts
init|=
name|createTopicReferenceStore
argument_list|(
operator|(
name|ActiveMQTopic
operator|)
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|ts
operator|.
name|addSubsciption
argument_list|(
name|info
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|retrievePreparedState
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
argument_list|()
decl_stmt|;
name|preparedTransactions
operator|.
name|load
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|TransactionId
argument_list|>
name|i
init|=
name|preparedTransactions
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TransactionId
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|AMQTx
name|value
init|=
name|preparedTransactions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|savePreparedState
parameter_list|(
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|preparedTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
argument_list|>
name|iter
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|preparedTransactions
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|super
operator|.
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|createStateStore
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|Store
name|getStateStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|stateStore
operator|==
literal|null
condition|)
block|{
name|File
name|stateDirectory
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
literal|"kr-state"
argument_list|)
decl_stmt|;
name|stateDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|createStateStore
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|stateStore
return|;
block|}
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
if|if
condition|(
name|stateStore
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|stateStore
operator|.
name|isInitialized
argument_list|()
condition|)
block|{
name|stateStore
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stateStore
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|File
name|stateDirectory
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
literal|"kr-state"
argument_list|)
decl_stmt|;
name|StoreFactory
operator|.
name|delete
argument_list|(
name|stateDirectory
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isPersistentIndex
parameter_list|()
block|{
return|return
name|persistentIndex
return|;
block|}
specifier|public
name|void
name|setPersistentIndex
parameter_list|(
name|boolean
name|persistentIndex
parameter_list|)
block|{
name|this
operator|.
name|persistentIndex
operator|=
name|persistentIndex
expr_stmt|;
block|}
specifier|private
name|Store
name|createStateStore
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|File
name|stateDirectory
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"state"
argument_list|)
decl_stmt|;
name|stateDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|StoreFactory
operator|.
name|open
argument_list|(
name|stateDirectory
argument_list|,
literal|"rw"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create the state store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|addSubscriberState
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|durableSubscribers
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|removeSubscriberState
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|)
block|{
name|durableSubscribers
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexBinSize
parameter_list|()
block|{
return|return
name|indexBinSize
return|;
block|}
specifier|public
name|void
name|setIndexBinSize
parameter_list|(
name|int
name|indexBinSize
parameter_list|)
block|{
name|this
operator|.
name|indexBinSize
operator|=
name|indexBinSize
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexKeySize
parameter_list|()
block|{
return|return
name|indexKeySize
return|;
block|}
specifier|public
name|void
name|setIndexKeySize
parameter_list|(
name|int
name|indexKeySize
parameter_list|)
block|{
name|this
operator|.
name|indexKeySize
operator|=
name|indexKeySize
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexPageSize
parameter_list|()
block|{
return|return
name|indexPageSize
return|;
block|}
specifier|public
name|void
name|setIndexPageSize
parameter_list|(
name|int
name|indexPageSize
parameter_list|)
block|{
name|this
operator|.
name|indexPageSize
operator|=
name|indexPageSize
expr_stmt|;
block|}
block|}
end_class

end_unit

