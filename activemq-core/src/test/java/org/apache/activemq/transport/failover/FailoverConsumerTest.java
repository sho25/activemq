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
name|transport
operator|.
name|failover
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|ActiveMQPrefetchPolicy
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
name|network
operator|.
name|NetworkTestSupport
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

begin_class
specifier|public
class|class
name|FailoverConsumerTest
extends|extends
name|NetworkTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MSG_COUNT
init|=
literal|100
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
name|FailoverConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testPublisherFailsOver
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Uncomment this if you want to use remote broker created by
comment|// NetworkTestSupport.
comment|// But it doesn't work. See comments below.
comment|// URI failoverURI = new
comment|// URI("failover://"+remoteConnector.getServer().getConnectURI());
name|URI
name|failoverURI
init|=
operator|new
name|URI
argument_list|(
literal|"failover://tcp://localhost:61616"
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|failoverURI
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
comment|// Prefetch size must be less than messages in the queue!!
name|prefetchPolicy
operator|.
name|setQueuePrefetch
argument_list|(
name|MSG_COUNT
operator|-
literal|10
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|MSG_COUNT
condition|;
operator|++
name|idx
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
comment|// restartRemoteBroker() doesn't work (you won't get received any
comment|// messages
comment|// after restart, javadoc says, that messages should be received
comment|// though).
comment|// So we must use external broker ant restart it manually.
name|LOG
operator|.
name|info
argument_list|(
literal|"You should restart remote broker now and press enter!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// Thread.sleep(20000);
name|restartRemoteBroker
argument_list|()
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
operator|++
name|count
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|1
init|;
name|idx
operator|<
name|MSG_COUNT
condition|;
operator|++
name|idx
control|)
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No messages received! Received:"
operator|+
name|count
argument_list|)
expr_stmt|;
break|break;
block|}
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|MSG_COUNT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|consumerSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
do|do
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
block|}
do|while
condition|(
name|msg
operator|!=
literal|null
condition|)
do|;
name|assertEquals
argument_list|(
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession
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
specifier|protected
name|String
name|getRemoteURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:55555"
return|;
block|}
block|}
end_class

end_unit

