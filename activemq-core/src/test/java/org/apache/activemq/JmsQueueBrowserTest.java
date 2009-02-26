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
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueBrowser
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
name|ActiveMQQueue
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueBrowserTest
extends|extends
name|JmsTestSupport
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
name|LOG
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
name|JmsQueueBrowserTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Tests the queue browser. Browses the messages then the consumer tries to receive them. The messages should still      * be in the queue even when it was browsed.      *      * @throws Exception      */
specifier|public
name|void
name|testReceiveBrowseReceive
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Message
index|[]
name|outbound
init|=
operator|new
name|Message
index|[]
block|{
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Third Message"
argument_list|)
block|}
decl_stmt|;
comment|// lets consume any outstanding messages from previous test runs
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// Get the first.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Thread.sleep(200);
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
operator|(
name|Queue
operator|)
name|destination
argument_list|)
decl_stmt|;
name|Enumeration
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
comment|// browse the second
name|assertTrue
argument_list|(
literal|"should have received the second message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|,
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// browse the third.
name|assertTrue
argument_list|(
literal|"Should have received the third message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|,
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// There should be no more.
name|boolean
name|tooMany
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got extra message: "
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|tooMany
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tooMany
argument_list|)
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Re-open the consumer.
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Receive the second.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|1
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Receive the third.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|2
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testBrowseReceive
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Message
index|[]
name|outbound
init|=
operator|new
name|Message
index|[]
block|{
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second Message"
argument_list|)
block|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Third Message"
argument_list|)
block|}
decl_stmt|;
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
name|outbound
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// create browser first
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
operator|(
name|Queue
operator|)
name|destination
argument_list|)
decl_stmt|;
name|Enumeration
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
comment|// create consumer
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// browse the first message
name|assertTrue
argument_list|(
literal|"should have received the first message"
argument_list|,
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
comment|// Receive the first message.
name|assertEquals
argument_list|(
name|outbound
index|[
literal|0
index|]
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|browser
operator|.
name|close
argument_list|()
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

