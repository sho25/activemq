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
name|broker
operator|.
name|region
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
name|EmbeddedBrokerTestSupport
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|ConstantPendingMessageLimitStrategy
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|region
operator|.
name|policy
operator|.
name|UniquePropertyMessageEvictionStrategy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|UniquePropertyMessageEvictionStrategyTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|policyEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdvisoryForDiscardingMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setTopicPrefetch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ConstantPendingMessageLimitStrategy
name|pendingMessageLimitStrategy
init|=
operator|new
name|ConstantPendingMessageLimitStrategy
argument_list|()
decl_stmt|;
name|pendingMessageLimitStrategy
operator|.
name|setLimit
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setPendingMessageLimitStrategy
argument_list|(
name|pendingMessageLimitStrategy
argument_list|)
expr_stmt|;
name|UniquePropertyMessageEvictionStrategy
name|messageEvictionStrategy
init|=
operator|new
name|UniquePropertyMessageEvictionStrategy
argument_list|()
decl_stmt|;
name|messageEvictionStrategy
operator|.
name|setPropertyName
argument_list|(
literal|"sequenceI"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setMessageEvictionStrategy
argument_list|(
name|messageEvictionStrategy
argument_list|)
expr_stmt|;
comment|// let evicted messages disappear
name|entry
operator|.
name|setDeadLetterStrategy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
specifier|final
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testEviction
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
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
decl_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Topic
name|destination
init|=
name|session
operator|.
name|createTopic
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message "
operator|+
name|i
operator|+
name|j
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setIntProperty
argument_list|(
literal|"sequenceI"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setIntProperty
argument_list|(
literal|"sequenceJ"
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|11
condition|;
name|i
operator|++
control|)
block|{
name|javax
operator|.
name|jms
operator|.
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|int
name|seqI
init|=
name|msg
operator|.
name|getIntProperty
argument_list|(
literal|"sequenceI"
argument_list|)
decl_stmt|;
name|int
name|seqJ
init|=
name|msg
operator|.
name|getIntProperty
argument_list|(
literal|"sequenceJ"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|seqI
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|seqJ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|seqJ
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|-
literal|1
argument_list|,
name|seqI
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println(msg.getIntProperty("sequenceI") + " " + msg.getIntProperty("sequenceJ"));
block|}
name|javax
operator|.
name|jms
operator|.
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

