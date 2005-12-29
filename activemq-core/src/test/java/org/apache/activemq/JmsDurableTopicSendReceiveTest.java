begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|test
operator|.
name|JmsTopicSendReceiveTest
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsDurableTopicSendReceiveTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsDurableTopicSendReceiveTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection2
decl_stmt|;
specifier|protected
name|Session
name|session2
decl_stmt|;
specifier|protected
name|Session
name|consumeSession2
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer2
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer2
decl_stmt|;
specifier|protected
name|Destination
name|consumerDestination2
decl_stmt|;
specifier|protected
name|Destination
name|producerDestination2
decl_stmt|;
comment|/**      * Set up a durable suscriber test.      *      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|durable
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test if all the messages sent are being received.      *      * @throws Exception      */
specifier|public
name|void
name|testSendWhileClosed
parameter_list|()
throws|throws
name|Exception
block|{
name|connection2
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|start
argument_list|()
expr_stmt|;
name|session2
operator|=
name|connection2
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
name|producer2
operator|=
name|session2
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|producer2
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|producerDestination2
operator|=
name|session2
operator|.
name|createTopic
argument_list|(
name|getProducerSubject
argument_list|()
operator|+
literal|"2"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|consumeSession2
operator|=
name|connection2
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
name|consumerDestination2
operator|=
name|session2
operator|.
name|createTopic
argument_list|(
name|getConsumerSubject
argument_list|()
operator|+
literal|"2"
argument_list|)
expr_stmt|;
name|consumer2
operator|=
name|consumeSession2
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|consumerDestination2
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|consumer2
operator|.
name|close
argument_list|()
expr_stmt|;
name|TextMessage
name|message
init|=
name|session2
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|producer2
operator|.
name|send
argument_list|(
name|producerDestination2
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating durable consumer"
argument_list|)
expr_stmt|;
name|consumer2
operator|=
name|consumeSession2
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|consumerDestination2
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|.
name|getJMSType
argument_list|()
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|.
name|getStringProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

