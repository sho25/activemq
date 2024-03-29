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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|MessageConsumer
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
name|MessageIdList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|AbortSlowConsumer2Test
extends|extends
name|AbortSlowConsumerBase
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"isTopic({0})"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|Boolean
operator|.
name|TRUE
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|AbortSlowConsumer2Test
parameter_list|(
name|Boolean
name|isTopic
parameter_list|)
block|{
name|this
operator|.
name|topic
operator|=
name|isTopic
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testLittleSlowConsumerIsNotAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumers
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
name|consumertoAbort
init|=
name|consumers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|setProcessingDelay
argument_list|(
literal|500
argument_list|)
expr_stmt|;
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|waitForMessagesToArrive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|assertAtLeastMessagesReceived
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

