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
name|FileFilter
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|LocalTransactionId
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
name|command
operator|.
name|XATransactionId
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
name|filter
operator|.
name|AnyDestination
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
name|filter
operator|.
name|DestinationMap
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
name|protobuf
operator|.
name|Buffer
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaTransactionInfo
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
name|kahadb
operator|.
name|data
operator|.
name|KahaXATransactionId
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IOHelper
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
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link org.apache.activemq.store.PersistenceAdapter}  that supports  * distribution of destinations across multiple kahaDB persistence adapters  *  * @org.apache.xbean.XBean element="mKahaDB"  */
end_comment

begin_class
specifier|public
class|class
name|MultiKahaDBPersistenceAdapter
extends|extends
name|DestinationMap
implements|implements
name|PersistenceAdapter
implements|,
name|BrokerServiceAware
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MultiKahaDBPersistenceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|ActiveMQDestination
name|matchAll
init|=
operator|new
name|AnyDestination
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
block|}
argument_list|)
decl_stmt|;
specifier|final
name|int
name|LOCAL_FORMAT_ID_MAGIC
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.store.kahadb.MultiKahaDBTransactionStore.localXaFormatId"
argument_list|,
literal|"61616"
argument_list|)
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|List
argument_list|<
name|KahaDBPersistenceAdapter
argument_list|>
name|adapters
init|=
operator|new
name|LinkedList
argument_list|<
name|KahaDBPersistenceAdapter
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"mKahaDB"
argument_list|)
decl_stmt|;
name|MultiKahaDBTransactionStore
name|transactionStore
init|=
operator|new
name|MultiKahaDBTransactionStore
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// all local store transactions are XA, 2pc if more than one adapter involved
name|TransactionIdTransformer
name|transactionIdTransformer
init|=
operator|new
name|TransactionIdTransformer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KahaTransactionInfo
name|transform
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
if|if
condition|(
name|txid
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|KahaTransactionInfo
name|rc
init|=
operator|new
name|KahaTransactionInfo
argument_list|()
decl_stmt|;
name|KahaXATransactionId
name|kahaTxId
init|=
operator|new
name|KahaXATransactionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|txid
operator|.
name|isLocalTransaction
argument_list|()
condition|)
block|{
name|LocalTransactionId
name|t
init|=
operator|(
name|LocalTransactionId
operator|)
name|txid
decl_stmt|;
name|kahaTxId
operator|.
name|setBranchQualifier
argument_list|(
operator|new
name|Buffer
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|t
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|kahaTxId
operator|.
name|setGlobalTransactionId
argument_list|(
operator|new
name|Buffer
argument_list|(
name|t
operator|.
name|getConnectionId
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|kahaTxId
operator|.
name|setFormatId
argument_list|(
name|LOCAL_FORMAT_ID_MAGIC
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XATransactionId
name|t
init|=
operator|(
name|XATransactionId
operator|)
name|txid
decl_stmt|;
name|kahaTxId
operator|.
name|setBranchQualifier
argument_list|(
operator|new
name|Buffer
argument_list|(
name|t
operator|.
name|getBranchQualifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|kahaTxId
operator|.
name|setGlobalTransactionId
argument_list|(
operator|new
name|Buffer
argument_list|(
name|t
operator|.
name|getGlobalTransactionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|kahaTxId
operator|.
name|setFormatId
argument_list|(
name|t
operator|.
name|getFormatId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rc
operator|.
name|setXaTransacitonId
argument_list|(
name|kahaTxId
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Sets the  FilteredKahaDBPersistenceAdapter entries      *      * @org.apache.xbean.ElementType class="org.apache.activemq.store.kahadb.FilteredKahaDBPersistenceAdapter"      */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
name|void
name|setFilteredPersistenceAdapters
parameter_list|(
name|List
name|entries
parameter_list|)
block|{
for|for
control|(
name|Object
name|entry
range|:
name|entries
control|)
block|{
name|FilteredKahaDBPersistenceAdapter
name|filteredAdapter
init|=
operator|(
name|FilteredKahaDBPersistenceAdapter
operator|)
name|entry
decl_stmt|;
name|KahaDBPersistenceAdapter
name|adapter
init|=
name|filteredAdapter
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
if|if
condition|(
name|filteredAdapter
operator|.
name|getDestination
argument_list|()
operator|==
literal|null
condition|)
block|{
name|filteredAdapter
operator|.
name|setDestination
argument_list|(
name|matchAll
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filteredAdapter
operator|.
name|isPerDestination
argument_list|()
condition|)
block|{
name|configureDirectory
argument_list|(
name|adapter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// per destination adapters will be created on demand or during recovery
continue|continue;
block|}
else|else
block|{
name|configureDirectory
argument_list|(
name|adapter
argument_list|,
name|nameFromDestinationFilter
argument_list|(
name|filteredAdapter
operator|.
name|getDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|configureAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|nameFromDestinationFilter
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|IOHelper
operator|.
name|toFileSystemSafeName
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isLocalXid
parameter_list|(
name|TransactionId
name|xid
parameter_list|)
block|{
return|return
name|xid
operator|instanceof
name|XATransactionId
operator|&&
operator|(
operator|(
name|XATransactionId
operator|)
name|xid
operator|)
operator|.
name|getFormatId
argument_list|()
operator|==
name|LOCAL_FORMAT_ID_MAGIC
return|;
block|}
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
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|checkpoint
parameter_list|(
specifier|final
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|checkpoint
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
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
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
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
name|PersistenceAdapter
name|persistenceAdapter
init|=
name|getMatchingPersistenceAdapter
argument_list|(
name|destination
argument_list|)
decl_stmt|;
return|return
name|transactionStore
operator|.
name|proxy
argument_list|(
name|persistenceAdapter
operator|.
name|createTransactionStore
argument_list|()
argument_list|,
name|persistenceAdapter
operator|.
name|createQueueMessageStore
argument_list|(
name|destination
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|PersistenceAdapter
name|getMatchingPersistenceAdapter
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Object
name|result
init|=
name|this
operator|.
name|chooseValue
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No matching persistence adapter configured for destination: "
operator|+
name|destination
operator|+
literal|", options:"
operator|+
name|adapters
argument_list|)
throw|;
block|}
name|FilteredKahaDBPersistenceAdapter
name|filteredAdapter
init|=
operator|(
name|FilteredKahaDBPersistenceAdapter
operator|)
name|result
decl_stmt|;
if|if
condition|(
name|filteredAdapter
operator|.
name|getDestination
argument_list|()
operator|==
name|matchAll
operator|&&
name|filteredAdapter
operator|.
name|isPerDestination
argument_list|()
condition|)
block|{
name|result
operator|=
name|addAdapter
argument_list|(
name|filteredAdapter
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|startAdapter
argument_list|(
operator|(
operator|(
name|FilteredKahaDBPersistenceAdapter
operator|)
name|result
operator|)
operator|.
name|getPersistenceAdapter
argument_list|()
argument_list|,
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"created per destination adapter for: "
operator|+
name|destination
operator|+
literal|", "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
operator|(
name|FilteredKahaDBPersistenceAdapter
operator|)
name|result
operator|)
operator|.
name|getPersistenceAdapter
argument_list|()
return|;
block|}
specifier|private
name|void
name|startAdapter
parameter_list|(
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
parameter_list|,
name|String
name|destination
parameter_list|)
block|{
try|try
block|{
name|kahaDBPersistenceAdapter
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|RuntimeException
name|detail
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to start per destination persistence adapter for destination: "
operator|+
name|destination
operator|+
literal|", options:"
operator|+
name|adapters
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|detail
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|detail
throw|;
block|}
block|}
specifier|private
name|void
name|stopAdapter
parameter_list|(
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
parameter_list|,
name|String
name|destination
parameter_list|)
block|{
try|try
block|{
name|kahaDBPersistenceAdapter
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|RuntimeException
name|detail
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to stop per destination persistence adapter for destination: "
operator|+
name|destination
operator|+
literal|", options:"
operator|+
name|adapters
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|detail
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|detail
throw|;
block|}
block|}
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
name|PersistenceAdapter
name|persistenceAdapter
init|=
name|getMatchingPersistenceAdapter
argument_list|(
name|destination
argument_list|)
decl_stmt|;
return|return
name|transactionStore
operator|.
name|proxy
argument_list|(
name|persistenceAdapter
operator|.
name|createTransactionStore
argument_list|()
argument_list|,
name|persistenceAdapter
operator|.
name|createTopicMessageStore
argument_list|(
name|destination
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|transactionStore
return|;
block|}
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
name|transactionStore
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|results
operator|.
name|addAll
argument_list|(
name|persistenceAdapter
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|maxId
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|maxId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxId
argument_list|,
name|persistenceAdapter
operator|.
name|getLastMessageBrokerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|maxId
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
name|long
name|maxId
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|maxId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxId
argument_list|,
name|persistenceAdapter
operator|.
name|getLastProducerSequenceId
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|maxId
return|;
block|}
specifier|public
name|void
name|removeQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
block|{
name|PersistenceAdapter
name|adapter
init|=
name|getMatchingPersistenceAdapter
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|adapter
operator|.
name|removeQueueMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|adapter
operator|instanceof
name|KahaDBPersistenceAdapter
condition|)
block|{
name|adapter
operator|.
name|removeQueueMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|removeMessageStore
argument_list|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|adapter
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
block|{
name|PersistenceAdapter
name|adapter
init|=
name|getMatchingPersistenceAdapter
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|adapter
operator|instanceof
name|KahaDBPersistenceAdapter
condition|)
block|{
name|adapter
operator|.
name|removeTopicMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|removeMessageStore
argument_list|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|adapter
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeMessageStore
parameter_list|(
name|KahaDBPersistenceAdapter
name|adapter
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|adapter
operator|.
name|getDestinations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stopAdapter
argument_list|(
name|adapter
argument_list|,
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|adapterDir
init|=
name|adapter
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|adapterDir
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|IOHelper
operator|.
name|deleteFile
argument_list|(
name|adapterDir
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"deleted per destination adapter directory for: "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failed to deleted per destination adapter directory for: "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|size
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|size
operator|+=
name|persistenceAdapter
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|Object
name|result
init|=
name|this
operator|.
name|chooseValue
argument_list|(
name|matchAll
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|FilteredKahaDBPersistenceAdapter
name|filteredAdapter
init|=
operator|(
name|FilteredKahaDBPersistenceAdapter
operator|)
name|result
decl_stmt|;
if|if
condition|(
name|filteredAdapter
operator|.
name|getDestination
argument_list|()
operator|==
name|matchAll
operator|&&
name|filteredAdapter
operator|.
name|isPerDestination
argument_list|()
condition|)
block|{
name|findAndRegisterExistingAdapters
argument_list|(
name|filteredAdapter
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|findAndRegisterExistingAdapters
parameter_list|(
name|FilteredKahaDBPersistenceAdapter
name|template
parameter_list|)
block|{
name|FileFilter
name|destinationNames
init|=
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"queue#"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"topic#"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|File
index|[]
name|candidates
init|=
name|template
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|.
name|listFiles
argument_list|(
name|destinationNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|candidates
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|candidate
range|:
name|candidates
control|)
block|{
name|registerExistingAdapter
argument_list|(
name|template
argument_list|,
name|candidate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|registerExistingAdapter
parameter_list|(
name|FilteredKahaDBPersistenceAdapter
name|filteredAdapter
parameter_list|,
name|File
name|candidate
parameter_list|)
block|{
name|KahaDBPersistenceAdapter
name|adapter
init|=
name|adapterFromTemplate
argument_list|(
name|filteredAdapter
operator|.
name|getPersistenceAdapter
argument_list|()
argument_list|,
name|candidate
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|startAdapter
argument_list|(
name|adapter
argument_list|,
name|candidate
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|adapter
operator|.
name|getDestinations
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|registerAdapter
argument_list|(
name|adapter
argument_list|,
name|adapter
operator|.
name|getDestinations
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{}
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stopAdapter
argument_list|(
name|adapter
argument_list|,
name|candidate
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|FilteredKahaDBPersistenceAdapter
name|addAdapter
parameter_list|(
name|FilteredKahaDBPersistenceAdapter
name|filteredAdapter
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|KahaDBPersistenceAdapter
name|adapter
init|=
name|adapterFromTemplate
argument_list|(
name|filteredAdapter
operator|.
name|getPersistenceAdapter
argument_list|()
argument_list|,
name|nameFromDestinationFilter
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|registerAdapter
argument_list|(
name|adapter
argument_list|,
name|destination
argument_list|)
return|;
block|}
specifier|private
name|KahaDBPersistenceAdapter
name|adapterFromTemplate
parameter_list|(
name|KahaDBPersistenceAdapter
name|template
parameter_list|,
name|String
name|destinationName
parameter_list|)
block|{
name|KahaDBPersistenceAdapter
name|adapter
init|=
name|kahaDBFromTemplate
argument_list|(
name|template
argument_list|)
decl_stmt|;
name|configureAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|configureDirectory
argument_list|(
name|adapter
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
return|return
name|adapter
return|;
block|}
specifier|private
name|void
name|configureDirectory
parameter_list|(
name|KahaDBPersistenceAdapter
name|adapter
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
name|File
name|directory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|MessageDatabase
operator|.
name|DEFAULT_DIRECTORY
operator|.
name|equals
argument_list|(
name|adapter
operator|.
name|getDirectory
argument_list|()
argument_list|)
condition|)
block|{
comment|// not set so inherit from mkahadb
name|directory
operator|=
name|getDirectory
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|directory
operator|=
name|adapter
operator|.
name|getDirectory
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
name|adapter
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
specifier|private
name|FilteredKahaDBPersistenceAdapter
name|registerAdapter
parameter_list|(
name|KahaDBPersistenceAdapter
name|adapter
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|adapters
operator|.
name|add
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|FilteredKahaDBPersistenceAdapter
name|result
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|(
name|destination
argument_list|,
name|adapter
argument_list|)
decl_stmt|;
name|put
argument_list|(
name|destination
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|configureAdapter
parameter_list|(
name|KahaDBPersistenceAdapter
name|adapter
parameter_list|)
block|{
comment|// need a per store factory that will put the store in the branch qualifier to disiambiguate xid mbeans
name|adapter
operator|.
name|getStore
argument_list|()
operator|.
name|setTransactionIdTransformer
argument_list|(
name|transactionIdTransformer
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setBrokerService
argument_list|(
name|getBrokerService
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|KahaDBPersistenceAdapter
name|kahaDBFromTemplate
parameter_list|(
name|KahaDBPersistenceAdapter
name|template
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|getProperties
argument_list|(
name|template
argument_list|,
name|configuration
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|adapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|adapter
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
return|return
name|adapter
return|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|PersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|this
operator|.
name|directory
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
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
for|for
control|(
name|KahaDBPersistenceAdapter
name|persistenceAdapter
range|:
name|adapters
control|)
block|{
name|persistenceAdapter
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
name|brokerService
return|;
block|}
specifier|public
name|void
name|setTransactionStore
parameter_list|(
name|MultiKahaDBTransactionStore
name|transactionStore
parameter_list|)
block|{
name|this
operator|.
name|transactionStore
operator|=
name|transactionStore
expr_stmt|;
block|}
comment|/**      * Set the max file length of the transaction journal      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can      * be used      *      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryIntPropertyEditor"      */
specifier|public
name|void
name|setJournalMaxFileLength
parameter_list|(
name|int
name|maxFileLength
parameter_list|)
block|{
name|transactionStore
operator|.
name|setJournalMaxFileLength
argument_list|(
name|maxFileLength
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalMaxFileLength
parameter_list|()
block|{
return|return
name|transactionStore
operator|.
name|getJournalMaxFileLength
argument_list|()
return|;
block|}
comment|/**      * Set the max write batch size of  the transaction journal      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can      * be used      *      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryIntPropertyEditor"      */
specifier|public
name|void
name|setJournalWriteBatchSize
parameter_list|(
name|int
name|journalWriteBatchSize
parameter_list|)
block|{
name|transactionStore
operator|.
name|setJournalMaxWriteBatchSize
argument_list|(
name|journalWriteBatchSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalWriteBatchSize
parameter_list|()
block|{
return|return
name|transactionStore
operator|.
name|getJournalMaxWriteBatchSize
argument_list|()
return|;
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
name|getAbsolutePath
argument_list|()
else|:
literal|"DIRECTORY_NOT_SET"
decl_stmt|;
return|return
literal|"MultiKahaDBPersistenceAdapter["
operator|+
name|path
operator|+
literal|"]"
operator|+
name|adapters
return|;
block|}
block|}
end_class

end_unit

