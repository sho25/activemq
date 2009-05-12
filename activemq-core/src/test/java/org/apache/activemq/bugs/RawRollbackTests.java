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
name|bugs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|ActiveMQConnectionFactory
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
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|RawRollbackTests
block|{
specifier|private
specifier|static
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
specifier|static
name|Destination
name|queue
decl_stmt|;
specifier|private
specifier|static
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|clean
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|connectionFactory
operator|.
name|setBrokerURL
argument_list|(
literal|"vm://localhost?async=false&waitForStart=5000&jms.prefetchPolicy.all=0"
argument_list|)
expr_stmt|;
name|RawRollbackTests
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
name|queue
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|clearData
parameter_list|()
throws|throws
name|Exception
block|{
name|getMessages
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// drain queue
name|convertAndSend
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|convertAndSend
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|checkPostConditions
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000L
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|getMessages
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReceiveMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|getMessages
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|convertAndSend
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getMessages
parameter_list|(
name|boolean
name|rollback
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|String
name|next
init|=
literal|""
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|msgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|=
operator|(
name|String
operator|)
name|receiveAndConvert
argument_list|(
name|session
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|msgs
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rollback
condition|)
block|{
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|msgs
return|;
block|}
specifier|private
name|String
name|receiveAndConvert
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|100L
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
return|;
block|}
block|}
end_class

end_unit

