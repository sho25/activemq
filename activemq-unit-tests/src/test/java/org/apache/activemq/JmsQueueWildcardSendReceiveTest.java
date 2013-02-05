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
name|test
operator|.
name|JmsTopicSendReceiveTest
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueWildcardSendReceiveTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
specifier|private
name|String
name|destination1String
init|=
literal|"TEST.ONE.ONE"
decl_stmt|;
specifier|private
name|String
name|destination2String
init|=
literal|"TEST.ONE.ONE.ONE"
decl_stmt|;
specifier|private
name|String
name|destination3String
init|=
literal|"TEST.ONE.TWO"
decl_stmt|;
specifier|private
name|String
name|destination4String
init|=
literal|"TEST.TWO.ONE"
decl_stmt|;
comment|/**      * Sets a test to have a queue destination and non-persistent delivery mode.      *       * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|topic
operator|=
literal|false
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the consumer subject.      *       * @return String - consumer subject      * @see org.apache.activemq.test.TestSupport#getConsumerSubject()      */
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
literal|"FOO.>"
return|;
block|}
comment|/**      * Returns the producer subject.      *       * @return String - producer subject      * @see org.apache.activemq.test.TestSupport#getProducerSubject()      */
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
literal|"FOO.BAR.HUMBUG"
return|;
block|}
specifier|public
name|void
name|testReceiveWildcardQueueEndAsterisk
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQDestination
name|destination1
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination1String
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination3
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination3String
argument_list|)
decl_stmt|;
name|Message
name|m
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
name|String
name|text
init|=
literal|null
decl_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination1
argument_list|,
name|destination1String
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination3
argument_list|,
name|destination3String
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destination6
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.ONE.*"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination6
argument_list|)
expr_stmt|;
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|text
operator|=
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination3String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|text
operator|=
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination3String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReceiveWildcardQueueEndGreaterThan
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQDestination
name|destination1
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination1String
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination2
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination2String
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination3
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination3String
argument_list|)
decl_stmt|;
name|Message
name|m
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
name|String
name|text
init|=
literal|null
decl_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination1
argument_list|,
name|destination1String
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination2
argument_list|,
name|destination2String
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination3
argument_list|,
name|destination3String
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destination7
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.ONE.>"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination7
argument_list|)
expr_stmt|;
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|text
operator|=
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination2String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination3String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination2String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination3String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination2String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination3String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReceiveWildcardQueueMidAsterisk
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQDestination
name|destination1
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination1String
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination4
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|destination4String
argument_list|)
decl_stmt|;
name|Message
name|m
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
name|String
name|text
init|=
literal|null
decl_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination1
argument_list|,
name|destination1String
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|destination4
argument_list|,
name|destination4String
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destination8
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.*.ONE"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination8
argument_list|)
expr_stmt|;
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|text
operator|=
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination4String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|text
operator|=
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|text
operator|.
name|equals
argument_list|(
name|destination1String
argument_list|)
operator|||
name|text
operator|.
name|equals
argument_list|(
name|destination4String
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message:"
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
