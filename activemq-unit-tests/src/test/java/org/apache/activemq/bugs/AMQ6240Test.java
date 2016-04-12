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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnection
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
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|PolicyMap
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
name|ActiveMQDestination
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
name|transport
operator|.
name|RequestTimedOutIOException
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ6240Test
extends|extends
name|JmsTimeoutTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ6240Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|testBlockedTxProducerConnectionTimeoutConnectionCanClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnection
name|cx
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQDestination
name|queue
init|=
name|createDestination
argument_list|(
literal|"noPfc"
argument_list|)
decl_stmt|;
comment|// we should not take longer than 10 seconds to return from send
name|cx
operator|.
name|setSendTimeout
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sender thread starting"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|cx
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|messageCount
condition|;
name|count
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done sending.."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RequestTimedOutIOException
condition|)
block|{
name|exceptionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
decl_stmt|;
name|cx
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producerThread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerThread
operator|.
name|join
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|cx
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We should have a few timeout exceptions as memory store will fill up
name|assertTrue
argument_list|(
literal|"No exception from the broker"
argument_list|,
name|exceptionCount
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
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
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|noProducerFlowControl
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|noProducerFlowControl
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"noPfc"
argument_list|)
argument_list|,
name|noProducerFlowControl
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|50
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

