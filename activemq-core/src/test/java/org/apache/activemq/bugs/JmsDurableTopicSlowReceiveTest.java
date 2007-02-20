begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|store
operator|.
name|kahadaptor
operator|.
name|KahaPersistenceAdapter
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
name|JmsDurableTopicSlowReceiveTest
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
name|JmsDurableTopicSlowReceiveTest
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
name|BrokerService
name|broker
decl_stmt|;
specifier|final
name|int
name|NMSG
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|MSIZE
init|=
literal|256000
decl_stmt|;
specifier|private
name|Connection
name|connection3
decl_stmt|;
specifier|private
name|Session
name|consumeSession3
decl_stmt|;
specifier|private
name|TopicSubscriber
name|consumer3
decl_stmt|;
comment|/**      * Set up a durable suscriber test.      *       * @see junit.framework.TestCase#setUp()      */
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
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|result
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prefetchPolicy.durableTopicPrefetch"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prefetchPolicy.optimizeDurableTopicPrefetch"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
comment|//KahaPersistenceAdapter adapter=new KahaPersistenceAdapter(new File("activemq-data/durableTest"));
comment|//JDBCPersistenceAdapter adapter = new JDBCPersistenceAdapter();
comment|// answer.setPersistenceAdapter(adapter);
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test if all the messages sent are being received.      *       * @throws Exception      */
specifier|public
name|void
name|testSlowReceiver
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
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
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
for|for
control|(
name|int
name|loop
init|=
literal|0
init|;
name|loop
operator|<
literal|4
condition|;
name|loop
operator|++
control|)
block|{
name|connection2
operator|=
name|createConnection
argument_list|()
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
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
name|NMSG
operator|/
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|message
init|=
name|session2
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
name|MSIZE
index|]
argument_list|)
expr_stmt|;
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
name|consumerDestination2
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Sent("
operator|+
name|loop
operator|+
literal|"): "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|producer2
operator|.
name|close
argument_list|()
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"SENDER Thread"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection3
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection3
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|connection3
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumeSession3
operator|=
name|connection3
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
name|consumer3
operator|=
name|consumeSession3
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
name|connection3
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|loop
init|=
literal|0
init|;
name|loop
operator|<
literal|4
condition|;
operator|++
name|loop
control|)
block|{
name|connection3
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection3
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|connection3
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumeSession3
operator|=
name|connection3
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
name|consumer3
operator|=
name|consumeSession3
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
literal|null
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
name|NMSG
operator|/
literal|4
condition|;
name|i
operator|++
control|)
block|{
comment|// System.err.println("Receive...");
name|msg
operator|=
name|consumer3
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
break|break;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Received("
operator|+
name|loop
operator|+
literal|"): "
operator|+
name|i
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
name|consumer3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Receiver "
operator|+
name|loop
argument_list|,
name|NMSG
operator|/
literal|4
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// assertEquals(((BytesMessage) msg).getText(), "test");
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
comment|// connection3.stop();
name|connection3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

