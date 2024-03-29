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
name|fanout
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
name|BrokerFactory
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
name|util
operator|.
name|MessageIdList
import|;
end_import

begin_class
specifier|public
class|class
name|FanoutTest
extends|extends
name|TestCase
block|{
name|BrokerService
name|broker1
decl_stmt|;
name|BrokerService
name|broker2
decl_stmt|;
name|ActiveMQConnectionFactory
name|producerFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"fanout:(static:(tcp://localhost:61616,tcp://localhost:61617))?fanOutQueues=true"
argument_list|)
decl_stmt|;
name|Connection
name|producerConnection
decl_stmt|;
name|Session
name|producerSession
decl_stmt|;
name|int
name|messageCount
init|=
literal|100
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker1
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(tcp://localhost:61616)/brokerA?persistent=false&useJmx=false"
argument_list|)
expr_stmt|;
name|broker2
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(tcp://localhost:61617)/brokerB?persistent=false&useJmx=false"
argument_list|)
expr_stmt|;
name|broker1
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker1
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|producerConnection
operator|=
name|producerFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerSession
operator|=
name|producerConnection
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
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|producerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageProducer
name|prod
init|=
name|createProducer
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
literal|"Message "
operator|+
name|i
argument_list|)
decl_stmt|;
name|prod
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|prod
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertMessagesReceived
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|assertMessagesReceived
argument_list|(
literal|"tcp://localhost:61617"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageProducer
name|createProducer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|producerSession
operator|.
name|createProducer
argument_list|(
name|producerSession
operator|.
name|createQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|void
name|assertMessagesReceived
parameter_list|(
name|String
name|brokerURL
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|consumerFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
name|Connection
name|consumerConnection
init|=
name|consumerFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|consumerSession
operator|.
name|createQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|MessageIdList
name|listener
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|waitForMessagesToArrive
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertMessagesReceived
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

