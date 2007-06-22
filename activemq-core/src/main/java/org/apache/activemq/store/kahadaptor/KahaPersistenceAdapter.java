begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|Message
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
name|Marshaller
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
name|MessageMarshaller
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
name|int
name|STORE_LOCKED_WAIT_DELAY
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
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
argument_list|<
name|ActiveMQTopic
argument_list|,
name|TopicMessageStore
argument_list|>
name|topics
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQTopic
argument_list|,
name|TopicMessageStore
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
argument_list|<
name|ActiveMQQueue
argument_list|,
name|MessageStore
argument_list|>
name|queues
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQQueue
argument_list|,
name|MessageStore
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|MessageStore
argument_list|>
name|messageStores
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|MessageStore
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
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
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|String
name|brokerName
decl_stmt|;
specifier|private
name|Store
name|theStore
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
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
name|rc
init|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|Store
name|store
init|=
name|getStore
argument_list|()
decl_stmt|;
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
operator|(
name|ActiveMQDestination
operator|)
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
name|getSubsMapContainer
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
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Store
name|store
init|=
name|getStore
argument_list|()
decl_stmt|;
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
break|break;
block|}
catch|catch
parameter_list|(
name|StoreLockedExcpetion
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Store is locked... waiting "
operator|+
operator|(
name|STORE_LOCKED_WAIT_DELAY
operator|/
literal|1000
operator|)
operator|+
literal|" seconds for the Store to be unlocked."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|STORE_LOCKED_WAIT_DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{                     }
block|}
block|}
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
block|{     }
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
if|if
condition|(
name|theStore
operator|!=
literal|null
condition|)
block|{
name|theStore
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{     }
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|initialize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|theStore
operator|!=
literal|null
condition|)
block|{
name|theStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|theStore
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|theStore
operator|.
name|isInitialized
argument_list|()
condition|)
block|{
name|theStore
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|theStore
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|StoreFactory
operator|.
name|delete
argument_list|(
name|getStoreName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
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
name|Message
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
argument_list|)
decl_stmt|;
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
name|MessageMarshaller
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
return|return
name|container
return|;
block|}
specifier|protected
name|MapContainer
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSubsMapContainer
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
name|String
argument_list|,
name|Object
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
argument_list|)
decl_stmt|;
name|container
operator|.
name|setKeyMarshaller
argument_list|(
name|Store
operator|.
name|StringMarshaller
argument_list|)
expr_stmt|;
name|container
operator|.
name|setValueMarshaller
argument_list|(
name|createMessageMarshaller
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
specifier|protected
name|Marshaller
argument_list|<
name|Object
argument_list|>
name|createMessageMarshaller
parameter_list|()
block|{
return|return
operator|new
name|CommandMarshaller
argument_list|(
name|wireFormat
argument_list|)
return|;
block|}
specifier|protected
name|ListContainer
name|getListContainer
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
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|id
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|container
operator|.
name|setMarshaller
argument_list|(
name|createMessageMarshaller
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
comment|/**      * @param usageManager The UsageManager that is controlling the broker's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{     }
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
comment|/**      * @param maxDataFileLength the maxDataFileLength to set      *       * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      */
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
specifier|protected
specifier|synchronized
name|Store
name|getStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|theStore
operator|==
literal|null
condition|)
block|{
name|theStore
operator|=
name|StoreFactory
operator|.
name|open
argument_list|(
name|getStoreName
argument_list|()
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|theStore
operator|.
name|setMaxDataFileLength
argument_list|(
name|maxDataFileLength
argument_list|)
expr_stmt|;
block|}
return|return
name|theStore
return|;
block|}
specifier|private
name|String
name|getStoreName
parameter_list|()
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|directory
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"KahaPersistenceAdapter("
operator|+
name|getStoreName
argument_list|()
operator|+
literal|")"
return|;
block|}
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
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|brokerName
return|;
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
name|checkpoint
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sync
condition|)
block|{
name|getStore
argument_list|()
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initialize
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|initialized
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|directory
operator|==
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|brokerName
operator|+
literal|"-kahastore"
argument_list|)
expr_stmt|;
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|wireFormat
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wireFormat
operator|.
name|setTightEncodingEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

