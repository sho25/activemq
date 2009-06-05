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
name|Assert
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
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_class
specifier|public
class|class
name|QueueConsumerPriorityTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VM_BROKER_URL
init|=
literal|"vm://localhost?broker.persistent=false&broker.useJmx=true"
decl_stmt|;
specifier|public
name|QueueConsumerPriorityTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
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
block|}
specifier|private
name|Connection
name|createConnection
parameter_list|(
specifier|final
name|boolean
name|start
parameter_list|)
throws|throws
name|JMSException
block|{
name|ConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|VM_BROKER_URL
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
condition|)
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|conn
return|;
block|}
specifier|public
name|void
name|testQueueConsumerPriority
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|consumerLowPriority
init|=
literal|null
decl_stmt|;
name|Session
name|consumerHighPriority
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|consumerLowPriority
operator|=
name|conn
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
name|consumerHighPriority
operator|=
name|conn
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
name|senderSession
operator|=
name|conn
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
name|String
name|queueName
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ActiveMQQueue
name|low
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
operator|+
literal|"?consumer.priority=1"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|lowConsumer
init|=
name|consumerLowPriority
operator|.
name|createConsumer
argument_list|(
name|low
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|high
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
operator|+
literal|"?consumer.priority=2"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|highConsumer
init|=
name|consumerLowPriority
operator|.
name|createConsumer
argument_list|(
name|high
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"null on iteration: "
operator|+
name|i
argument_list|,
name|highConsumer
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
name|lowConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

