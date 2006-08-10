begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StringMarshaller
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
name|memory
operator|.
name|UsageManager
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
name|openwire
operator|.
name|OpenWireFormat
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaPersistenceAdapter
implements|implements
name|PersistenceAdapter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KahaPersistenceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|PREPARED_TRANSACTIONS_NAME
init|=
literal|"PreparedTransactions"
decl_stmt|;
name|KahaTransactionStore
name|transactionStore
decl_stmt|;
name|ConcurrentHashMap
name|topics
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
name|queues
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
name|messageStores
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|useExternalMessageReferences
decl_stmt|;
specifier|private
name|OpenWireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
name|long
name|maxDataFileLength
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|Store
name|store
decl_stmt|;
specifier|public
name|KahaPersistenceAdapter
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"kaha.db"
decl_stmt|;
name|store
operator|=
name|StoreFactory
operator|.
name|open
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setMaxDataFileLength
argument_list|(
name|maxDataFileLength
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Set
name|getDestinations
parameter_list|()
block|{
name|Set
name|rc
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|store
operator|.
name|getMapContainerIds
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
name|Object
name|obj
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|ActiveMQDestination
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to get destinations "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
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
name|MessageStore
name|rc
init|=
operator|(
name|MessageStore
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
name|KahaMessageStore
argument_list|(
name|getMapContainer
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
if|if
condition|(
name|transactionStore
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|transactionStore
operator|.
name|proxy
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
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
name|TopicMessageStore
name|rc
init|=
operator|(
name|TopicMessageStore
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
name|MapContainer
name|messageContainer
init|=
name|getMapContainer
argument_list|(
name|destination
argument_list|,
literal|"topic-data"
argument_list|)
decl_stmt|;
name|MapContainer
name|subsContainer
init|=
name|getMapContainer
argument_list|(
name|destination
operator|.
name|toString
argument_list|()
operator|+
literal|"-Subscriptions"
argument_list|,
literal|"topic-subs"
argument_list|)
decl_stmt|;
name|MapContainer
name|ackContainer
init|=
name|store
operator|.
name|getMapContainer
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
name|setKeyMarshaller
argument_list|(
operator|new
name|StringMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|ackContainer
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|AtomicIntegerMarshaller
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
operator|new
name|KahaTopicMessageStore
argument_list|(
name|store
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
if|if
condition|(
name|transactionStore
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|transactionStore
operator|.
name|proxy
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
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
specifier|protected
name|MessageStore
name|retrieveMessageStore
parameter_list|(
name|Object
name|id
parameter_list|)
block|{
name|MessageStore
name|result
init|=
operator|(
name|MessageStore
operator|)
name|messageStores
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|transactionStore
operator|==
literal|null
condition|)
block|{
name|MapContainer
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|PREPARED_TRANSACTIONS_NAME
argument_list|,
literal|"transactions"
argument_list|)
decl_stmt|;
name|container
operator|.
name|setKeyMarshaller
argument_list|(
operator|new
name|CommandMarshaller
argument_list|(
name|wireFormat
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|TransactionMarshaller
argument_list|(
name|wireFormat
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|transactionStore
operator|=
operator|new
name|KahaTransactionStore
argument_list|(
name|this
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
return|return
name|transactionStore
return|;
block|}
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{}
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
name|store
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isUseExternalMessageReferences
parameter_list|()
block|{
return|return
name|useExternalMessageReferences
return|;
block|}
specifier|public
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|useExternalMessageReferences
parameter_list|)
block|{
name|this
operator|.
name|useExternalMessageReferences
operator|=
name|useExternalMessageReferences
expr_stmt|;
block|}
specifier|protected
name|MapContainer
name|getMapContainer
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
name|MapContainer
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|id
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|container
operator|.
name|setKeyMarshaller
argument_list|(
operator|new
name|StringMarshaller
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|useExternalMessageReferences
condition|)
block|{
name|container
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|StringMarshaller
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|container
operator|.
name|setValueMarshaller
argument_list|(
operator|new
name|CommandMarshaller
argument_list|(
name|wireFormat
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|container
return|;
block|}
comment|/**      * @param usageManager      *            The UsageManager that is controlling the broker's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{}
comment|/**      * @return the maxDataFileLength      */
specifier|public
name|long
name|getMaxDataFileLength
parameter_list|()
block|{
return|return
name|maxDataFileLength
return|;
block|}
comment|/**      * @param maxDataFileLength the maxDataFileLength to set      */
specifier|public
name|void
name|setMaxDataFileLength
parameter_list|(
name|long
name|maxDataFileLength
parameter_list|)
block|{
name|this
operator|.
name|maxDataFileLength
operator|=
name|maxDataFileLength
expr_stmt|;
block|}
block|}
end_class

end_unit

