begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
comment|/**  * Test cases used to test the JMS message exclusive consumers.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|RedeliveryPolicyTest
extends|extends
name|JmsTestSupport
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|RedeliveryPolicyTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws Exception      */
specifier|public
name|void
name|testExponentialRedeliveryPolicyDelaysDeliveryOnRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Receive a message with the JMS API
name|RedeliveryPolicy
name|policy
init|=
name|connection
operator|.
name|getRedeliveryPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setBackOffMultiplier
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setUseExponentialBackOff
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
literal|true
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
comment|// Send the messages
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1st"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"2nd"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TextMessage
name|m
decl_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// No delay on first rollback..
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Show subsequent re-delivery delay is incrementing.
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Show re-delivery delay is incrementing exponentially
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws Exception      */
specifier|public
name|void
name|testNornalRedeliveryPolicyDelaysDeliveryOnRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Receive a message with the JMS API
name|RedeliveryPolicy
name|policy
init|=
name|connection
operator|.
name|getRedeliveryPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|500
argument_list|)
expr_stmt|;
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
literal|true
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
comment|// Send the messages
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1st"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"2nd"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TextMessage
name|m
decl_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// No delay on first rollback..
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Show subsequent re-delivery delay is incrementing.
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// The message gets redelivered after 500 ms every time since
comment|// we are not using exponential backoff.
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws Exception      */
specifier|public
name|void
name|testDLQHandling
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Receive a message with the JMS API
name|RedeliveryPolicy
name|policy
init|=
name|connection
operator|.
name|getRedeliveryPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setUseExponentialBackOff
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
literal|true
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
name|MessageConsumer
name|dlqConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Send the messages
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1st"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"2nd"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TextMessage
name|m
decl_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// The last rollback should cause the 1st message to get sent to the DLQ
name|m
operator|=
operator|(
name|TextMessage
operator|)
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
name|assertEquals
argument_list|(
literal|"2nd"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// We should be able to get the message off the DLQ now.
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|dlqConsumer
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
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

