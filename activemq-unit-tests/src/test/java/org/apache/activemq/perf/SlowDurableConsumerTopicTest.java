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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|SlowDurableConsumerTopicTest
extends|extends
name|SlowConsumerTopicTest
block|{
specifier|protected
name|PerfConsumer
index|[]
name|slowConsumers
decl_stmt|;
specifier|protected
name|int
name|numberOfSlowConsumers
init|=
literal|1
decl_stmt|;
specifier|protected
name|PerfConsumer
name|createSlowConsumer
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
return|return
operator|new
name|SlowConsumer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
literal|"durableSlowConsumer"
operator|+
name|number
argument_list|)
return|;
block|}
block|}
end_class

end_unit

