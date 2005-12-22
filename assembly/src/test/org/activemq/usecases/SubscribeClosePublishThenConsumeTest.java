begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
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
name|Message
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_comment
comment|/**  * @author Paul Smith  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SubscribeClosePublishThenConsumeTest
extends|extends
name|TestSupport
block|{
specifier|public
name|void
name|testDurableTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://locahost"
argument_list|)
decl_stmt|;
name|String
name|topicName
init|=
literal|"TestTopic"
decl_stmt|;
name|String
name|clientID
init|=
name|getName
argument_list|()
decl_stmt|;
name|String
name|subscriberName
init|=
literal|"MySubscriber:"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
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
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
comment|// this should register a durable subscriber, we then close it to
comment|// test that we get messages from the producer later on
name|TopicSubscriber
name|subscriber
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subscriberName
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|topic
operator|=
literal|null
expr_stmt|;
name|subscriber
operator|.
name|close
argument_list|()
expr_stmt|;
name|subscriber
operator|=
literal|null
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
comment|// Create the new connection before closing to avoid the broker shutting down.
comment|// now create a new Connection, Session&  Producer, send some messages& then close
name|Connection
name|t
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
name|t
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|topic
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
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
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello World"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|textMessage
operator|=
literal|null
expr_stmt|;
name|topic
operator|=
literal|null
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
comment|// Now (re)register the Durable subscriber, setup a listener and wait for messages that should
comment|// have been published by the previous producer
name|t
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
name|t
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|topic
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
name|subscriber
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Started connection - now about to try receive the textMessage"
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Message
name|message
init|=
name|subscriber
operator|.
name|receive
argument_list|(
literal|15000L
argument_list|)
decl_stmt|;
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Waited for: "
operator|+
name|elapsed
operator|+
literal|" millis"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received the message we published by now"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be text textMessage"
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|textMessage
operator|=
operator|(
name|TextMessage
operator|)
name|message
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello World"
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

