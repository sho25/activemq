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
name|broker
operator|.
name|region
operator|.
name|cursors
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|broker
operator|.
name|region
operator|.
name|DestinationStatistics
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
name|region
operator|.
name|MessageReference
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
name|region
operator|.
name|Queue
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
name|ActiveMQTextMessage
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
name|ConsumerInfo
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
name|usage
operator|.
name|SystemUsage
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
comment|/**  * @author gtully  * https://issues.apache.org/activemq/browse/AMQ-2020  **/
end_comment

begin_class
specifier|public
class|class
name|StoreQueueCursorNoDuplicateTest
extends|extends
name|TestCase
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
name|StoreQueueCursorNoDuplicateTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue-"
operator|+
name|StoreQueueCursorNoDuplicateTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
specifier|final
specifier|static
name|String
name|mesageIdRoot
init|=
literal|"11111:22222:0:"
decl_stmt|;
specifier|final
name|int
name|messageBytesSize
init|=
literal|1024
decl_stmt|;
specifier|final
name|String
name|text
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
name|messageBytesSize
index|]
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|count
init|=
literal|6
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerService
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoDuplicateAfterCacheFullAndReadPast
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
init|=
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
specifier|final
name|MessageStore
name|queueMessageStore
init|=
name|persistenceAdapter
operator|.
name|createQueueMessageStore
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|ConsumerInfo
name|consumerInfo
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
specifier|final
name|DestinationStatistics
name|destinationStatistics
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
name|consumerInfo
operator|.
name|setExclusive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Queue
name|queue
init|=
operator|new
name|Queue
argument_list|(
name|brokerService
argument_list|,
name|destination
argument_list|,
name|queueMessageStore
argument_list|,
name|destinationStatistics
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|queueMessageStore
operator|.
name|start
argument_list|()
expr_stmt|;
name|queueMessageStore
operator|.
name|registerIndexListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|QueueStorePrefetch
name|underTest
init|=
operator|new
name|QueueStorePrefetch
argument_list|(
name|queue
argument_list|,
name|brokerService
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
name|SystemUsage
name|systemUsage
init|=
operator|new
name|SystemUsage
argument_list|()
decl_stmt|;
comment|// ensure memory limit is reached
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|messageBytesSize
operator|*
operator|(
name|count
operator|+
literal|2
operator|)
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|setSystemUsage
argument_list|(
name|systemUsage
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|setEnableAudit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cache enabled"
argument_list|,
name|underTest
operator|.
name|isUseCache
argument_list|()
operator|&&
name|underTest
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ConnectionContext
name|contextNotInTx
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQTextMessage
name|msg
init|=
name|getMessage
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setMemoryUsage
argument_list|(
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|queueMessageStore
operator|.
name|addMessage
argument_list|(
name|contextNotInTx
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|addMessageLast
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"cache is disabled as limit reached"
argument_list|,
operator|!
name|underTest
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|dequeueCount
init|=
literal|0
decl_stmt|;
name|underTest
operator|.
name|setMaxBatchSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|underTest
operator|.
name|hasNext
argument_list|()
operator|&&
name|dequeueCount
operator|<
name|count
condition|)
block|{
name|MessageReference
name|ref
init|=
name|underTest
operator|.
name|next
argument_list|()
decl_stmt|;
name|ref
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|underTest
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: {} with body: {}"
argument_list|,
name|ref
operator|.
name|getMessageId
argument_list|()
argument_list|,
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|ref
operator|.
name|getMessage
argument_list|()
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dequeueCount
operator|++
argument_list|,
name|ref
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|underTest
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|dequeueCount
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ActiveMQTextMessage
name|getMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|MessageId
name|id
init|=
operator|new
name|MessageId
argument_list|(
name|mesageIdRoot
operator|+
name|i
argument_list|)
decl_stmt|;
name|id
operator|.
name|setBrokerSequenceId
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|id
operator|.
name|setProducerSequenceId
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Msg:"
operator|+
name|i
operator|+
literal|" "
operator|+
name|text
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

