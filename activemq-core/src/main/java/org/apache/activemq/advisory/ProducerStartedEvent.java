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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
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
name|ProducerInfo
import|;
end_import

begin_comment
comment|/**  * An event when a new consumer has started.  *   * @version $Revision: 359679 $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerStartedEvent
extends|extends
name|ProducerEvent
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5088138839609391074L
decl_stmt|;
specifier|private
specifier|transient
specifier|final
name|ProducerInfo
name|consumerInfo
decl_stmt|;
specifier|public
name|ProducerStartedEvent
parameter_list|(
name|ProducerEventSource
name|source
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|ProducerInfo
name|consumerInfo
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|destination
argument_list|,
name|consumerInfo
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|this
operator|.
name|consumerInfo
operator|=
name|consumerInfo
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * @return details of the subscription      */
specifier|public
name|ProducerInfo
name|getProducerInfo
parameter_list|()
block|{
return|return
name|consumerInfo
return|;
block|}
block|}
end_class

end_unit

