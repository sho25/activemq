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
name|perf
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
name|Session
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|SimpleQueueTest
extends|extends
name|SimpleTopicTest
block|{
specifier|protected
name|long
name|initialConsumerDelay
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|consumerSleep
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|s
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|numberOfConsumers
operator|=
literal|1
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|PerfConsumer
name|createConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|number
parameter_list|)
throws|throws
name|JMSException
block|{
name|PerfConsumer
name|consumer
init|=
operator|new
name|PerfConsumer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setInitialDelay
argument_list|(
name|this
operator|.
name|initialConsumerDelay
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setSleepDuration
argument_list|(
name|this
operator|.
name|consumerSleep
argument_list|)
expr_stmt|;
name|boolean
name|enableAudit
init|=
name|numberOfConsumers
operator|<=
literal|1
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Enable Audit = "
operator|+
name|enableAudit
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
block|}
end_class

end_unit

