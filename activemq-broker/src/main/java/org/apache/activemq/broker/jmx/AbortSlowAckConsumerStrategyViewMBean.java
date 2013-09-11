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
name|jmx
package|;
end_package

begin_interface
specifier|public
interface|interface
name|AbortSlowAckConsumerStrategyViewMBean
extends|extends
name|AbortSlowConsumerStrategyViewMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current max time since last ack setting"
argument_list|)
name|long
name|getMaxTimeSinceLastAck
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"sets the duration (milliseconds) after which a consumer that doesn't ack a message will be marked as slow"
argument_list|)
name|void
name|setMaxTimeSinceLastAck
parameter_list|(
name|long
name|maxTimeSinceLastAck
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current value of the ignore idle consumers setting."
argument_list|)
name|boolean
name|isIgnoreIdleConsumers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"sets whether consumers that are idle (no dispatched messages) should be included when checking for slow acks."
argument_list|)
name|void
name|setIgnoreIdleConsumers
parameter_list|(
name|boolean
name|ignoreIdleConsumers
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current value of the ignore network connector consumers setting."
argument_list|)
name|boolean
name|isIgnoreNetworkConsumers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"sets whether consumers that are from network connector should be included when checking for slow acks."
argument_list|)
name|void
name|setIgnoreNetworkConsumers
parameter_list|(
name|boolean
name|ignoreIdleConsumers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

