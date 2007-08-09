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
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|SimpleTopicTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
comment|// protected String
comment|// bindAddress="tcp://localhost:61616?wireFormat.cacheEnabled=true&wireFormat.tightEncodingEnabled=true&jms.useAsyncSend=false";
comment|// protected String bindAddress="tcp://localhost:61616";
specifier|protected
name|String
name|bindAddress
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
comment|// protected String bindAddress="vm://localhost?marshal=true";
comment|// protected String bindAddress="vm://localhost";
specifier|protected
name|PerfProducer
index|[]
name|producers
decl_stmt|;
specifier|protected
name|PerfConsumer
index|[]
name|consumers
decl_stmt|;
specifier|protected
name|String
name|destinationName
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|samepleCount
init|=
literal|10
decl_stmt|;
specifier|protected
name|long
name|sampleInternal
init|=
literal|1000
decl_stmt|;
specifier|protected
name|int
name|numberOfConsumers
init|=
literal|10
decl_stmt|;
specifier|protected
name|int
name|numberofProducers
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|playloadSize
init|=
literal|1024
decl_stmt|;
specifier|protected
name|byte
index|[]
name|array
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|long
name|consumerSleepDuration
decl_stmt|;
comment|/**      * Sets up a test where the producer and consumer have their own connection.      *       * @see junit.framework.TestCase#setUp()      */
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
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|Connection
name|con
init|=
name|factory
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
name|log
operator|.
name|info
argument_list|(
literal|"Testing against destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|log
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
name|factory
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
name|factory
argument_list|,
name|destination
argument_list|,
name|i
argument_list|,
name|array
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setUp
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
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|s
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
comment|/**      * Factory method to create a new broker      *       * @throws Exception      */
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
name|PerfProducer
name|createProducer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|number
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|PerfProducer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
name|payload
argument_list|)
return|;
block|}
specifier|protected
name|PerfConsumer
name|createConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|number
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|PerfConsumer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|)
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
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|bindAddress
argument_list|)
return|;
block|}
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
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
name|start
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
name|start
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Sampling performance "
operator|+
name|samepleCount
operator|+
literal|" times at a "
operator|+
name|sampleInternal
operator|+
literal|" ms interval."
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
name|samepleCount
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sampleInternal
argument_list|)
expr_stmt|;
name|dumpProducerRate
argument_list|()
expr_stmt|;
name|dumpConsumerRate
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
name|stop
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
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|dumpProducerRate
parameter_list|()
block|{
name|int
name|totalRate
init|=
literal|0
decl_stmt|;
name|int
name|totalCount
init|=
literal|0
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
name|producers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PerfRate
name|rate
init|=
name|producers
index|[
name|i
index|]
operator|.
name|getRate
argument_list|()
operator|.
name|cloneAndReset
argument_list|()
decl_stmt|;
name|totalRate
operator|+=
name|rate
operator|.
name|getRate
argument_list|()
expr_stmt|;
name|totalCount
operator|+=
name|rate
operator|.
name|getTotalCount
argument_list|()
expr_stmt|;
block|}
name|int
name|avgRate
init|=
name|totalRate
operator|/
name|producers
operator|.
name|length
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Avg producer rate = "
operator|+
name|avgRate
operator|+
literal|" msg/sec | Total rate = "
operator|+
name|totalRate
operator|+
literal|", sent = "
operator|+
name|totalCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|dumpConsumerRate
parameter_list|()
block|{
name|int
name|totalRate
init|=
literal|0
decl_stmt|;
name|int
name|totalCount
init|=
literal|0
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
name|consumers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PerfRate
name|rate
init|=
name|consumers
index|[
name|i
index|]
operator|.
name|getRate
argument_list|()
operator|.
name|cloneAndReset
argument_list|()
decl_stmt|;
name|totalRate
operator|+=
name|rate
operator|.
name|getRate
argument_list|()
expr_stmt|;
name|totalCount
operator|+=
name|rate
operator|.
name|getTotalCount
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|consumers
operator|!=
literal|null
operator|&&
name|consumers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|avgRate
init|=
name|totalRate
operator|/
name|consumers
operator|.
name|length
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Avg consumer rate = "
operator|+
name|avgRate
operator|+
literal|" msg/sec | Total rate = "
operator|+
name|totalRate
operator|+
literal|", received = "
operator|+
name|totalCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

