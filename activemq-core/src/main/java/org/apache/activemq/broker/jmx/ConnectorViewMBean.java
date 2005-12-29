begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ConnectorViewMBean
extends|extends
name|Service
block|{
specifier|public
name|short
name|getBackOffMultiplier
parameter_list|()
function_decl|;
specifier|public
name|long
name|getInitialRedeliveryDelay
parameter_list|()
function_decl|;
specifier|public
name|int
name|getMaximumRedeliveries
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isUseExponentialBackOff
parameter_list|()
function_decl|;
specifier|public
name|void
name|setBackOffMultiplier
parameter_list|(
name|short
name|backOffMultiplier
parameter_list|)
function_decl|;
specifier|public
name|void
name|setInitialRedeliveryDelay
parameter_list|(
name|long
name|initialRedeliveryDelay
parameter_list|)
function_decl|;
specifier|public
name|void
name|setMaximumRedeliveries
parameter_list|(
name|int
name|maximumRedeliveries
parameter_list|)
function_decl|;
specifier|public
name|void
name|setUseExponentialBackOff
parameter_list|(
name|boolean
name|useExponentialBackOff
parameter_list|)
function_decl|;
comment|/**      * Resets the statistics      */
specifier|public
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages enqueued on this connector      *       * @return the number of messages enqueued on this connector      */
specifier|public
name|long
name|getEnqueueCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages dequeued on this connector      *       * @return the number of messages dequeued on this connector      */
specifier|public
name|long
name|getDequeueCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

