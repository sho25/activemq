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
name|network
operator|.
name|NetworkConnector
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
name|SimpleNetworkTest
extends|extends
name|SimpleTopicTest
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
name|SimpleNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|consumerBindAddress
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|protected
name|String
name|producerBindAddress
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|CONSUMER_BROKER_NAME
init|=
literal|"Consumer"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|PRODUCER_BROKER_NAME
init|=
literal|"Producer"
decl_stmt|;
specifier|protected
name|BrokerService
name|consumerBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|producerBroker
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|consumerFactory
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|producerFactory
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumerBroker
operator|==
literal|null
condition|)
block|{
name|consumerBroker
operator|=
name|createConsumerBroker
argument_list|(
name|consumerBindAddress
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|producerBroker
operator|==
literal|null
condition|)
block|{
name|producerBroker
operator|=
name|createProducerBroker
argument_list|(
name|producerBindAddress
argument_list|)
expr_stmt|;
block|}
name|consumerFactory
operator|=
name|createConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|CONSUMER_BROKER_NAME
argument_list|)
expr_stmt|;
name|producerFactory
operator|=
name|createConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|PRODUCER_BROKER_NAME
argument_list|)
expr_stmt|;
comment|//consumerFactory = createConnectionFactory(consumerBindAddress);
comment|//producerFactory = createConnectionFactory(producerBindAddress);
name|Connection
name|con
init|=
name|consumerFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing against destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|numberofProducers
operator|+
literal|" producer(s) and "
operator|+
name|numberOfConsumers
operator|+
literal|" consumer(s)"
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|producers
operator|=
operator|new
name|PerfProducer
index|[
name|numberofProducers
index|]
expr_stmt|;
name|consumers
operator|=
operator|new
name|PerfConsumer
index|[
name|numberOfConsumers
index|]
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
name|numberOfConsumers
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|=
name|createConsumer
argument_list|(
name|consumerFactory
argument_list|,
name|destination
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|setSleepDuration
argument_list|(
name|consumerSleepDuration
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberofProducers
condition|;
name|i
operator|++
control|)
block|{
name|array
operator|=
operator|new
name|byte
index|[
name|playloadSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|array
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|array
index|[
name|j
index|]
operator|=
operator|(
name|byte
operator|)
name|j
expr_stmt|;
block|}
name|producers
index|[
name|i
index|]
operator|=
name|createProducer
argument_list|(
name|producerFactory
argument_list|,
name|destination
argument_list|,
name|i
argument_list|,
name|array
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
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
name|numberOfConsumers
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|.
name|shutDown
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberofProducers
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|.
name|shutDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|producerBroker
operator|!=
literal|null
condition|)
block|{
name|producerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|producerBroker
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|consumerBroker
operator|!=
literal|null
condition|)
block|{
name|consumerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|consumerBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createConsumerBroker
parameter_list|(
name|String
name|uri
parameter_list|)
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
name|configureConsumerBroker
argument_list|(
name|answer
argument_list|,
name|uri
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
name|configureConsumerBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
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
name|setBrokerName
argument_list|(
name|CONSUMER_BROKER_NAME
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createProducerBroker
parameter_list|(
name|String
name|uri
parameter_list|)
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
name|configureProducerBroker
argument_list|(
name|answer
argument_list|,
name|uri
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
name|configureProducerBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setBrokerName
argument_list|(
name|PRODUCER_BROKER_NAME
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NetworkConnector
name|connector
init|=
name|answer
operator|.
name|addNetworkConnector
argument_list|(
literal|"static://"
operator|+
name|consumerBindAddress
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

