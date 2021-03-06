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
name|JmsMultipleBrokersTestSupport
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
name|jmx
operator|.
name|BrokerView
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
name|Wait
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
name|AMQ2439Test
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
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
name|AMQ2439Test
operator|.
name|class
argument_list|)
decl_stmt|;
name|Destination
name|dest
decl_stmt|;
specifier|public
name|void
name|testDuplicatesThroughNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"received expected amount"
argument_list|,
literal|500
argument_list|,
name|receiveExactMessages
argument_list|(
literal|"BrokerB"
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"received expected amount"
argument_list|,
literal|500
argument_list|,
name|receiveExactMessages
argument_list|(
literal|"BrokerB"
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|validateQueueStats
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|validateQueueStats
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerView
name|brokerView
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerA"
argument_list|)
operator|.
name|broker
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"enequeue is correct"
argument_list|,
literal|1000
argument_list|,
name|brokerView
operator|.
name|getTotalEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dequeue is correct"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"dequeue count (want 1000), is : "
operator|+
name|brokerView
operator|.
name|getTotalDequeueCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1000
operator|==
name|brokerView
operator|.
name|getTotalDequeueCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|int
name|receiveExactMessages
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|int
name|msgCount
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerItem
name|brokerItem
init|=
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|brokerItem
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
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Message
name|msg
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|msgCount
condition|;
name|i
operator|++
control|)
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
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|brokerItem
operator|.
name|connections
operator|.
name|remove
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
specifier|public
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
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616)/BrokerA?persistent=true&deleteAllMessagesOnStartup=true&advisorySupport=false"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61617)/BrokerB?persistent=true&deleteAllMessagesOnStartup=true&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Create queue
name|dest
operator|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

