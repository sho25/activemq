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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|ExceptionListener
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IdGenerator
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|TopicRedeliverTest
extends|extends
name|TestSupport
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
name|TopicRedeliverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|RECEIVE_TIMEOUT
init|=
literal|10000
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|private
name|IdGenerator
name|idGen
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|public
name|TopicRedeliverTest
parameter_list|()
block|{     }
specifier|public
name|TopicRedeliverTest
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|super
argument_list|(
name|n
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
name|topic
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * test messages are acknowledged and recovered properly      *       * @throws Exception      */
specifier|public
name|void
name|testClientAcknowledge
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
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
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Session
name|producerSession
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
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
comment|// send some messages
name|TextMessage
name|sent1
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sent1
operator|.
name|setText
argument_list|(
literal|"msg1"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sent1
argument_list|)
expr_stmt|;
name|TextMessage
name|sent2
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sent1
operator|.
name|setText
argument_list|(
literal|"msg2"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sent2
argument_list|)
expr_stmt|;
name|TextMessage
name|sent3
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sent1
operator|.
name|setText
argument_list|(
literal|"msg3"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sent3
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|Message
name|rec2
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
comment|// ack rec2
name|rec2
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|TextMessage
name|sent4
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sent4
operator|.
name|setText
argument_list|(
literal|"msg4"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sent4
argument_list|)
expr_stmt|;
name|Message
name|rec4
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
name|rec4
operator|.
name|equals
argument_list|(
name|sent4
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|recover
argument_list|()
expr_stmt|;
name|rec4
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
name|rec4
operator|.
name|equals
argument_list|(
name|sent4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rec4
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|rec4
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test redelivered flag is set on rollbacked transactions      *       * @throws Exception      */
specifier|public
name|void
name|testRedilveredFlagSetOnRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"TESTRED"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|TextMessage
name|sentMsg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg1"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Message
name|recMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|recMsg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|recMsg
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
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|equals
argument_list|(
name|sentMsg
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoExceptionOnRedeliveryAckWithSimpleTopicConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotException
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"unexpected ex:"
operator|+
name|exception
argument_list|)
expr_stmt|;
name|gotException
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|TextMessage
name|sentMsg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg1"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Message
name|recMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|recMsg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|recMsg
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
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|recMsg
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
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|equals
argument_list|(
name|sentMsg
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"no exception"
argument_list|,
name|gotException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check a session is rollbacked on a Session close();      *       * @throws Exception      */
specifier|public
name|void
name|xtestTransactionRollbackOnSessionClose
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"TESTRED"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|TextMessage
name|sentMsg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg1"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Message
name|recMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|recMsg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|recMsg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|equals
argument_list|(
name|sentMsg
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * check messages are actuallly sent on a tx rollback      *       * @throws Exception      */
specifier|public
name|void
name|testTransactionRollbackOnSend
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|TextMessage
name|sentMsg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg1"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Message
name|recMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|recMsg
operator|.
name|equals
argument_list|(
name|sentMsg
argument_list|)
argument_list|)
expr_stmt|;
name|sentMsg
operator|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg2"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|sentMsg
operator|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|sentMsg
operator|.
name|setText
argument_list|(
literal|"msg3"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMsg
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|recMsg
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
name|recMsg
operator|.
name|equals
argument_list|(
name|sentMsg
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

