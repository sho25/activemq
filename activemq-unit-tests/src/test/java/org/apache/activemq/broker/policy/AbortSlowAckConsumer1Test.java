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
name|policy
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|AbortSlowAckConsumerStrategy
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
name|AbortSlowConsumerStrategy
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AbortSlowAckConsumer1Test
extends|extends
name|AbortSlowConsumer1Test
block|{
specifier|protected
name|long
name|maxTimeSinceLastAck
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
specifier|public
name|AbortSlowAckConsumer1Test
parameter_list|(
name|Boolean
name|abortConnection
parameter_list|,
name|Boolean
name|topic
parameter_list|)
block|{
name|super
argument_list|(
name|abortConnection
argument_list|,
name|topic
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|AbortSlowConsumerStrategy
name|createSlowConsumerStrategy
parameter_list|()
block|{
return|return
operator|new
name|AbortSlowConsumerStrategy
argument_list|()
return|;
block|}
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
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|AbortSlowAckConsumerStrategy
name|strategy
init|=
operator|new
name|AbortSlowAckConsumerStrategy
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setAbortConnection
argument_list|(
name|abortConnection
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setCheckPeriod
argument_list|(
name|checkPeriod
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setMaxSlowDuration
argument_list|(
name|maxSlowDuration
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setMaxTimeSinceLastAck
argument_list|(
name|maxTimeSinceLastAck
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setSlowConsumerStrategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setQueuePrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setTopicPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

