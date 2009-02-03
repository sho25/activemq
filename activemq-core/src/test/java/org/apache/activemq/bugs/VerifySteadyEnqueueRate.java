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
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|CountDownLatch
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
name|ObjectMessage
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
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|amq
operator|.
name|AMQPersistenceAdapter
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
name|AMQPersistenceAdapterFactory
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
name|VerifySteadyEnqueueRate
extends|extends
name|TestCase
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
name|VerifySteadyEnqueueRate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|max_messages
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|max_messages
init|=
literal|10000000
decl_stmt|;
specifier|private
specifier|static
name|int
name|messageCounter
decl_stmt|;
specifier|private
name|String
name|destinationName
init|=
name|getName
argument_list|()
operator|+
literal|"_Queue"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|Connection
name|receiverConnection
decl_stmt|;
specifier|private
name|Connection
name|producerConnection
decl_stmt|;
specifier|final
name|boolean
name|useTopic
init|=
literal|false
decl_stmt|;
name|AMQPersistenceAdapter
name|persistentAdapter
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|24
index|]
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|messageCounter
operator|=
literal|0
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
name|receiverConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|receiverConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|receiverConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testForDataFileNotDeleted
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|true
condition|)
block|{
return|return;
block|}
name|doTestForDataFileNotDeleted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestForDataFileNotDeleted
parameter_list|(
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|min
init|=
literal|100
decl_stmt|;
name|long
name|max
init|=
literal|0
decl_stmt|;
name|long
name|reportTime
init|=
literal|0
decl_stmt|;
name|Receiver
name|receiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|messageCounter
operator|++
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|//buildReceiver(receiverConnection, destinationName, transacted, receiver, useTopic);
specifier|final
name|MessageSender
name|producer
init|=
operator|new
name|MessageSender
argument_list|(
name|destinationName
argument_list|,
name|producerConnection
argument_list|,
name|transacted
argument_list|,
name|useTopic
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
name|max_messages
condition|;
name|i
operator|++
control|)
block|{
name|long
name|startT
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|long
name|endT
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
name|endT
operator|-
name|startT
decl_stmt|;
if|if
condition|(
name|duration
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|duration
expr_stmt|;
block|}
if|if
condition|(
name|duration
operator|>
name|min
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|DateFormat
operator|.
name|getTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|startT
argument_list|)
argument_list|)
operator|+
literal|" at message "
operator|+
name|i
operator|+
literal|" send time="
operator|+
name|duration
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"max = "
operator|+
name|max
argument_list|)
expr_stmt|;
comment|//latch.await();
comment|//assertEquals(max_messages, messageCounter);
comment|//waitFordataFilesToBeCleanedUp(persistentAdapter.getAsyncDataManager(), 30000, 2);
block|}
specifier|private
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|private
name|void
name|startBroker
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
name|setPersistent
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
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|AMQPersistenceAdapterFactory
name|factory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
comment|// ensure there are a bunch of data files but multiple entries in each
comment|//factory.setMaxFileLength(1024 * 20);
comment|// speed up the test case, checkpoint an cleanup early and often
comment|//factory.setCheckpointInterval(500);
name|factory
operator|.
name|setCleanupInterval
argument_list|(
literal|1000
operator|*
literal|60
operator|*
literal|30
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setSyncOnWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//int indexBinSize=262144; // good for 6M
name|int
name|indexBinSize
init|=
literal|1024
decl_stmt|;
name|factory
operator|.
name|setIndexMaxBinSize
argument_list|(
name|indexBinSize
operator|*
literal|2
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIndexBinSize
argument_list|(
name|indexBinSize
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIndexPageSize
argument_list|(
literal|192
operator|*
literal|20
argument_list|)
expr_stmt|;
name|persistentAdapter
operator|=
operator|(
name|AMQPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker.."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|buildReceiver
parameter_list|(
name|Connection
name|connection
parameter_list|,
specifier|final
name|String
name|queueName
parameter_list|,
name|boolean
name|transacted
parameter_list|,
specifier|final
name|Receiver
name|receiver
parameter_list|,
name|boolean
name|isTopic
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Session
name|session
init|=
name|transacted
condition|?
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
else|:
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
name|MessageConsumer
name|inputMessageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|isTopic
condition|?
name|session
operator|.
name|createTopic
argument_list|(
name|queueName
argument_list|)
else|:
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
name|MessageListener
name|messageListener
init|=
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|String
name|s
init|=
operator|(
name|String
operator|)
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|receive
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|inputMessageConsumer
operator|.
name|setMessageListener
argument_list|(
name|messageListener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

