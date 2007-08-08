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
name|usecases
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
name|test
operator|.
name|TestSupport
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
name|DeliveryMode
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DurableConsumerCloseAndReconnectTest
extends|extends
name|TestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|long
name|RECEIVE_TIMEOUT
init|=
literal|5000L
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|int
name|messageCount
decl_stmt|;
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
literal|"vm://localhost?broker.deleteAllMessagesOnStartup=false"
argument_list|)
return|;
block|}
specifier|public
name|void
name|testCreateDurableConsumerCloseThenReconnect
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the server to stay up across both connection tests
name|Connection
name|dummyConnection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|dummyConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumeMessagesDeliveredWhileConsumerClosed
argument_list|()
expr_stmt|;
name|dummyConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now lets try again without one connection open
name|consumeMessagesDeliveredWhileConsumerClosed
argument_list|()
expr_stmt|;
comment|//now delete the db
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.deleteAllMessagesOnStartup=true"
argument_list|)
decl_stmt|;
name|dummyConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|dummyConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|dummyConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|consumeMessagesDeliveredWhileConsumerClosed
parameter_list|()
throws|throws
name|Exception
block|{
name|makeConsumer
argument_list|()
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
comment|// wait a few moments for the close to really occur
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Now lets create the consumer again and because we didn't ack, we should get it again"
argument_list|)
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Now lets create the consumer again and because we didn't ack, we should get it again"
argument_list|)
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have no more messages left!"
argument_list|,
name|message
operator|==
literal|null
argument_list|)
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Lets publish one more message now"
argument_list|)
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|publish
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
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
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
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
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"This is a test: "
operator|+
name|messageCount
operator|++
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|=
literal|null
expr_stmt|;
name|closeSession
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|isTopic
argument_list|()
condition|)
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|getSubject
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|getSubject
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|protected
name|boolean
name|isTopic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|closeConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|=
literal|null
expr_stmt|;
name|closeSession
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|closeSession
parameter_list|()
throws|throws
name|JMSException
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|makeConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|durableName
init|=
name|getName
argument_list|()
decl_stmt|;
name|String
name|clientID
init|=
name|getSubject
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating a durable subscribe for clientID: "
operator|+
name|clientID
operator|+
literal|" and durable name: "
operator|+
name|durableName
argument_list|)
expr_stmt|;
name|createSession
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
name|durableName
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|durableName
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Topic
condition|)
block|{
return|return
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|durableName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
block|}
specifier|protected
name|void
name|createSession
parameter_list|(
name|String
name|clientID
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
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
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

