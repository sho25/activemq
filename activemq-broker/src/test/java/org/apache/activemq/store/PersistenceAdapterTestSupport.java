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
package|;
end_package

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

begin_comment
comment|/**  *   * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|PersistenceAdapterTestSupport
extends|extends
name|TestCase
block|{
specifier|protected
name|PersistenceAdapter
name|pa
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|abstract
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|pa
operator|=
name|createPersistenceAdapter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|pa
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testStoreCanHandleDupMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageStore
name|ms
init|=
name|pa
operator|.
name|createQueueMessageStore
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|ms
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|MessageId
name|messageId
init|=
operator|new
name|MessageId
argument_list|(
literal|"ID:localhost-56913-1254499826208-0:0:1:1:1"
argument_list|)
decl_stmt|;
name|messageId
operator|.
name|setBrokerSequenceId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|ms
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
comment|// here comes the dup...
name|message
operator|=
operator|new
name|ActiveMQTextMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|messageId
operator|=
operator|new
name|MessageId
argument_list|(
literal|"ID:localhost-56913-1254499826208-0:0:1:1:1"
argument_list|)
expr_stmt|;
name|messageId
operator|.
name|setBrokerSequenceId
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|ms
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|recovered
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|ms
operator|.
name|recover
argument_list|(
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|recovered
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
name|recovered
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|recovered
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRemoveConsumerDest
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQQueue
name|consumerQ
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.A.VirtualTopicTest"
argument_list|)
decl_stmt|;
name|MessageStore
name|ms
init|=
name|pa
operator|.
name|createQueueMessageStore
argument_list|(
name|consumerQ
argument_list|)
decl_stmt|;
name|pa
operator|.
name|removeQueueMessageStore
argument_list|(
name|consumerQ
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pa
operator|.
name|getDestinations
argument_list|()
operator|.
name|contains
argument_list|(
name|consumerQ
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

