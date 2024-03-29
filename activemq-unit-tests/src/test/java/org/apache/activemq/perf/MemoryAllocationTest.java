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
name|perf
package|;
end_package

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
name|JMSException
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
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|MemoryAllocationTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|2000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MemoryAllocationTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|bindAddress
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|protected
name|int
name|topicCount
decl_stmt|;
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|session
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|MessageProducer
name|mp
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|mp
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|releaseDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Iterator "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Destination
name|getDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|topicName
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|topicCount
operator|++
decl_stmt|;
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
return|;
block|}
specifier|protected
name|void
name|releaseDestination
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|dest
operator|instanceof
name|TemporaryTopic
condition|)
block|{
name|TemporaryTopic
name|tt
init|=
operator|(
name|TemporaryTopic
operator|)
name|dest
decl_stmt|;
name|tt
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dest
operator|instanceof
name|TemporaryQueue
condition|)
block|{
name|TemporaryQueue
name|tq
init|=
operator|(
name|TemporaryQueue
operator|)
name|dest
decl_stmt|;
name|tq
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|setUp
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|bindAddress
argument_list|)
decl_stmt|;
return|return
name|cf
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

