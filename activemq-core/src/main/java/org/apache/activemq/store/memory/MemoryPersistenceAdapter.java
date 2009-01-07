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
name|memory
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
name|ProxyMessageStore
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
comment|/**  * @org.apache.xbean.XBean  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|MemoryPersistenceAdapter
implements|implements
name|PersistenceAdapter
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
name|MemoryPersistenceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
name|MemoryTransactionStore
name|transactionStore
decl_stmt|;
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|TopicMessageStore
argument_list|>
name|topics
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|TopicMessageStore
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|MessageStore
argument_list|>
name|queues
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
specifier|private
name|boolean
name|useExternalMessageReferences
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
argument_list|(
name|queues
operator|.
name|size
argument_list|()
operator|+
name|topics
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ActiveMQDestination
argument_list|>
name|iter
init|=
name|queues
operator|.
name|keySet
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
name|rc
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ActiveMQDestination
argument_list|>
name|iter
init|=
name|topics
operator|.
name|keySet
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
name|rc
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
specifier|static
name|MemoryPersistenceAdapter
name|newInstance
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
operator|new
name|MemoryPersistenceAdapter
argument_list|()
return|;
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
name|MemoryMessageStore
argument_list|(
name|destination
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
name|rc
operator|=
operator|new
name|MemoryTopicMessageStore
argument_list|(
name|destination
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
comment|/**      * Cleanup method to remove any state associated with the given destination      *      * @param destination Destination to forget      */
specifier|public
name|void
name|removeQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
block|{
name|queues
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * Cleanup method to remove any state associated with the given destination      *      * @param destination Destination to forget      */
specifier|public
name|void
name|removeTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
block|{
name|topics
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
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
name|transactionStore
operator|=
operator|new
name|MemoryTransactionStore
argument_list|(
name|this
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
block|{     }
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{     }
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
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
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
for|for
control|(
name|Iterator
argument_list|<
name|TopicMessageStore
argument_list|>
name|iter
init|=
name|topics
operator|.
name|values
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
name|MemoryMessageStore
name|store
init|=
name|asMemoryMessageStore
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
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
for|for
control|(
name|Iterator
argument_list|<
name|MessageStore
argument_list|>
name|iter
init|=
name|queues
operator|.
name|values
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
name|MemoryMessageStore
name|store
init|=
name|asMemoryMessageStore
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|transactionStore
operator|!=
literal|null
condition|)
block|{
name|transactionStore
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
name|MemoryMessageStore
name|asMemoryMessageStore
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|MemoryMessageStore
condition|)
block|{
return|return
operator|(
name|MemoryMessageStore
operator|)
name|value
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|ProxyMessageStore
condition|)
block|{
name|MessageStore
name|delegate
init|=
operator|(
operator|(
name|ProxyMessageStore
operator|)
name|value
operator|)
operator|.
name|getDelegate
argument_list|()
decl_stmt|;
if|if
condition|(
name|delegate
operator|instanceof
name|MemoryMessageStore
condition|)
block|{
return|return
operator|(
name|MemoryMessageStore
operator|)
name|delegate
return|;
block|}
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Expected an instance of MemoryMessageStore but was: "
operator|+
name|value
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**      * @param usageManager The UsageManager that is controlling the broker's      *                memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{     }
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MemoryPersistenceAdapter"
return|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{     }
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{     }
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

