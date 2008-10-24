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
name|advisory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|EmbeddedBrokerTestSupport
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
name|RegionBroker
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

begin_comment
comment|/**  * @version $Revision: 397249 $  */
end_comment

begin_class
specifier|public
class|class
name|TempQueueMemoryTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|protected
name|Connection
name|serverConnection
decl_stmt|;
specifier|protected
name|Session
name|serverSession
decl_stmt|;
specifier|protected
name|Connection
name|clientConnection
decl_stmt|;
specifier|protected
name|Session
name|clientSession
decl_stmt|;
specifier|protected
name|Destination
name|serverDestination
decl_stmt|;
specifier|protected
name|int
name|messagesToSend
init|=
literal|2000
decl_stmt|;
specifier|protected
name|boolean
name|deleteTempQueue
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|serverTransactional
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|clientTransactional
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|numConsumers
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|numProducers
init|=
literal|1
decl_stmt|;
specifier|public
name|void
name|testLoadRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|serverSession
operator|.
name|createConsumer
argument_list|(
name|serverDestination
argument_list|)
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
try|try
block|{
name|Destination
name|replyTo
init|=
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|serverSession
operator|.
name|createProducer
argument_list|(
name|replyTo
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|replyTo
argument_list|,
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|serverTransactional
condition|)
block|{
name|serverSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
class|class
name|Producer
extends|extends
name|Thread
block|{
specifier|private
name|int
name|numToSend
decl_stmt|;
specifier|public
name|Producer
parameter_list|(
name|int
name|numToSend
parameter_list|)
block|{
name|this
operator|.
name|numToSend
operator|=
name|numToSend
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MessageProducer
name|producer
decl_stmt|;
try|try
block|{
name|producer
operator|=
name|clientSession
operator|.
name|createProducer
argument_list|(
name|serverDestination
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numToSend
condition|;
name|i
operator|++
control|)
block|{
name|TemporaryQueue
name|replyTo
init|=
name|clientSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|clientSession
operator|.
name|createConsumer
argument_list|(
name|replyTo
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|clientSession
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
name|replyTo
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientTransactional
condition|)
block|{
name|clientSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|Message
name|reply
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientTransactional
condition|)
block|{
name|clientSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|deleteTempQueue
condition|)
block|{
name|replyTo
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// temp queue will be cleaned up on clientConnection.close
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Vector
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|Vector
argument_list|<
name|Thread
argument_list|>
argument_list|(
name|numProducers
argument_list|)
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
name|numProducers
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Producer
argument_list|(
name|messagesToSend
operator|/
name|numProducers
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startAndJoinThreads
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|clientSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|serverSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|clientConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|serverConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|AdvisoryBroker
name|ab
init|=
operator|(
name|AdvisoryBroker
operator|)
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|AdvisoryBroker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|///The server destination will be left
name|assertTrue
argument_list|(
name|ab
operator|.
name|getAdvisoryDestinations
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be zero but is "
operator|+
name|ab
operator|.
name|getAdvisoryConsumers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|ab
operator|.
name|getAdvisoryConsumers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be zero but is "
operator|+
name|ab
operator|.
name|getAdvisoryProducers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|ab
operator|.
name|getAdvisoryProducers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|RegionBroker
name|rb
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|RegionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//serverDestination +
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|rb
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startAndJoinThreads
parameter_list|(
name|Vector
argument_list|<
name|Thread
argument_list|>
name|threads
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|serverConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|serverConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|serverSession
operator|=
name|serverConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|clientConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|clientConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientSession
operator|=
name|clientConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|serverDestination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
block|}
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
name|serverTransactional
operator|=
name|clientTransactional
operator|=
literal|false
expr_stmt|;
name|numConsumers
operator|=
name|numProducers
operator|=
literal|1
expr_stmt|;
name|messagesToSend
operator|=
literal|2000
expr_stmt|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

