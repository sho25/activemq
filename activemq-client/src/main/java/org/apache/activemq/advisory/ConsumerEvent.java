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
name|advisory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventObject
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerId
import|;
end_import

begin_comment
comment|/**  * An event when the number of consumers on a given destination changes.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConsumerEvent
extends|extends
name|EventObject
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2442156576867593780L
decl_stmt|;
specifier|private
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|private
specifier|final
name|ConsumerId
name|consumerId
decl_stmt|;
specifier|private
specifier|final
name|int
name|consumerCount
decl_stmt|;
specifier|public
name|ConsumerEvent
parameter_list|(
name|ConsumerEventSource
name|source
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|ConsumerId
name|consumerId
parameter_list|,
name|int
name|consumerCount
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
name|this
operator|.
name|consumerCount
operator|=
name|consumerCount
expr_stmt|;
block|}
specifier|public
name|ConsumerEventSource
name|getAdvisor
parameter_list|()
block|{
return|return
operator|(
name|ConsumerEventSource
operator|)
name|getSource
argument_list|()
return|;
block|}
specifier|public
name|Destination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
comment|/**      * Returns the current number of consumers active at the time this advisory was sent.      *       * Note that this is not the number of consumers active when the consumer started consuming.      * It is usually more vital to know how many consumers there are now - rather than historically      * how many there were when a consumer started. So if you create a {@link ConsumerListener}      * after many consumers have started, you will receive a ConsumerEvent for each consumer. However the      * {@link #getConsumerCount()} method will always return the current active consumer count on each event.      */
specifier|public
name|int
name|getConsumerCount
parameter_list|()
block|{
return|return
name|consumerCount
return|;
block|}
specifier|public
name|ConsumerId
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumerId
return|;
block|}
specifier|public
specifier|abstract
name|boolean
name|isStarted
parameter_list|()
function_decl|;
block|}
end_class

end_unit
