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
name|Message
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
name|JMSDurableTopicRedeliverTest
extends|extends
name|JmsTopicRedeliverTest
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
name|JMSDurableTopicRedeliverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|/**      * Sends and consumes the messages.      *       * @throws Exception      */
specifier|public
name|void
name|testRedeliverNewSession
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"TEST: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Message
name|sendMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"About to send a message: "
operator|+
name|sendMessage
operator|+
literal|" with text: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|sendMessage
argument_list|)
expr_stmt|;
comment|// receive but don't acknowledge
name|Message
name|unackMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|unackMessage
argument_list|)
expr_stmt|;
name|String
name|unackId
init|=
name|unackMessage
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|unackMessage
operator|)
operator|.
name|getText
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unackMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unackMessage
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|consumeSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// receive then acknowledge
name|consumeSession
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
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|Message
name|ackMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ackMessage
argument_list|)
expr_stmt|;
name|ackMessage
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|String
name|ackId
init|=
name|ackMessage
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|ackMessage
operator|)
operator|.
name|getText
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ackMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ackMessage
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unackId
argument_list|,
name|ackId
argument_list|)
expr_stmt|;
name|consumeSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumeSession
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
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

